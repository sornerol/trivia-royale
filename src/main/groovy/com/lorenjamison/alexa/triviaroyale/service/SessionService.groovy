package com.lorenjamison.alexa.triviaroyale.service

import com.lorenjamison.alexa.triviaroyale.data.Session
import com.lorenjamison.alexa.triviaroyale.data.enums.SessionStatus
import com.lorenjamison.alexa.triviaroyale.util.SessionAttributes

class SessionService {
    static Session startNewSession(long playerId) {

    }

    static Session getActiveSession(long playerId) {

    }

    static Session getSessionFromAlexaSessionAttributes(Map<String, Object> sessionAttributes) {
        Session session = new Session()
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

    static Map<String, Object> updateSessionAttributesWithSession(Map<String, Object> sessionAttributes, Session session) {
        sessionAttributes.with {
            put(SessionAttributes.SESSION_ID, session.id)
            put(SessionAttributes.QUIZ_ID, session.quizId)
            put(SessionAttributes.QUESTION_NUMBER, session.currentQuestionIndex)
            put(SessionAttributes.PLAYER_ID, session.playerId)
            put(SessionAttributes.PLAYERS_HEALTH, session.playersHealth)
        }
        sessionAttributes
    }
}
