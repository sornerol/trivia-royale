package com.triviaroyale.service

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.triviaroyale.data.GameState
import com.triviaroyale.data.Player
import com.triviaroyale.data.util.DynamoDBConstants
import com.triviaroyale.data.util.GameStateStatus
import com.triviaroyale.service.bean.AnswerValidationBean
import com.triviaroyale.service.exception.GameStateNotFoundException
import com.triviaroyale.util.AppState
import com.triviaroyale.util.Constants
import com.triviaroyale.util.Messages
import com.triviaroyale.util.SessionAttributes
import groovy.transform.CompileStatic
import groovy.util.logging.Log

@Log
@CompileStatic
class GameStateService extends DynamoDBAccess {

    public static final String STATUS_ATTRIBUTE = ':status'
    public static final String HASH_KEY_ATTRIBUTE = ':hk'
    public static final String SESSION_STATUS_INDEX = 'sessionStatus'

    public static final boolean CORRECT = true
    public static final boolean INCORRECT = false

    public static final Map<Integer, String> PLACE = [
            1 : 'first',
            2 : 'second',
            3 : 'third',
            4 : 'fourth',
            5 : 'fifth',
            6 : 'sixth',
            7 : 'seventh',
            8 : 'eighth',
            9 : 'ninth',
            10: 'tenth',
    ]

    GameStateService(AmazonDynamoDB dynamoDB) {
        super(dynamoDB)
    }

    static GameState getSessionFromAlexaSessionAttributes(Map<String, Object> sessionAttributes) {
        if (!sessionAttributes[SessionAttributes.SESSION_ID]) {
            return null
        }

        GameState session = new GameState()
        session.with {
            playerId = sessionAttributes[SessionAttributes.PLAYER_ID] as String
            sessionId = sessionAttributes[SessionAttributes.SESSION_ID] as String
            status = sessionAttributes[SessionAttributes.GAME_STATE] as GameStateStatus
            quizId = sessionAttributes[SessionAttributes.QUIZ_ID] as String
            questions = sessionAttributes[SessionAttributes.QUESTION_LIST] as List<String>
            currentQuestionIndex = sessionAttributes[SessionAttributes.QUESTION_NUMBER] as Integer
            secondChanceUsed = sessionAttributes[SessionAttributes.SECOND_CHANCE_USED] as Boolean
            playersHealth = sessionAttributes[SessionAttributes.PLAYERS_HEALTH] as Map<String, Integer>
            playersPerformance = sessionAttributes[SessionAttributes.PLAYERS_PERFORMANCE] as Map<String, List<Boolean>>
        }

        session
    }

    static Map<String, Object> updateGameStateSessionAttributes(Map<String, Object> sessionAttributes,
                                                                GameState gameState) {
        sessionAttributes.with {
            put(SessionAttributes.PLAYER_ID, gameState?.playerId)
            put(SessionAttributes.SESSION_ID, gameState?.sessionId)
            put(SessionAttributes.GAME_STATE, gameState?.status)
            put(SessionAttributes.QUIZ_ID, gameState?.quizId)
            put(SessionAttributes.QUESTION_LIST, gameState?.questions)
            put(SessionAttributes.QUESTION_NUMBER, gameState?.currentQuestionIndex)
            put(SessionAttributes.SECOND_CHANCE_USED, gameState?.secondChanceUsed)
            put(SessionAttributes.PLAYERS_HEALTH, gameState?.playersHealth)
            put(SessionAttributes.PLAYERS_PERFORMANCE, gameState?.playersPerformance)
        }

        sessionAttributes
    }

    static GameState initializePlayers(GameState gameState, Map<String, List<Boolean>> opponents) {
        gameState.playersHealth.put(gameState.playerId, Constants.STARTING_HEALTH)
        gameState.playersPerformance.put(gameState.playerId, [])

        opponents.each { opponent ->
            gameState.playersHealth.put(opponent.key, Constants.STARTING_HEALTH)
            gameState.playersPerformance.put(opponent.key, opponent.value)
        }

        gameState
    }

    static AnswerValidationBean processPlayersAnswer(Map<String, Object> sessionAttributes,
                                                     int playersAnswer) {
        log.fine(Constants.ENTERING_LOG_MESSAGE)
        int correctAnswerIndex = sessionAttributes[SessionAttributes.CORRECT_ANSWER_INDEX] as int
        log.info("Player's answer: $playersAnswer. Correct answer: $correctAnswerIndex")
        Boolean isPlayerCorrect = (playersAnswer == correctAnswerIndex)
        GameState oldGameState = getSessionFromAlexaSessionAttributes(sessionAttributes)
        GameState newGameState = updatePlayersHealthAfterResponse(sessionAttributes, isPlayerCorrect)

        AnswerValidationBean validation = new AnswerValidationBean()
        validation.updatedAppState = AppState.IN_GAME
        validation.validationMessage = isPlayerCorrect ? 'Correct!' :
                'Sorry, the correct answer was ' +
                        "${sessionAttributes[SessionAttributes.CORRECT_ANSWER_TEXT]}. <break time=\"500ms\"/>"

        int playersWithRightAnswer = numberOfPlayersWithAnswerType(newGameState, CORRECT)
        validation.validationMessage += " $playersWithRightAnswer out of" +
                " ${oldGameState.playersHealth.size()} players got that question right."

        newGameState.currentQuestionIndex++
        if (!isPlayerAlive(newGameState)) {
            newGameState.status = GameStateStatus.GAME_OVER
            validation.updatedAppState = AppState.NEW_GAME
            validation.validationMessage += " $Messages.HEALTH_BELOW_ZERO"
        } else if (newGameState.currentQuestionIndex >= Constants.NUMBER_OF_QUESTIONS) {
            newGameState.status = GameStateStatus.COMPLETED
            validation.updatedAppState = AppState.NEW_GAME
            int finalPlace = determinePlayersCurrentPlace(newGameState)
            validation.validationMessage +=
                    " You completed the quiz. You finished in ${PLACE[finalPlace]} place."
            if (finalPlace == 1) {
                validation.validationMessage += " $Messages.CONGRATULATIONS"
            }
        } else if (newGameState.playersHealth.size() == 1) {
            newGameState.status = GameStateStatus.COMPLETED
            validation.updatedAppState = AppState.NEW_GAME
            validation.validationMessage +=
                    " You're the last player remaining. $Messages.CONGRATULATIONS"
        } else {
            validation.validationMessage += getPlayerStatusMessage(newGameState)
        }

        validation.updatedGameState = newGameState

        log.fine(Constants.EXITING_LOG_MESSAGE)
        validation
    }

    static String getPlayerStatusMessage(GameState gameState) {
        if (isPlayerAlive(gameState)) {
            int currentPlace = determinePlayersCurrentPlace(gameState)
            return " You're currently in ${PLACE[currentPlace]}. <break time=\"500ms\"/>" +
                    "Your current health is ${gameState.playersHealth[gameState.playerId]}." +
                    "<break time=\"500ms\"/> There are ${gameState.playersHealth.size()} players remaining." +
                    '<break time="500ms"/>'
        }
        int finalPlace = gameState.playersHealth.size() + 1
        "You finished in ${PLACE[finalPlace]} place."
    }

    static int determinePlayersCurrentPlace(GameState gameState) {
        List<Integer> healthValues = gameState.playersHealth.values() as List<Integer>
        int playersHealth = gameState.playersHealth[gameState.playerId]
        Collections.sort(healthValues, Collections.reverseOrder())
        log.fine(healthValues.toString())
        healthValues.indexOf(playersHealth) + 1
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

    GameState loadGameStateById(String alexaId, String sessionId) throws GameStateNotFoundException {
        log.fine(Constants.ENTERING_LOG_MESSAGE)

        String hashKey = DynamoDBConstants.PLAYER_PREFIX + alexaId
        String rangeKey = DynamoDBConstants.SESSION_PREFIX + sessionId
        GameState gameState = mapper.load(GameState, hashKey, rangeKey)
        if (!gameState) {
            throw new GameStateNotFoundException("Could not find GameState $sessionId for player $alexaId")
        }
        gameState.playerId = gameState.playerId - DynamoDBConstants.PLAYER_PREFIX
        gameState.sessionId = gameState.sessionId - DynamoDBConstants.SESSION_PREFIX

        log.fine(Constants.EXITING_LOG_MESSAGE)

        gameState
    }

    void saveGameState(GameState gameState) {
        log.fine(Constants.ENTERING_LOG_MESSAGE)
        GameState savedGameState = gameState.clone() as GameState
        savedGameState.playerId = DynamoDBConstants.PLAYER_PREFIX + savedGameState.playerId
        savedGameState.sessionId = DynamoDBConstants.SESSION_PREFIX + savedGameState.sessionId
        mapper.save(savedGameState)
        log.fine(Constants.EXITING_LOG_MESSAGE)
    }

    protected static GameState updatePlayersHealthAfterResponse(Map<String, Object> sessionAttributes,
                                                                Boolean isPlayerCorrect)
            throws IllegalStateException {
        GameState gameState = getSessionFromAlexaSessionAttributes(sessionAttributes)
        if (!gameState.playersPerformance.containsKey(gameState.playerId)) {
            if (gameState.currentQuestionIndex != 0) {
                throw new IllegalStateException("GameState is missing player's performance history.")
            }
            gameState.playersPerformance.put(gameState.playerId, [])
        }
        gameState.playersPerformance[gameState.playerId].add(isPlayerCorrect)
        int playersWithRightAnswer = numberOfPlayersWithAnswerType(gameState, CORRECT)
        int playersWithWrongAnswer = numberOfPlayersWithAnswerType(gameState, INCORRECT)

        log.info("Correct/Incorrect counts: $playersWithRightAnswer to $playersWithWrongAnswer")
        int rightAnswerHealthAdjustment = playersWithWrongAnswer *
                Constants.CORRECT_HEALTH_ADJUSTMENT *
                (gameState.currentQuestionIndex + 1)
        int wrongAnswerHealthAdjustment = playersWithRightAnswer *
                Constants.INCORRECT_HEALTH_ADJUSTMENT *
                (gameState.currentQuestionIndex + 1)

        gameState.playersHealth.each {
            if (gameState.playersPerformance[it.key][gameState.currentQuestionIndex]) {
                it.value += rightAnswerHealthAdjustment
            } else {
                it.value -= wrongAnswerHealthAdjustment
            }
        }
        log.info("Player's health after adjustment: ${gameState.playersHealth.toString()}")
        gameState.playersHealth = gameState.playersHealth.findAll {
            it.value > 0
        }

        log.info("Player's health after eliminations: ${gameState.playersHealth.toString()}")
        gameState
    }

    protected static int numberOfPlayersWithAnswerType(GameState gameState, boolean answerType) {
        int playerCount = 0
        gameState.playersHealth.each { player ->
            if (gameState.playersPerformance[player.key][gameState.currentQuestionIndex] == answerType) {
                playerCount++
            }
        }

        playerCount
    }

    protected static boolean isPlayerAlive(GameState gameState) {
        gameState.playersHealth.containsKey(gameState.playerId)
    }

}
