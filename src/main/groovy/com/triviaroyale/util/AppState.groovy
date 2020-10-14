package com.triviaroyale.util

import groovy.transform.CompileStatic

@CompileStatic
enum AppState {

    RESUME_EXISTING_GAME,
    NEW_GAME,
    IN_GAME,
    START_OVER_REQUEST,
    ASK_TO_PLAY_TRIVIA_ROYALE,
    ASK_TO_USE_SECOND_CHANCE

}
