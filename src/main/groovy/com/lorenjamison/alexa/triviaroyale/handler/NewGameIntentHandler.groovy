package com.lorenjamison.alexa.triviaroyale.handler

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.dispatcher.request.handler.RequestHandler
import com.amazon.ask.model.Response
import com.amazon.ask.response.ResponseBuilder

import com.lorenjamison.alexa.triviaroyale.dataobject.Category
import com.lorenjamison.alexa.triviaroyale.dataobject.Player
import com.lorenjamison.alexa.triviaroyale.service.CategoryService
import com.lorenjamison.alexa.triviaroyale.util.AlexaSdkHelper
import com.lorenjamison.alexa.triviaroyale.util.Constants
import com.lorenjamison.alexa.triviaroyale.util.GameState
import com.lorenjamison.alexa.triviaroyale.util.Messages
import com.lorenjamison.alexa.triviaroyale.util.SessionAttributes

import static com.amazon.ask.request.Predicates.intentName
import static com.amazon.ask.request.Predicates.sessionAttribute

class NewGameIntentHandler implements RequestHandler {
    @Override
    boolean canHandle(HandlerInput input) {
        input.matches(intentName("NewGameIntent") & sessionAttribute(SessionAttributes.GAME_STATE, GameState.NEW_GAME))
    }

    @Override
    Optional<Response> handle(HandlerInput input) {
        Map<String, Object> sessionAttributes = input.attributesManager.sessionAttributes
        String responseMessage
        String repromptMessage
        long playerId = (long) sessionAttributes[SessionAttributes.PLAYER_ID]

        //TODO: create new game

        ResponseBuilder response = AlexaSdkHelper.responseWithSimpleCard(input, responseMessage, repromptMessage)
        response.build()
    }
}
