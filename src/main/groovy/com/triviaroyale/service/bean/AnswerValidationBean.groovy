package com.triviaroyale.service.bean

import com.triviaroyale.data.GameState
import com.triviaroyale.util.AppState
import groovy.transform.CompileStatic

@CompileStatic
class AnswerValidationBean {

    GameState updatedGameState
    AppState updatedAppState
    String validationMessage

}
