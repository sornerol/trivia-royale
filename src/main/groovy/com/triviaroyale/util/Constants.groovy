package com.triviaroyale.util

import groovy.transform.CompileStatic

@CompileStatic
class Constants {

    public static final String REDIS_URL = 'trivia-royale.cs5jjy.ng.0001.use1.cache.amazonaws.com'

    public static final String ENTERING_LOG_MESSAGE = 'ENTERING'
    public static final String EXITING_LOG_MESSAGE = 'EXITING'

    public static final String HOUSE_PLAYER_ID_BASE = '__TRIVIAROYALE__'
    public static final int HOUSE_PLAYER_CORRECT_PERCENTAGE = 45

    public static final int NUMBER_OF_PLAYERS = 10
    public static final int NUMBER_OF_QUESTIONS = 10
    public static final int STARTING_HEALTH = 100

    public static final int CORRECT_HEALTH_ADJUSTMENT = 2
    public static final int INCORRECT_HEALTH_ADJUSTMENT = 5

    public static final String GENERAL_CATEGORY = 'GENERAL'
    public static final String CATEGORY_PROGRESS_INITIALIZER = '!'

    public static final String S3_QUESTION_BUCKET = 'triviaroyale'

    public static final String QUIZ_ID_DELIMITER = '|'

}
