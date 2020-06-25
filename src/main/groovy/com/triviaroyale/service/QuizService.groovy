package com.triviaroyale.service

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const
import com.triviaroyale.data.GameState
import com.triviaroyale.data.Player
import com.triviaroyale.data.Question
import com.triviaroyale.data.Quiz
import com.triviaroyale.data.util.DynamoDBConstants
import com.triviaroyale.data.util.GameStateStatus
import com.triviaroyale.util.Constants
import com.triviaroyale.util.SessionAttributes
import groovy.transform.CompileStatic

import java.security.SecureRandom

@CompileStatic
class QuizService extends DynamoDBAccess {

    public static final char FIRST_ANSWER_LETTER = 'A'

    public static final String CATEGORY_ATTRIBUTE = ':hk'
    public static final String QUIZ_ID_ATTRIBUTE = ':rk'

    QuizService(AmazonDynamoDB dynamoDB) {
        super(dynamoDB)
    }

    static String getQuizIdAsString(Quiz quiz) {
        String quizId = quiz.category + '|' + quiz.uniqueId
        quizId
    }

    static List<Tuple2<String, List<Boolean>>> getRandomPlayersForQuiz(Quiz quiz, int numberOfPlayers) {
        List<Tuple2<String, List<Boolean>>> playerPool = quiz.playerPool.asList()
        List<Tuple2<String, List<Boolean>>> selectedPlayers = []
        Collections.shuffle(playerPool)
        for (int i = 0; i < numberOfPlayers; i++) {
            selectedPlayers.add(playerPool.pop())
        }

        selectedPlayers
    }

    static Map<String, Object> updateSessionAttributesWithCurrentQuestion(Map<String, Object> sessionAttributes) {
        List<String> questionList = sessionAttributes[SessionAttributes.QUESTION_LIST] as List<String>
        Question currentQuestion = Question.fromJson(
                questionList[sessionAttributes[SessionAttributes.QUESTION_NUMBER] as int])
        SecureRandom random = new SecureRandom()
        int correctAnswerIndex = random.nextInt(currentQuestion.otherAnswers.size() + 1)
        String questionText = generateQuestionText(currentQuestion, correctAnswerIndex)

        sessionAttributes.put(SessionAttributes.LAST_RESPONSE, questionText)
        sessionAttributes.put(SessionAttributes.CORRECT_ANSWER_INDEX, correctAnswerIndex)

        sessionAttributes
    }

    Quiz loadNextAvailableQuizForPlayer(Player player, String category = Constants.GENERAL_CATEGORY) {
        Map<String, AttributeValue> attributeValues = [:]

        String categoryValue = "${DynamoDBConstants.QUIZ_PREFIX}${category}"
        String lastPlayedQuizId = player.quizCompletion[category]

        attributeValues.put(CATEGORY_ATTRIBUTE, new AttributeValue().withS(categoryValue))
        attributeValues.put(QUIZ_ID_ATTRIBUTE, new AttributeValue().withS(lastPlayedQuizId))
        String keyConditionExpression = "$DynamoDBConstants.HASH_KEY = $CATEGORY_ATTRIBUTE " +
                "and $DynamoDBConstants.RANGE_KEY > $QUIZ_ID_ATTRIBUTE"
        DynamoDBQueryExpression<Quiz> queryExpression = new DynamoDBQueryExpression<Quiz>()
                .withKeyConditionExpression(keyConditionExpression)
                .withExpressionAttributeValues(attributeValues)

        Quiz quiz = mapper.query(Quiz, queryExpression)[0] as Quiz
        if (quiz) {
            quiz.category = quiz.category - DynamoDBConstants.QUIZ_PREFIX
        }

        quiz
    }

    Quiz generateNewQuiz(List<String> questions, String playerId, String category = Constants.GENERAL_CATEGORY) {
        Quiz quiz = new Quiz()
        quiz.with {
            category = "${DynamoDBConstants.QUIZ_PREFIX}${category}"
            uniqueId = "${System.currentTimeMillis().toString()}#${playerId}"
            questionJson = questions
        }
        Queue<Tuple2<String, List<Boolean>>> playerPool = new LinkedList<>()
        for (int i = 0; i < Quiz.MAXIMUM_POOL_SIZE; i++) {
            String housePlayerId = Constants.HOUSE_PLAYER_ID_BASE + i.toString()
            List<Boolean> performance = []
            SecureRandom random = new SecureRandom()
            int correctPercentage = random.nextInt(Constants.HOUSE_PLAYER_CORRECT_PERCENTAGE)

            for (int x = 0; x < Constants.NUMBER_OF_QUESTIONS; x++) {
                performance.add(random.nextInt(correctPercentage) <= correctPercentage)
            }
            Tuple2<String, List<Boolean>> poolEntry = new Tuple2<String, List<Boolean>>(housePlayerId, performance)
            playerPool.add(poolEntry)
        }
        quiz.playerPool = playerPool
        mapper.save(quiz)
        quiz.category = quiz.category - DynamoDBConstants.QUIZ_PREFIX

        quiz
    }

    void addPerformanceToPool(GameState completedGame) {

    }

    protected static String generateQuestionText(Question question, int correctAnswerIndex) {
        int possibleAnswers = question.otherAnswers.size() + 1
        List<String> answers = []
        Collections.shuffle(question.otherAnswers)
        String answerLetter = FIRST_ANSWER_LETTER.toString()
        for (int i = 0; i < possibleAnswers; i++) {
            String formattedAnswer
            if (i == correctAnswerIndex) {
                formattedAnswer = "${answerLetter}. $question.correctAnswer\n"
            } else {
                formattedAnswer = "${answerLetter}. ${question.otherAnswers.pop()}\n"
            }
            answers[i] = formattedAnswer
            answerLetter++
        }

        String questionText = question.questionText + '\n'
        answers.each {
            answer -> questionText += answer
        }

        questionText
    }

}
