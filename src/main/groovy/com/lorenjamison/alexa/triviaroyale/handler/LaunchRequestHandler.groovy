package com.lorenjamison.alexa.triviaroyale.handler

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.dispatcher.request.handler.RequestHandler
import com.amazon.ask.model.LaunchRequest
import com.amazon.ask.model.Response
import com.amazon.ask.response.ResponseBuilder
import com.lorenjamison.alexa.triviaroyale.dataobject.Player
import com.lorenjamison.alexa.triviaroyale.util.AlexaSdkHelper
import com.lorenjamison.alexa.triviaroyale.util.Constants
import com.lorenjamison.alexa.triviaroyale.util.GameState

import static com.amazon.ask.request.Predicates.requestType

class LaunchRequestHandler implements RequestHandler{

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

        if(player.name == null) {
            sessionAttributes.put("GameState", GameState.NEW_PLAYER_INIT)
            responseMessage = Constants.NEW_PLAYER_WELCOME_MESSAGE
        } else {
            sessionAttributes.put("GameState", GameState.NEW_GAME)
            responseMessage = "${Constants.EXISTING_PLAYER_WELCOME_MESSAGE} ${Constants.CHOOSE_CATEGORY_MESSAGE}"
            //TODO: Get list of categories available to the player
        }

        response.with {
            withSpeech(responseMessage)
            withReprompt(responseMessage)
            withSimpleCard(Constants.SKILL_TITLE, responseMessage)
            withShouldEndSession(false)
        }

        return response.build()
    }
}
