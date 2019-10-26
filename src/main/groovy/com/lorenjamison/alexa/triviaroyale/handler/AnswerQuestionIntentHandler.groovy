package com.lorenjamison.alexa.triviaroyale.handler

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.dispatcher.request.handler.RequestHandler
import com.amazon.ask.model.Response
import com.lorenjamison.alexa.triviaroyale.util.GameState
import com.lorenjamison.alexa.triviaroyale.util.SessionAttributes

import static com.amazon.ask.request.Predicates.intentName
import static com.amazon.ask.request.Predicates.sessionAttribute

class AnswerQuestionIntentHandler implements RequestHandler{
    @Override
    boolean canHandle(HandlerInput input) {
        input.matches(intentName("AnswerIntent") & sessionAttribute(SessionAttributes.GAME_STATE, GameState.IN_GAME))
    }

    @Override
    Optional<Response> handle(HandlerInput input) {
        Map<String, Object> sessionAttributes = input.attributesManager.sessionAttributes
        long playerId = (long) sessionAttributes[SessionAttributes.PLAYER_ID]

    }

}
