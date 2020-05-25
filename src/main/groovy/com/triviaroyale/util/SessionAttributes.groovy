package com.triviaroyale.util

import groovy.transform.CompileStatic

@CompileStatic
class SessionAttributes {

    //Player information
    public static final String PLAYER_ID = 'playerId'
    public static final String PLAYER_NAME = 'playerName'
    public static final String PLAYER_QUIZ_COMPLETION = 'quizCompletion'

    //Game state information
    public static final String GAME_STATE = 'gameState'
    public static final String SESSION_ID = 'sessionId'
    public static final String QUIZ_ID = 'quizId'
    public static final String LAST_RESPONSE = 'lastResponse'
    public static final String PLAYERS_HEALTH = 'playersHealth'
    public static final String PLAYERS_PERFORMANCE = 'playersPerformance'

    //Quiz information
    public static final String QUESTION_LIST = 'questionList'
    public static final String QUESTION_NUMBER = 'questionNumber'
    public static final String CORRECT_ANSWER_INDEX = 'correctAnswerIndex'

}
