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
        ResponseBuilder response = input.getResponseBuilder()
        Map<String, Object> sessionAttributes = input.getAttributesManager().getSessionAttributes()
        Player player = new Player(AlexaSdkHelper.getUserId(input))
        String responseMessage
        String repromptMessage

        if (player.name == null) {
            sessionAttributes.put(SessionAttributes.GAME_STATE, GameState.NEW_PLAYER_HEAR_RULES)
            responseMessage = Messages.NEW_PLAYER_WELCOME_MESSAGE
            repromptMessage = Messages.NEW_PLAYER_WELCOME_MESSAGE
        } else {
            sessionAttributes.put(SessionAttributes.GAME_STATE, GameState.NEW_GAME)
            sessionAttributes.put(SessionAttributes.PLAYER_ID, player.id)
            String availableCategories = Messages.getAvailableCategoryListMessage(player.id)
            responseMessage = "${Messages.EXISTING_PLAYER_WELCOME_MESSAGE} " +
                    "${Messages.CHOOSE_CATEGORY_MESSAGE} " +
                    "${availableCategories}"
            repromptMessage = "${Messages.CHOOSE_CATEGORY_MESSAGE} ${availableCategories}"
        }

        response.with {
            withSpeech(responseMessage)
            withReprompt(repromptMessage)
            withSimpleCard(Constants.SKILL_TITLE, responseMessage)
            withShouldEndSession(false)
        }

        return response.build()
    }
}
