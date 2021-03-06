package com.triviaroyale.data.util

import groovy.transform.CompileStatic

@CompileStatic
class DynamoDBConstants {

    public static final String TABLE_NAME = 'TriviaRoyale'
    public static final String HASH_KEY = 'hk'
    public static final String RANGE_KEY = 'rk'

    public static final String SESSION_STATUS_KEY = 'sessionStatus'

    public static final String PLAYER_PREFIX = 'PLAYER#'
    public static final String SESSION_PREFIX = 'SESSION#'
    public static final String QUIZ_PREFIX = 'QUIZ#'
    public static final String METADATA = 'METADATA'

}
