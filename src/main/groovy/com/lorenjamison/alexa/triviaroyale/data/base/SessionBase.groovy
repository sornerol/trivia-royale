package com.lorenjamison.alexa.triviaroyale.data.base

import com.lorenjamison.alexa.triviaroyale.data.enums.SessionStatus

class SessionBase {
    long id
    SessionStatus status
    long quizId
    int currentQuestionIndex
    long playerId
    LinkedHashMap<Long, Integer> playersHealth
}
