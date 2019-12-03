package com.lorenjamison.alexa.triviaroyale.handler

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.dispatcher.request.handler.RequestHandler
import com.amazon.ask.model.Response
import com.amazon.ask.response.ResponseBuilder

import com.lorenjamison.alexa.triviaroyale.dataobject.Player
import com.lorenjamison.alexa.triviaroyale.dataobject.base.PlayerBase
import com.lorenjamison.alexa.triviaroyale.util.AlexaSdkHelper
import com.lorenjamison.alexa.triviaroyale.util.GameState
import com.lorenjamison.alexa.triviaroyale.util.Messages
import com.lorenjamison.alexa.triviaroyale.util.SessionAttributes

import static com.amazon.ask.request.Predicates.intentName
import static com.amazon.ask.request.Predicates.sessionAttribute

class CreatePlayerIntentHandler implements RequestHandler {
    @Override
    boolean canHandle(HandlerInput input) {
        input.matches(intentName("NewPlayerIntent") & sessionAttribute(SessionAttributes.GAME_STATE, GameState.NEW_PLAYER_SETUP))
    }

    @Override
    Optional<Response> handle(HandlerInput input) {
        Map<String, Object> sessionAttributes = input.attributesManager.sessionAttributes
        PlayerBase newPlayerBase = new PlayerBase()
        newPlayerBase.with {
            isHousePlayer = false
            name = AlexaSdkHelper.getSlotValue(input, AlexaSdkHelper.NAME_SLOT_KEY)
            alexaId = AlexaSdkHelper.getUserId(input)
        }
        Player newPlayer = new Player(newPlayerBase)
        newPlayer.save()

        sessionAttributes.put(SessionAttributes.GAME_STATE, GameState.NEW_GAME)
        sessionAttributes.put(SessionAttributes.PLAYER_ID, newPlayer.id)
        String responseMessage = "Thank you. ${Messages.ASK_TO_START_NEW_GAME}"
        String repromptMessage = Messages.ASK_TO_START_NEW_GAME

        sessionAttributes.put(SessionAttributes.LAST_RESPONSE, responseMessage)
        ResponseBuilder response = AlexaSdkHelper.responseWithSimpleCard(input, responseMessage, repromptMessage)
        response.build()
    }
}
