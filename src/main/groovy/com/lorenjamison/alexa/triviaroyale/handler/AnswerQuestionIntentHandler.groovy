package com.lorenjamison.alexa.triviaroyale.handler

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.dispatcher.request.handler.RequestHandler
import com.amazon.ask.model.Response
import com.lorenjamison.alexa.triviaroyale.service.AnswerService
import com.lorenjamison.alexa.triviaroyale.service.HealthService
import com.lorenjamison.alexa.triviaroyale.util.AlexaSdkHelper
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
        String playerAnswer = AlexaSdkHelper.getSlotValue(input, AlexaSdkHelper.ANSWER_SLOT_KEY)

        boolean wasAnswerCorrect = AnswerService.checkAnswer(playerAnswer, sessionAttributes)
        sessionAttributes = HealthService.adjustHealth(wasAnswerCorrect, sessionAttributes)
    }

}
