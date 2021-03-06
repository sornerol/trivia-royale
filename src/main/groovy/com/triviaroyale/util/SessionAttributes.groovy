package com.triviaroyale.util

import groovy.transform.CompileStatic

@CompileStatic
class SessionAttributes {

    public static final String APP_STATE = 'appState'
    //Player information
    public static final String PLAYER_ID = 'playerId'
    public static final String PLAYER_QUIZ_COMPLETION = 'quizCompletion'
    public static final String SECOND_CHANCES_PURCHASED = 'secondChancesPurchased'
    public static final String SECOND_CHANCES_CONSUMED = 'secondChancesConsumed'
    public static final String ACTIVE_LEADERBOARD = 'activeLeaderboard'

    //Game state information
    public static final String GAME_STATE = 'gameState'
    public static final String SESSION_ID = 'sessionId'
    public static final String QUIZ_ID = 'quizId'
    public static final String LAST_RESPONSE = 'lastResponse'
    public static final String PLAYERS_HEALTH = 'playersHealth'
    public static final String PLAYERS_PERFORMANCE = 'playersPerformance'
    public static final String SECOND_CHANCE_USED = 'secondChanceUsed'

    //Quiz information
    public static final String QUESTION_LIST = 'questionList'
    public static final String QUESTION_NUMBER = 'questionNumber'
    public static final String CORRECT_ANSWER_INDEX = 'correctAnswerIndex'
    public static final String CORRECT_ANSWER_TEXT = 'correctAnswer'

}
