package com.triviaroyale.service

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.triviaroyale.data.GameState
import com.triviaroyale.data.Player
import com.triviaroyale.data.Question
import com.triviaroyale.data.Quiz
import com.triviaroyale.data.util.DynamoDBConstants
import com.triviaroyale.util.Constants
import com.triviaroyale.util.SessionAttributes
import groovy.transform.CompileStatic
import groovy.util.logging.Log

import java.security.SecureRandom

@Log
@CompileStatic
class QuizService extends DynamoDBAccess {

    public static final char FIRST_ANSWER_LETTER = 'A'

    public static final String CATEGORY_ATTRIBUTE = ':hk'
    public static final String QUIZ_ID_ATTRIBUTE = ':rk'

    public static final int ONE_HUNDRED_PERCENT = 100
    public static final int FIRST_ELEMENT = 0

    QuizService(AmazonDynamoDB dynamoDB) {
        super(dynamoDB)
    }

    static String getQuizIdAsString(Quiz quiz) {
        String quizId = quiz.category + '|' + quiz.uniqueId
        quizId
    }

    static List<Tuple2<String, List<Boolean>>> getRandomPlayersForQuiz(Quiz quiz, int numberOfPlayers) {
        List<Tuple2<String, List<Boolean>>> playerPool = quiz.playerPool
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

    Quiz generateNewQuiz(List<String> questions, String playerId, String quizCategory = Constants.GENERAL_CATEGORY) {
        Quiz quiz = new Quiz()
        quiz.with {
            category = "${DynamoDBConstants.QUIZ_PREFIX}${quizCategory}"
            uniqueId = "${System.currentTimeMillis().toString()}#${playerId}"
            questionJson = questions
        }
        List<Tuple2<String, List<Boolean>>> playerPool = []
        for (int i = 0; i < Quiz.MAXIMUM_POOL_SIZE; i++) {
            String housePlayerId = Constants.HOUSE_PLAYER_ID_BASE + i.toString()
            List<Boolean> performance = completePerformanceWithRandomAnswers([])

            Tuple2<String, List<Boolean>> poolEntry = new Tuple2<String, List<Boolean>>(housePlayerId, performance)
            playerPool.add(poolEntry)
        }
        quiz.playerPool = playerPool
        mapper.save(quiz)
        quiz.category = quiz.category - DynamoDBConstants.QUIZ_PREFIX

        quiz
    }

    void addPerformanceToPool(GameState completedGame) {
        List<String> tokenizedQuizId = completedGame.quizId.tokenize(Constants.QUIZ_ID_DELIMITER)
        Quiz quiz = loadQuizByCategoryAndId(tokenizedQuizId[0], tokenizedQuizId[1])

        log.fine('Completed game player ID: ' + completedGame.playerId)
        log.fine('Player\'s performance: ' + completedGame.playersPerformance[completedGame.playerId])
        
        List<Boolean> completedPlayerPerformance = completePerformanceWithRandomAnswers(
                completedGame.playersPerformance[completedGame.playerId])
        quiz.playerPool.add(new Tuple2<String, List<Boolean>>(completedGame.playerId, completedPlayerPerformance))
        if (quiz.playerPool.size() > Quiz.MAXIMUM_POOL_SIZE) {
            quiz.playerPool.remove(FIRST_ELEMENT)
        }
        quiz.category = DynamoDBConstants.QUIZ_PREFIX + quiz.category
        mapper.save(quiz)
    }

    Quiz loadQuizByCategoryAndId(String category, String id) {
        String hashKey = DynamoDBConstants.QUIZ_PREFIX + category
        Quiz quiz = mapper.load(Quiz, hashKey, id)
        if (quiz) {
            quiz.category = quiz.category - DynamoDBConstants.QUIZ_PREFIX
        }
        quiz
    }

    protected static String generateQuestionText(Question question, int correctAnswerIndex) {
        int possibleAnswers = question.otherAnswers.size() + 1
        List<String> answers = []
        Collections.shuffle(question.otherAnswers)
        String answerLetter = FIRST_ANSWER_LETTER
        for (int i = 0; i < possibleAnswers; i++) {
            String formattedAnswer
            if (i == correctAnswerIndex) {
                formattedAnswer = "<emphasis>${answerLetter}.</emphasis> " +
                        "$question.correctAnswer\n<break time=\"500ms\"/>"
            } else {
                formattedAnswer = "<emphasis>${answerLetter}.</emphasis> " +
                        "${question.otherAnswers.pop()}\n<break time=\"500ms\"/>"
            }
            answers[i] = formattedAnswer
            answerLetter++
        }

        String questionText = question.questionText + '<break time="500ms"/>\n'
        answers.each {
            answer -> questionText += answer
        }

        questionText
    }

    protected static List<Boolean> completePerformanceWithRandomAnswers(List<Boolean> performance) {
        SecureRandom random = new SecureRandom()
        log.fine('Performance: ' + performance.toString())
        List<Boolean> completedPerformance = performance
        int answeredQuestions = completedPerformance.size()
        for (int i = answeredQuestions; i < Constants.NUMBER_OF_QUESTIONS; i++) {
            completedPerformance.add(random.nextInt(ONE_HUNDRED_PERCENT) <= Constants.HOUSE_PLAYER_CORRECT_PERCENTAGE)
        }

        completedPerformance
    }

}
