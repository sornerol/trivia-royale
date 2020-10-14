package com.triviaroyale.handler

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.model.Response
import com.amazon.ask.response.ResponseBuilder
import com.triviaroyale.util.*
import groovy.transform.CompileStatic
import groovy.util.logging.Log

@CompileStatic
@Log
class CancelAndStopIntentHandler {

    static Optional<Response> handle(HandlerInput input) {
        log.fine(Constants.ENTERING_LOG_MESSAGE)

        Map<String, Object> sessionAttributes = input.attributesManager.sessionAttributes
        if (sessionAttributes[SessionAttributes.APP_STATE] as AppState == AppState.IN_GAME) {
            AlexaSdkHelper.saveCurrentSession(sessionAttributes)
        }
        ResponseBuilder responseBuilder = ResponseHelper.generateEndSessionResponse(input, Messages.EXIT_SKILL)

        log.fine(Constants.EXITING_LOG_MESSAGE)
        responseBuilder.build()
    }

}
