package com.triviaroyale.util

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.model.Response
import com.amazon.ask.response.ResponseBuilder
import groovy.transform.CompileStatic

@CompileStatic
class ResponseHelper {

    static Optional<Response> askToStartNewGame(HandlerInput input, String preText) {
        Map<String, Object> sessionAttributes = input.attributesManager.sessionAttributes
        String responseMessage = preText ? "$preText $Messages.ASK_TO_START_NEW_GAME" : Messages.ASK_TO_START_NEW_GAME
        String repromptMessage = Messages.ASK_TO_START_NEW_GAME
        sessionAttributes.put(SessionAttributes.LAST_RESPONSE, responseMessage)
        sessionAttributes.put(SessionAttributes.APP_STATE, AppState.NEW_GAME)

        input.attributesManager.sessionAttributes = sessionAttributes
        generateResponse(input, responseMessage, repromptMessage).build()
    }

    static ResponseBuilder generateResponse(HandlerInput input,
                                            String responseMessage,
                                            String repromptMessage) {
        ResponseBuilder responseBuilder = input.responseBuilder
        responseBuilder = responseBuilder
                .withSpeech(responseMessage)
                .withReprompt(repromptMessage)
                .withShouldEndSession(false)
        responseBuilder
    }

    static ResponseBuilder generateEndSessionResponse(HandlerInput input,
                                                      String responseMessage) {
        ResponseBuilder responseBuilder = input.responseBuilder
        responseBuilder = responseBuilder
                .withSpeech(responseMessage)
                .withShouldEndSession(true)
        responseBuilder
    }

    static Optional<Response> endSessionWithoutSpeech(HandlerInput input) {
        ResponseBuilder responseBuilder = input.responseBuilder
        responseBuilder.withShouldEndSession(true).build()
    }

    static Optional<Response> informationalResponse(HandlerInput input, String preText = null) {
        Map<String, Object> sessionAttributes = input.attributesManager.sessionAttributes
        if (!sessionAttributes[SessionAttributes.APP_STATE]) {
            sessionAttributes.put(SessionAttributes.LAST_RESPONSE, Messages.ASK_TO_PLAY_TRIVIA_ROYALE)
            sessionAttributes.put(SessionAttributes.APP_STATE, AppState.ASK_TO_PLAY_TRIVIA_ROYALE)
        }
        String responseMessage = sessionAttributes[SessionAttributes.LAST_RESPONSE]
        if (preText) {
            responseMessage = "$preText $responseMessage"
        }
        String repromptMessage = sessionAttributes[SessionAttributes.LAST_RESPONSE]

        ResponseBuilder response = generateResponse(input, responseMessage, repromptMessage)

        response.build()
    }

}
