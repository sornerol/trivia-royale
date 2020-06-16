package com.triviaroyale.util

import groovy.transform.CompileStatic

@CompileStatic
enum AppState {

    NEW_PLAYER_SETUP,
    RESUME_EXISTING_GAME,
    NEW_GAME,
    IN_GAME,
    START_OVER_REQUEST

}
