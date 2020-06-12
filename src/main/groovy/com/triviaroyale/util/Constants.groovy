package com.triviaroyale.util

import groovy.transform.CompileStatic

import java.util.logging.Level

@CompileStatic
class Constants {

    //TODO: Find a better way to set the default log level
    public static final Level LOG_LEVEL = Level.FINER

    public static final String SKILL_TITLE = 'Trivia Royale'
    public static final int NUMBER_OF_PLAYERS = 10
    public static final int NUMBER_OF_QUESTIONS = 10
    public static final int STARTING_HEALTH = 100

    public static final int CORRECT_HEALTH_ADJUSTMENT = 5
    public static final int INCORRECT_HEALTH_ADJUSTMENT = 10

    public static final String GENERAL_CATEGORY = 'GENERAL'
    public static final String S3_QUESTION_BUCKET = 'triviaroyale'

}
