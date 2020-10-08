package com.triviaroyale.service.exception

import groovy.transform.CompileStatic

@CompileStatic
class GameStateNotFoundException extends Exception {

    GameStateNotFoundException(String message) {
        super(message)
    }

}
