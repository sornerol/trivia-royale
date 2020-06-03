package com.triviaroyale.handler

import static com.amazon.ask.request.Predicates.intentName
import static com.amazon.ask.request.Predicates.sessionAttribute

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.dispatcher.request.handler.RequestHandler
import com.amazon.ask.model.Response
import com.triviaroyale.data.GameState
import com.triviaroyale.service.GameStateService
import com.triviaroyale.util.AlexaSdkHelper
import com.triviaroyale.util.AnswerValidationBean
import com.triviaroyale.util.AppState
import com.triviaroyale.util.SessionAttributes
import groovy.transform.CompileStatic

@CompileStatic
class AnswerQuestionIntentHandler implements RequestHandler {

    @Override
    boolean canHandle(HandlerInput input) {
        input.matches(intentName('AnswerIntent') & sessionAttribute(SessionAttributes.APP_STATE, AppState.IN_GAME))
    }

    @Override
    Optional<Response> handle(HandlerInput input) {
        Map<String, Object> sessionAttributes = input.attributesManager.sessionAttributes
        String playerAnswer = AlexaSdkHelper.getSlotValue(input, AlexaSdkHelper.ANSWER_SLOT_KEY)
        GameState currentGameState = GameStateService.getSessionFromAlexaSessionAttributes(sessionAttributes)
        AnswerValidationBean answerValidation = GameStateService.processPlayersAnswer(currentGameState,
                sessionAttributes[SessionAttributes.CORRECT_ANSWER_INDEX] as int, playerAnswer)

        return null
    }

}
