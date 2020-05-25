package com.triviaroyale.service

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.triviaroyale.data.GameState
import com.triviaroyale.data.util.DynamoDBConstants
import com.triviaroyale.data.util.GameStateStatus
import com.triviaroyale.util.Constants
import com.triviaroyale.util.SessionAttributes
import groovy.transform.CompileStatic

@CompileStatic
class GameStateService extends DynamoDBAccess {

    public static final String STATUS_ATTRIBUTE = ':status'
    public static final String HASH_KEY_ATTRIBUTE = ':hk'
    public static final String SESSION_STATUS_INDEX = 'sessionStatus'

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

    GameState loadActiveGameState(String alexaId) {
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

        retrievedGameState
    }

    void saveGameState(GameState gameState) {
        gameState.playerId = DynamoDBConstants.PLAYER_PREFIX + gameState.playerId
        gameState.sessionId = DynamoDBConstants.SESSION_PREFIX + gameState.sessionId
        mapper.save(gameState)
    }

}
