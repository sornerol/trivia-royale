package com.triviaroyale.handler.exception

import groovy.transform.CompileStatic

@CompileStatic
class InvalidSlotException extends Exception {

    InvalidSlotException(String message) {
        super(message)
    }

}
