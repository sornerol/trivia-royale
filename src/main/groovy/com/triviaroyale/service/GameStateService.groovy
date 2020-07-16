package com.triviaroyale.service

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.triviaroyale.data.GameState
import com.triviaroyale.data.Question
import com.triviaroyale.data.util.DynamoDBConstants
import com.triviaroyale.data.util.GameStateStatus
import com.triviaroyale.util.AnswerValidationBean
import com.triviaroyale.util.AppState
import com.triviaroyale.util.Constants
import com.triviaroyale.util.SessionAttributes
import groovy.transform.CompileStatic
import groovy.util.logging.Log

@Log
@CompileStatic
class GameStateService extends DynamoDBAccess {

    public static final String STATUS_ATTRIBUTE = ':status'
    public static final String HASH_KEY_ATTRIBUTE = ':hk'
    public static final String SESSION_STATUS_INDEX = 'sessionStatus'

    public static final Map<Integer, String> PLACE = [
            1:'first',
            2:'second',
            3:'third',
            4:'fourth',
            5:'fifth',
            6:'sixth',
            7:'seventh',
            8:'eighth',
            9:'ninth',
            10:'tenth',
    ]

    GameStateService(AmazonDynamoDB dynamoDB) {
        super(dynamoDB)
    }

    static GameState getSessionFromAlexaSessionAttributes(Map<String, Object> sessionAttributes) {
        GameState session = new GameState()
        session.with {
            playerId = sessionAttributes[SessionAttributes.PLAYER_ID] as String
            sessionId = sessionAttributes[SessionAttributes.SESSION_ID] as String
            status = sessionAttributes[SessionAttributes.GAME_STATE] as GameStateStatus
            quizId = sessionAttributes[SessionAttributes.QUIZ_ID] as String
            questions = sessionAttributes[SessionAttributes.QUESTION_LIST] as List<String>
            currentQuestionIndex = sessionAttributes[SessionAttributes.QUESTION_NUMBER] as int
            playersHealth = sessionAttributes[SessionAttributes.PLAYERS_HEALTH] as Map<String, Integer>
            playersPerformance = sessionAttributes[SessionAttributes.PLAYERS_PERFORMANCE] as Map<String, List<Boolean>>
        }
        session
    }

    static Map<String, Object> updateGameStateSessionAttributes(Map<String, Object> sessionAttributes,
                                                                GameState gameState) {
        sessionAttributes.with {
            put(SessionAttributes.PLAYER_ID, gameState.playerId)
            put(SessionAttributes.SESSION_ID, gameState.sessionId)
            put(SessionAttributes.GAME_STATE, gameState.status)
            put(SessionAttributes.QUIZ_ID, gameState.quizId)
            put(SessionAttributes.QUESTION_LIST, gameState.questions)
            put(SessionAttributes.QUESTION_NUMBER, gameState.currentQuestionIndex)
            put(SessionAttributes.PLAYERS_HEALTH, gameState.playersHealth)
            put(SessionAttributes.PLAYERS_PERFORMANCE, gameState.playersPerformance)
        }

        sessionAttributes
    }

    static GameState initializePlayers(GameState gameState, List<Tuple2<String, List<Boolean>>> opponents) {
        opponents.each { opponent ->
            gameState.playersHealth.put(opponent.first, Constants.STARTING_HEALTH)
            gameState.playersPerformance.put(opponent.first, opponent.second)
        }

        gameState
    }

    static AnswerValidationBean processPlayersAnswer(GameState gameState,
                                                     int correctAnswerIndex,
                                                     String playersAnswer) {
        Question currentQuestion = Question.fromJson(gameState.questions[gameState.currentQuestionIndex])
        String correctAnswerLetter = QuizService.FIRST_ANSWER_LETTER + correctAnswerIndex
        Boolean isPlayerCorrect = (playersAnswer.toUpperCase() == correctAnswerLetter ||
                playersAnswer.toUpperCase() == currentQuestion.correctAnswer.toUpperCase())
        GameState newGameState = updatePlayersHealthAfterResponse(gameState, isPlayerCorrect)

        AnswerValidationBean validation = new AnswerValidationBean()
        validation.updatedAppState = AppState.IN_GAME
        if (isPlayerCorrect) {
            validation.validationMessage = 'Correct!'
        } else {
            validation.validationMessage = "Sorry, the correct answer was $currentQuestion.correctAnswer."
        }

        newGameState.currentQuestionIndex++

        if (!newGameState.playersHealth.containsKey(newGameState.playerId)) {
            newGameState.status = GameStateStatus.GAME_OVER
            validation.updatedAppState = AppState.NEW_GAME
            int finalPlace = newGameState.playersHealth.size() + 1
            validation.validationMessage +=
                    " Uh-oh, you've been eliminated. You finished in ${PLACE[finalPlace]} place."
        } else if (newGameState.currentQuestionIndex >= Constants.NUMBER_OF_QUESTIONS) {
            newGameState.status = GameStateStatus.COMPLETED
            validation.updatedAppState = AppState.NEW_GAME
            int finalPlace = determinePlayersCurrentPlace(newGameState)
            validation.validationMessage +=
                    " You completed the quiz. You finished in ${PLACE[finalPlace]} place."
            if (finalPlace == 1) {
                validation.validationMessage += ' Congratulations on your win!'
            }
        } else if (newGameState.playersHealth.size() == 1) {
            newGameState.status = GameStateStatus.COMPLETED
            validation.updatedAppState = AppState.NEW_GAME
            validation.validationMessage +=
                    " Congratulations! You're the last player remaining."
        }
        else {
            int currentPlace = determinePlayersCurrentPlace(newGameState)
            validation.validationMessage += " You're currently in ${PLACE[currentPlace]}. " +
                    "Your current health is ${newGameState.playersHealth[newGameState.playerId]}"
        }

        validation.updatedGameState = newGameState

        validation
    }

    GameState loadActiveGameState(String alexaId) {
        log.fine(Constants.ENTERING_LOG_MESSAGE)

        Map<String, AttributeValue> attributeValues = [:]
        attributeValues.put(STATUS_ATTRIBUTE, new AttributeValue().withS(GameStateStatus.ACTIVE as String))
        attributeValues.put(HASH_KEY_ATTRIBUTE, new AttributeValue().withS(DynamoDBConstants.PLAYER_PREFIX + alexaId))
        String keyConditionExpression = "$DynamoDBConstants.HASH_KEY = $HASH_KEY_ATTRIBUTE " +
                "and $SESSION_STATUS_INDEX = $STATUS_ATTRIBUTE"
        DynamoDBQueryExpression<GameState> queryExpression = new DynamoDBQueryExpression<GameState>()
                .withIndexName(SESSION_STATUS_INDEX)
                .withKeyConditionExpression(keyConditionExpression)
                .withExpressionAttributeValues(attributeValues)

        GameState retrievedGameState = mapper.query(GameState, queryExpression)[0] as GameState
        if (retrievedGameState) {
            retrievedGameState.playerId = retrievedGameState.playerId - DynamoDBConstants.PLAYER_PREFIX
            retrievedGameState.sessionId = retrievedGameState.sessionId - DynamoDBConstants.SESSION_PREFIX
        }
        log.fine(Constants.EXITING_LOG_MESSAGE)

        retrievedGameState
    }

    void saveGameState(GameState gameState) {
        log.fine(Constants.ENTERING_LOG_MESSAGE)
        GameState savedGameState = gameState.clone() as GameState
        savedGameState.playerId = DynamoDBConstants.PLAYER_PREFIX + savedGameState.playerId
        savedGameState.sessionId = DynamoDBConstants.SESSION_PREFIX + savedGameState.sessionId
        mapper.save(savedGameState)
        log.fine(Constants.EXITING_LOG_MESSAGE)
    }

    protected static GameState updatePlayersHealthAfterResponse(GameState gameState, Boolean isPlayerCorrect) {
        log.fine("Player ID: $gameState.playerId")
        log.fine("Was player correct? $isPlayerCorrect")
        log.fine(gameState.playersPerformance.toString())
        gameState.playersPerformance[gameState.playerId].add(isPlayerCorrect)
        int playersWithRightAnswer = 0
        int playersWithWrongAnswer = 0
        gameState.playersHealth.each { player ->
            gameState.playersPerformance[player.key][gameState.currentQuestionIndex] ?
                    playersWithRightAnswer++ : playersWithWrongAnswer++
        }

        int rightAnswerHealthAdjustment = playersWithWrongAnswer * Constants.CORRECT_HEALTH_ADJUSTMENT
        int wrongAnswerHealthAdjustment = playersWithRightAnswer * Constants.INCORRECT_HEALTH_ADJUSTMENT

        gameState.playersHealth.each {
            if (gameState.playersPerformance[it.key][gameState.currentQuestionIndex]) {
                it.value += rightAnswerHealthAdjustment
            } else {
                it.value -= wrongAnswerHealthAdjustment
            }
        }

        gameState.playersHealth = gameState.playersHealth.findAll {
            it.value >= 0
        }

        gameState
    }

    protected static int determinePlayersCurrentPlace(GameState gameState) {
        List<Integer> healthValues = gameState.playersHealth.values() as List<Integer>
        int playersHealth = gameState.playersHealth[gameState.playerId]
        Collections.sort(healthValues)
        healthValues.indexOf(playersHealth) + 1
    }

}
