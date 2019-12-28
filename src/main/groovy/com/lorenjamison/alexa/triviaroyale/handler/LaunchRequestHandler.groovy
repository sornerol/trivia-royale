package com.lorenjamison.alexa.triviaroyale.handler

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.dispatcher.request.handler.RequestHandler
import com.amazon.ask.model.LaunchRequest
import com.amazon.ask.model.Response
import com.amazon.ask.response.ResponseBuilder
import com.lorenjamison.alexa.triviaroyale.data.Player
import com.lorenjamison.alexa.triviaroyale.util.AlexaSdkHelper
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
            responseMessage = Messages.WELCOME_NEW_PLAYER + Messages.RULES + Messages.ASK_FOR_NAME
            repromptMessage = Messages.ASK_FOR_NAME
        } else {
            sessionAttributes.put(SessionAttributes.GAME_STATE, GameState.NEW_GAME)
            sessionAttributes.put(SessionAttributes.PLAYER_ID, player.id)

            responseMessage = "${Messages.WELCOME_EXISTING_PLAYER} " +
                    "${Messages.ASK_TO_START_NEW_GAME}"
            repromptMessage = Messages.ASK_TO_START_NEW_GAME
        }

        sessionAttributes.put(SessionAttributes.LAST_RESPONSE, responseMessage)
        ResponseBuilder response = AlexaSdkHelper.responseWithSimpleCard(input, responseMessage, repromptMessage)
        response.build()
    }
}
