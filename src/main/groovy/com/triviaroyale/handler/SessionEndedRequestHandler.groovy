package com.triviaroyale.handler

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.model.Response
import com.triviaroyale.util.AlexaSdkHelper
import com.triviaroyale.util.AppState
import com.triviaroyale.util.Constants
import com.triviaroyale.util.SessionAttributes
import groovy.transform.CompileStatic
import groovy.util.logging.Log

@CompileStatic
@Log
class SessionEndedRequestHandler {

    static Optional<Response> handle(HandlerInput input) {
        log.fine(Constants.ENTERING_LOG_MESSAGE)
        Map<String, Object> sessionAttributes = input.attributesManager.sessionAttributes
        if (sessionAttributes[SessionAttributes.APP_STATE] as AppState == AppState.IN_GAME) {
            AlexaSdkHelper.saveCurrentSession(sessionAttributes)
        }
        log.fine(Constants.EXITING_LOG_MESSAGE)

        null
    }

}
