package com.lorenjamison.alexa.triviaroyale.handler

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.dispatcher.request.handler.RequestHandler
import com.amazon.ask.model.IntentRequest
import com.amazon.ask.model.Response
import com.amazon.ask.model.Slot
import com.amazon.ask.response.ResponseBuilder
import com.lorenjamison.alexa.triviaroyale.dataobject.Player
import com.lorenjamison.alexa.triviaroyale.dataobject.base.PlayerBase
import com.lorenjamison.alexa.triviaroyale.service.CategoryService
import com.lorenjamison.alexa.triviaroyale.util.AlexaSdkHelper
import com.lorenjamison.alexa.triviaroyale.util.Constants
import com.lorenjamison.alexa.triviaroyale.util.GameState
import com.lorenjamison.alexa.triviaroyale.util.Messages
import com.lorenjamison.alexa.triviaroyale.util.SessionAttributes

import static com.amazon.ask.request.Predicates.intentName
import static com.amazon.ask.request.Predicates.sessionAttribute

class CreatePlayerIntentHandler implements RequestHandler {
    static final String NAME_SLOT_KEY = "name"
    @Override
    boolean canHandle(HandlerInput input) {
        input.matches(intentName("NewPlayerIntent") & sessionAttribute(SessionAttributes.GAME_STATE, GameState.NEW_PLAYER_SETUP))
    }

    @Override
    Optional<Response> handle(HandlerInput input) {
        ResponseBuilder response = input.getResponseBuilder()
        Map<String, Object> sessionAttributes = input.getAttributesManager().getSessionAttributes()
        PlayerBase newPlayerBase = new PlayerBase()
        newPlayerBase.with {
            isHousePlayer = false
            name = getPlayerName((IntentRequest) input.requestEnvelope.request)
            alexaId = AlexaSdkHelper.getUserId(input)
        }
        Player newPlayer = new Player(newPlayerBase)

        sessionAttributes.put(SessionAttributes.GAME_STATE, GameState.NEW_GAME)
        String responseMessage = Messages.CHOOSE_CATEGORY_MESSAGE
        String availableCategories = CategoryService.getCategoriesAvailableForPlayer(newPlayer)
        responseMessage += availableCategories
        String repromptMessage = availableCategories

        response.with {
            withSpeech(responseMessage)
            withReprompt(repromptMessage)
            withSimpleCard(Constants.SKILL_TITLE, responseMessage)
            withShouldEndSession(false)
        }

        response.build()
    }

    private String getPlayerName(IntentRequest intentRequest) {
        Map <String, Slot> slots = intentRequest.intent.slots
        slots[NAME_SLOT_KEY].value
    }
}
