package com.triviaroyale.handler

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.model.Response
import com.amazon.ask.response.ResponseBuilder
import com.triviaroyale.data.GameState
import com.triviaroyale.service.GameStateService
import com.triviaroyale.util.AlexaSdkHelper
import com.triviaroyale.util.Constants
import com.triviaroyale.util.Messages
import com.triviaroyale.util.SessionAttributes
import groovy.transform.CompileStatic
import groovy.util.logging.Log

@CompileStatic
@Log
class GetStatusIntentHandler {

    static Optional<Response> getStatus(HandlerInput input) {
        log.fine(Constants.ENTERING_LOG_MESSAGE)

        Map<String, Object> sessionAttributes = input.attributesManager.sessionAttributes
        GameState gameState = GameStateService.getSessionFromAlexaSessionAttributes(sessionAttributes)
        String statusMessage = GameStateService.getPlayerStatusMessage(gameState)

        String responseMessage = "$statusMessage ${sessionAttributes[SessionAttributes.LAST_RESPONSE]}"
        String repromptMessage = sessionAttributes[SessionAttributes.LAST_RESPONSE]

        ResponseBuilder response = AlexaSdkHelper.generateResponse(input, responseMessage, repromptMessage)
        log.fine(Constants.EXITING_LOG_MESSAGE)

        response.build()
    }

    static Optional<Response> notInGame(HandlerInput input) {
        log.fine(Constants.ENTERING_LOG_MESSAGE)

        Map<String, Object> sessionAttributes = input.attributesManager.sessionAttributes
        String responseMessage = "$Messages.NOT_IN_GAME ${sessionAttributes[SessionAttributes.LAST_RESPONSE]}"
        String repromptMessage = responseMessage

        ResponseBuilder response = AlexaSdkHelper.generateResponse(input, responseMessage, repromptMessage)
        log.fine(Constants.EXITING_LOG_MESSAGE)

        response.build()
    }

}
