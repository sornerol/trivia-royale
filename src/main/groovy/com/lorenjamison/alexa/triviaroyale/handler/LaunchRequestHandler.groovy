package com.lorenjamison.alexa.triviaroyale.handler

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.dispatcher.request.handler.RequestHandler
import com.amazon.ask.model.LaunchRequest
import com.amazon.ask.model.Response
import com.amazon.ask.response.ResponseBuilder
import com.lorenjamison.alexa.triviaroyale.dataobject.Category
import com.lorenjamison.alexa.triviaroyale.dataobject.Player
import com.lorenjamison.alexa.triviaroyale.service.CategoryService
import com.lorenjamison.alexa.triviaroyale.util.AlexaSdkHelper
import com.lorenjamison.alexa.triviaroyale.util.Messages
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
        String repromptMessage

        if(player.name == null) {
            sessionAttributes.put("GameState", GameState.NEW_PLAYER_INIT)
            responseMessage = Messages.NEW_PLAYER_WELCOME_MESSAGE
            repromptMessage = Messages.NEW_PLAYER_WELCOME_MESSAGE
        } else {
            sessionAttributes.put("GameState", GameState.NEW_GAME)
            String availableCategories = Messages.getAvailableCategoryListMessage(player)
            responseMessage = "${Messages.EXISTING_PLAYER_WELCOME_MESSAGE} " +
                    "${Messages.CHOOSE_CATEGORY_MESSAGE} " +
                    "${availableCategories}"
            repromptMessage = "${Messages.CHOOSE_CATEGORY_MESSAGE} ${availableCategories}"
        }

        response.with {
            withSpeech(responseMessage)
            withReprompt(repromptMessage)
            withSimpleCard(Messages.SKILL_TITLE, responseMessage)
            withShouldEndSession(false)
        }

        return response.build()
    }
}
