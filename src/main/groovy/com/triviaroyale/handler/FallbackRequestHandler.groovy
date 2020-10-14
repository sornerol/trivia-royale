package com.triviaroyale.handler

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.model.Response
import com.amazon.ask.response.ResponseBuilder
import com.triviaroyale.util.*
import groovy.transform.CompileStatic
import groovy.util.logging.Log

@CompileStatic
@Log
class FallbackRequestHandler {

    static Optional<Response> handle(HandlerInput input) {
        log.fine(Constants.ENTERING_LOG_MESSAGE)

        Map<String, Object> sessionAttributes = input.attributesManager.sessionAttributes
        String responseMessage = Messages.CANT_UNDERSTAND
        if (sessionAttributes[SessionAttributes.APP_STATE] as AppState == AppState.IN_GAME) {
            responseMessage += " $Messages.HOW_TO_ANSWER"
        }
        responseMessage += " ${sessionAttributes[SessionAttributes.LAST_RESPONSE]}"
        String repromptMessage = responseMessage

        ResponseBuilder response = ResponseHelper.generateResponse(input, responseMessage, repromptMessage)
        log.fine(Constants.EXITING_LOG_MESSAGE)

        response.build()
    }

}
