package com.lorenjamison.triviaroyale.data.base


import com.lorenjamison.triviaroyale.data.enums.SessionStatus

class GameStateBase {
    long id
    SessionStatus status
    long quizId
    int currentQuestionIndex
    long playerId
    LinkedHashMap<Long, Integer> playersHealth
}
