package com.triviaroyale.service

import com.triviaroyale.data.GameState
import com.triviaroyale.data.enums.SessionStatus
import com.triviaroyale.util.SessionAttributes

class GameStateService {
    static GameState startNewGame(long playerId) {

    }

    static GameState getActiveGame(long playerId) {

    }

    static GameState getSessionFromAlexaSessionAttributes(Map<String, Object> sessionAttributes) {
        GameState session = new GameState()
        session.with {
            id = sessionAttributes[SessionAttributes.SESSION_ID] as long
            status = SessionStatus.ACTIVE   //we don't need to store the session status since all incoming sessions are active
            quizId = sessionAttributes[SessionAttributes.QUIZ_ID] as long
            currentQuestionIndex = sessionAttributes[SessionAttributes.QUESTION_NUMBER] as int
            playerId = sessionAttributes[SessionAttributes.PLAYER_ID] as long
            playersHealth = sessionAttributes[SessionAttributes.PLAYERS_HEALTH] as LinkedHashMap<Long, Integer>
        }
        session
    }

    static Map<String, Object> updateSessionAttributesWithGameState(Map<String, Object> sessionAttributes, GameState gameState) {
        sessionAttributes.with {
            put(SessionAttributes.SESSION_ID, gameState.id)
            put(SessionAttributes.QUIZ_ID, gameState.quizId)
            put(SessionAttributes.QUESTION_NUMBER, gameState.currentQuestionIndex)
            put(SessionAttributes.PLAYER_ID, gameState.playerId)
            put(SessionAttributes.PLAYERS_HEALTH, gameState.playersHealth)
        }
        sessionAttributes
    }
}
