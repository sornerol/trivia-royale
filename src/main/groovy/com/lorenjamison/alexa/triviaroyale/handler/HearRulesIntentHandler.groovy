package com.lorenjamison.alexa.triviaroyale.handler

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.dispatcher.request.handler.RequestHandler
import com.amazon.ask.model.Response
import com.amazon.ask.response.ResponseBuilder
import com.lorenjamison.alexa.triviaroyale.dataobject.Player
import com.lorenjamison.alexa.triviaroyale.util.AlexaSdkHelper
import com.lorenjamison.alexa.triviaroyale.util.Constants
import com.lorenjamison.alexa.triviaroyale.util.GameState
import com.lorenjamison.alexa.triviaroyale.util.Messages
import com.lorenjamison.alexa.triviaroyale.util.SessionAttributes

import static com.amazon.ask.request.Predicates.intentName
import static com.amazon.ask.request.Predicates.sessionAttribute

class HearRulesIntentHandler implements RequestHandler {
    @Override
    boolean canHandle(HandlerInput input) {
        input.matches(intentName("AMAZON.HelpIntent")) ||
                input.matches(intentName("AMAZON.YesIntent") &
                        sessionAttribute(SessionAttributes.GAME_STATE, GameState.NEW_PLAYER_HEAR_RULES))
    }

    @Override
    Optional<Response> handle(HandlerInput input) {
        ResponseBuilder response = input.getResponseBuilder()
        Map<String, Object> sessionAttributes = input.getAttributesManager().getSessionAttributes()
        Player player = new Player(AlexaSdkHelper.getUserId(input))
        String responseMessage = Messages.RULES_MESSAGE
        String repromptMessage

        if (sessionAttributes[SessionAttributes.GAME_STATE] == GameState.NEW_PLAYER_HEAR_RULES) {
            sessionAttributes.put(SessionAttributes.GAME_STATE, GameState.NEW_PLAYER_SETUP)
            responseMessage += Messages.REQUEST_NAME_MESSAGE
            repromptMessage = Messages.REQUEST_NAME_MESSAGE
        } else {
            //TODO - Prompt player on how to continue
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
