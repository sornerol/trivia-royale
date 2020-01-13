package com.triviaroyale.handler

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.dispatcher.request.handler.RequestHandler
import com.amazon.ask.model.Response
import com.triviaroyale.util.AlexaSdkHelper
import com.triviaroyale.util.AppState
import com.triviaroyale.util.SessionAttributes
import com.triviaroyale.service.AnswerService
import com.triviaroyale.service.HealthService

import static com.amazon.ask.request.Predicates.intentName
import static com.amazon.ask.request.Predicates.sessionAttribute

class AnswerQuestionIntentHandler implements RequestHandler{
    @Override
    boolean canHandle(HandlerInput input) {
        input.matches(intentName("AnswerIntent") & sessionAttribute(SessionAttributes.GAME_STATE, AppState.IN_GAME))
    }

    @Override
    Optional<Response> handle(HandlerInput input) {
        Map<String, Object> sessionAttributes = input.attributesManager.sessionAttributes
        String playerAnswer = AlexaSdkHelper.getSlotValue(input, AlexaSdkHelper.ANSWER_SLOT_KEY)

        boolean wasAnswerCorrect = AnswerService.checkAnswer(playerAnswer, sessionAttributes)
        sessionAttributes = HealthService.adjustHealth(wasAnswerCorrect, sessionAttributes)
    }

}
