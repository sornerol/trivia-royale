package com.triviaroyale.service

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.s3.AmazonS3ClientBuilder
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

@CompileStatic
@Log
class QuizService extends DynamoDBAccess {

    public static final char FIRST_ANSWER_LETTER = 'A'

    public static final String CATEGORY_ATTRIBUTE = ':hk'
    public static final String QUIZ_ID_ATTRIBUTE = ':rk'

    public static final int ONE_HUNDRED_PERCENT = 100
    public static final int FIRST_ELEMENT = 1

    QuizService(AmazonDynamoDB dynamoDB) {
        super(dynamoDB)
    }

    static String getQuizIdAsString(Quiz quiz) {
        String quizId = quiz.category + Constants.QUIZ_ID_DELIMITER + quiz.uniqueId
        quizId
    }

    static Map<String, List<Boolean>> getRandomPlayersForQuiz(Quiz quiz, int numberOfPlayers) {
        List<String> playerPoolIds = quiz.playerPool.keySet() as List<String>
        Map<String, List<Boolean>> selectedPlayers = [:]
        Collections.shuffle(playerPoolIds)
        for (int i = 0; i < numberOfPlayers; i++) {
            String selectedPlayer = playerPoolIds.pop()
            selectedPlayers.put(selectedPlayer, quiz.playerPool[selectedPlayer])
        }

        selectedPlayers
    }

    static Map<String, Object> updateSessionAttributesWithCurrentQuestion(Map<String, Object> sessionAttributes) {
        List<String> questionList = sessionAttributes[SessionAttributes.QUESTION_LIST] as List<String>
        QuestionService questionService =
                new QuestionService(AmazonS3ClientBuilder.defaultClient(), Constants.S3_QUESTION_BUCKET)
        int currentQuestionIndex = sessionAttributes[SessionAttributes.QUESTION_NUMBER] as int

        Question currentQuestion = questionService.fetchQuestion(questionList[currentQuestionIndex])
        SecureRandom random = new SecureRandom()
        int correctAnswerIndex = random.nextInt(currentQuestion.otherAnswers.size() + 1)
        String questionText = generateQuestionText(currentQuestion, correctAnswerIndex)

        sessionAttributes.put(SessionAttributes.LAST_RESPONSE, questionText)
        sessionAttributes.put(SessionAttributes.CORRECT_ANSWER_INDEX, correctAnswerIndex)
        sessionAttributes.put(SessionAttributes.CORRECT_ANSWER_TEXT, currentQuestion.correctAnswer)

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

    Quiz generateNewQuiz(String playerId, String quizCategory = Constants.GENERAL_CATEGORY) {
        Quiz quiz = new Quiz()
        QuestionService questionService =
                new QuestionService(AmazonS3ClientBuilder.defaultClient(), Constants.S3_QUESTION_BUCKET)
        quiz.with {
            category = "${DynamoDBConstants.QUIZ_PREFIX}${quizCategory}"
            uniqueId = "${System.currentTimeMillis().toString()}#${playerId}"
            questions = questionService.fetchRandomQuestionsForCategory(Constants.NUMBER_OF_QUESTIONS)
        }
        quiz.playerPool = [:]
        for (int i = 0; i < Quiz.STARTING_POOL_SIZE; i++) {
            String housePlayerId = Constants.HOUSE_PLAYER_ID_BASE + i.toString()
            List<Boolean> performance = completePerformanceWithRandomAnswers([])

            quiz.playerPool.put(housePlayerId, performance)
        }
        mapper.save(quiz)
        quiz.category = quiz.category - DynamoDBConstants.QUIZ_PREFIX

        quiz
    }

    void addPerformanceToPool(GameState completedGame) {
        List<String> tokenizedQuizId = completedGame.quizId.tokenize(Constants.QUIZ_ID_DELIMITER)
        Quiz quiz = loadQuizByCategoryAndId(tokenizedQuizId[0], tokenizedQuizId[1])
        if (!quiz) {
            log.info("$completedGame.quizId no longer exists. Not saving player performance")
            return
        }

        log.info("Adding player performance for player ID $completedGame.playerId" +
                " to quiz ID $completedGame.quizId")
        List<Boolean> completedPlayerPerformance = completePerformanceWithRandomAnswers(
                completedGame.playersPerformance[completedGame.playerId])
        quiz.playerPool.put(completedGame.playerId, completedPlayerPerformance)
        log.fine("Player pool size for ${quiz.category} - ${quiz.uniqueId} is ${quiz.playerPool.size()}.")
        if (quiz.playerPool.size() > Quiz.MAXIMUM_POOL_SIZE) {
            log.info("Dropping old player performance in ${quiz.category} - ${quiz.uniqueId}...")
            quiz.playerPool = quiz.playerPool.drop(FIRST_ELEMENT)
            log.fine("Player pool size for ${quiz.category} - ${quiz.uniqueId} is ${quiz.playerPool.size()}.")
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
                formattedAnswer = "${answerLetter}. <break time=\"300ms\"/>" +
                        "$question.correctAnswer\n<break time=\"500ms\"/>"
            } else {
                formattedAnswer = "${answerLetter}. <break time=\"300ms\"/>" +
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

        List<Boolean> completedPerformance = performance
        int answeredQuestions = completedPerformance.size()
        for (int i = answeredQuestions; i < Constants.NUMBER_OF_QUESTIONS; i++) {
            completedPerformance.add(random.nextInt(ONE_HUNDRED_PERCENT) <= Constants.HOUSE_PLAYER_CORRECT_PERCENTAGE)
        }

        completedPerformance
    }

}
