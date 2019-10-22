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
        ResponseBuilder response = input.getResponseBuilder()
        Map<String, Object> sessionAttributes = input.getAttributesManager().getSessionAttributes()
        String responseMessage
        String repromptMessage
        long playerId = (long) sessionAttributes[SessionAttributes.PLAYER_ID]
        String requestedCategory = AlexaSdkHelper.getSlotValue(input, AlexaSdkHelper.CATEGORY_SLOT_KEY)

        if (!CategoryService.isCategoryAvailableForPlayer(requestedCategory, playerId)) {
            String availableCategoriesMessage = CategoryService.getCategoriesAvailableForPlayer(playerId)

            responseMessage = Messages.INVALID_CATEGORY_SELECTION_MESSAGE + availableCategoriesMessage
            repromptMessage = availableCategoriesMessage
        } else {
            //TODO: Set up new game for selected category
        }

        response.with {
            withSpeech(responseMessage)
            withReprompt(repromptMessage)
            withSimpleCard(Constants.SKILL_TITLE, responseMessage)
            withShouldEndSession(false)
        }
        response.build()
    }
}
