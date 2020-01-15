package com.triviaroyale.handler

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.dispatcher.request.handler.RequestHandler
import com.amazon.ask.model.Response
import com.amazon.ask.response.ResponseBuilder

import com.triviaroyale.data.Player

import com.triviaroyale.util.AlexaSdkHelper
import com.triviaroyale.util.AppState
import com.triviaroyale.util.Messages
import com.triviaroyale.util.SessionAttributes

import static com.amazon.ask.request.Predicates.intentName
import static com.amazon.ask.request.Predicates.sessionAttribute

class CreatePlayerIntentHandler implements RequestHandler {
    @Override
    boolean canHandle(HandlerInput input) {
        input.matches(intentName('NewPlayerIntent') & sessionAttribute(SessionAttributes.GAME_STATE, AppState.NEW_PLAYER_SETUP))
    }

    @Override
    Optional<Response> handle(HandlerInput input) {
//        Map<String, Object> sessionAttributes = input.attributesManager.sessionAttributes
//        PlayerBase newPlayerBase = new PlayerBase()
//        newPlayerBase.with {
//            name = AlexaSdkHelper.getSlotValue(input, AlexaSdkHelper.NAME_SLOT_KEY)
//            alexaId = AlexaSdkHelper.getUserId(input)
//        }
//        Player newPlayer = Player.createNewPlayer(newPlayerBase)
//
//        sessionAttributes.put(SessionAttributes.GAME_STATE, AppState.NEW_GAME)
//        sessionAttributes.put(SessionAttributes.PLAYER_ID, newPlayer.alexaId)
//        String responseMessage = 'Thank you. ' + Messages.ASK_TO_START_NEW_GAME
//        String repromptMessage = Messages.ASK_TO_START_NEW_GAME
//
//        sessionAttributes.put(SessionAttributes.LAST_RESPONSE, responseMessage)
//        ResponseBuilder response = AlexaSdkHelper.responseWithSimpleCard(input, responseMessage, repromptMessage)
//        response.build()
    }
}
