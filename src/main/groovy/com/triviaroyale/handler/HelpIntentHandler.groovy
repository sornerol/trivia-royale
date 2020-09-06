package com.triviaroyale.handler

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.model.Response
import com.amazon.ask.response.ResponseBuilder
import com.triviaroyale.util.*
import groovy.transform.CompileStatic
import groovy.util.logging.Log

@CompileStatic
@Log
class HelpIntentHandler {

    static Optional<Response> handle(HandlerInput input) {
        log.fine(Constants.ENTERING_LOG_MESSAGE)

        Map<String, Object> sessionAttributes = input.attributesManager.sessionAttributes
        String responseMessage = Messages.HELP_MESSAGE
        switch (sessionAttributes[SessionAttributes.APP_STATE]) {
            case AppState.NEW_GAME.toString():
                sessionAttributes.put(SessionAttributes.LAST_RESPONSE, Messages.ASK_TO_START_NEW_GAME)
                break
            case AppState.RESUME_EXISTING_GAME.toString():
                sessionAttributes.put(SessionAttributes.LAST_RESPONSE, Messages.ASK_TO_RESUME_GAME)
                break
        }
        responseMessage += " ${sessionAttributes[SessionAttributes.LAST_RESPONSE]}"
        String repromptMessage = sessionAttributes[SessionAttributes.LAST_RESPONSE]

        ResponseBuilder response = AlexaSdkHelper.generateResponse(input, responseMessage, repromptMessage)
        log.fine(Constants.EXITING_LOG_MESSAGE)

        response.build()
    }

}
