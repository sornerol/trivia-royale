package com.triviaroyale.service

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.triviaroyale.data.GameState
import com.triviaroyale.data.util.DynamoDBConstants
import com.triviaroyale.data.util.GameStateStatus
import com.triviaroyale.util.SessionAttributes

class GameStateService extends DynamoDBAccess {

    GameStateService(AmazonDynamoDB dynamoDB) {
        super(dynamoDB)
    }
    
    GameState loadActiveGameState(String alexaId) {

    }

    void saveGameState(GameState gameState) {

    }

    static GameState getSessionFromAlexaSessionAttributes(Map<String, Object> sessionAttributes) {
        GameState session = new GameState()
        session.with {
            playerId = sessionAttributes[SessionAttributes.PLAYER_ID] as String
            sessionId = sessionAttributes[SessionAttributes.SESSION_ID] as String
            status = GameStateStatus.ACTIVE  //we don't need to store the session status since all incoming sessions are active
            quizId = sessionAttributes[SessionAttributes.QUIZ_ID] as String
            currentQuestionIndex = sessionAttributes[SessionAttributes.QUESTION_NUMBER] as int
            playersHealth = sessionAttributes[SessionAttributes.PLAYERS_HEALTH] as LinkedHashMap<String, Integer>
        }
        session
    }

    static Map<String, Object> updateSessionAttributesWithGameState(Map<String, Object> sessionAttributes, GameState gameState) {
        sessionAttributes.with {
            //We don't store GameState.status since all sessions using this method should be 'ACTIVE'
            put(SessionAttributes.PLAYER_ID, gameState.playerId)
            put(SessionAttributes.SESSION_ID, gameState.sessionId)
            put(SessionAttributes.QUIZ_ID, gameState.quizId)
            put(SessionAttributes.QUESTION_NUMBER, gameState.currentQuestionIndex)
            put(SessionAttributes.PLAYERS_HEALTH, gameState.playersHealth)
        }
        sessionAttributes
    }

}
