package com.lorenjamison.alexa.triviaroyale.service


import com.lorenjamison.alexa.triviaroyale.util.SessionAttributes

class AnswerService {
    static boolean checkAnswer(String answer, Map<String, Object> sessionAttributes) {
        long playerId = (long) sessionAttributes[SessionAttributes.PLAYER_ID]
        long gameId = (long) sessionAttributes[SessionAttributes.SESSION_ID]
        int questionNumber = (int) sessionAttributes[SessionAttributes.QUESTION_NUMBER]

    }
}
