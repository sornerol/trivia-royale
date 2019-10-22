package com.lorenjamison.alexa.triviaroyale.handler

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.dispatcher.request.handler.RequestHandler
import com.amazon.ask.model.LaunchRequest
import com.amazon.ask.model.Response
import com.amazon.ask.response.ResponseBuilder
import com.lorenjamison.alexa.triviaroyale.dataobject.Player
import com.lorenjamison.alexa.triviaroyale.util.AlexaSdkHelper
import com.lorenjamison.alexa.triviaroyale.util.Constants
import com.lorenjamison.alexa.triviaroyale.util.Messages
import com.lorenjamison.alexa.triviaroyale.util.GameState
import com.lorenjamison.alexa.triviaroyale.util.SessionAttributes

import static com.amazon.ask.request.Predicates.requestType

class LaunchRequestHandler implements RequestHandler {

    @Override
    boolean canHandle(HandlerInput input) {
        input.matches(requestType(LaunchRequest.class))
    }

    @Override
    Optional<Response> handle(HandlerInput input) {
        Map<String, Object> sessionAttributes = input.attributesManager.sessionAttributes
        Player player = new Player(AlexaSdkHelper.getUserId(input))
        String responseMessage
        String repromptMessage

        if (player.name == null) {
            sessionAttributes.put(SessionAttributes.GAME_STATE, GameState.NEW_PLAYER_SETUP)
            responseMessage = Messages.NEW_PLAYER_WELCOME_MESSAGE + Messages.RULES_MESSAGE + Messages.ASK_FOR_NAME_MESSAGE
            repromptMessage = Messages.ASK_FOR_NAME_MESSAGE
        } else {
            sessionAttributes.put(SessionAttributes.GAME_STATE, GameState.NEW_GAME)
            sessionAttributes.put(SessionAttributes.PLAYER_ID, player.id)

            responseMessage = "${Messages.EXISTING_PLAYER_WELCOME_MESSAGE} " +
                    "${Messages.ASK_TO_START_NEW_GAME_MESSAGE}"
            repromptMessage = Messages.ASK_TO_START_NEW_GAME_MESSAGE
        }

        sessionAttributes.put(SessionAttributes.LAST_RESPONSE, responseMessage)
        ResponseBuilder response = AlexaSdkHelper.responseWithSimpleCard(input, responseMessage, repromptMessage)
        response.build()
    }
}
