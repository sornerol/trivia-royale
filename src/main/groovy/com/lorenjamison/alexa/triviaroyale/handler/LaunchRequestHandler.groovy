package com.lorenjamison.alexa.triviaroyale.handler

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.dispatcher.request.handler.RequestHandler
import com.amazon.ask.model.LaunchRequest
import com.amazon.ask.model.Response
import com.amazon.ask.response.ResponseBuilder
import com.lorenjamison.alexa.triviaroyale.dataobject.Player
import com.lorenjamison.alexa.triviaroyale.util.AlexaSdkHelper
import com.lorenjamison.alexa.triviaroyale.util.Constants

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

        if(player.name == null) {
            sessionAttributes.put("GameState", "STATE_SETUP")
            response.with {
                withSpeech(Constants.NEW_PLAYER_WELCOME_MESSAGE)
                withReprompt(Constants.HELP_MESSAGE)
                withSimpleCard(Constants.SKILL_TITLE, Constants.NEW_PLAYER_WELCOME_MESSAGE)
                withShouldEndSession(false)
            }
        } else {
            sessionAttributes.put("GameState", "STATE_NEWGAME")
            response.with {
                withSpeech(Constants.EXISTING_PLAYER_WELCOME_MESSAGE)
                withReprompt(Constants.HELP_MESSAGE)
                withSimpleCard(Constants.SKILL_TITLE, Constants.EXISTING_PLAYER_WELCOME_MESSAGE)
                withShouldEndSession(false)
            }
        }

        return response.build()
    }
}
