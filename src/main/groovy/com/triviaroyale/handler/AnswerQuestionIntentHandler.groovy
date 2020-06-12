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
import com.triviaroyale.util.Constants
import com.triviaroyale.util.SessionAttributes
import groovy.transform.CompileStatic

import java.util.logging.Logger

@CompileStatic
class AnswerQuestionIntentHandler implements RequestHandler {

    @Override
    boolean canHandle(HandlerInput input) {
        input.matches(intentName('AnswerIntent') &
                sessionAttribute(SessionAttributes.APP_STATE, AppState.IN_GAME.toString()))
    }

    @Override
    Optional<Response> handle(HandlerInput input) {
        Logger logger = Logger.getLogger(this.class.name)
        logger.level = Constants.LOG_LEVEL
        logger.entering(this.class.name, Constants.HANDLE_METHOD)

        Map<String, Object> sessionAttributes = input.attributesManager.sessionAttributes
        String playerAnswer = AlexaSdkHelper.getSlotValue(input, AlexaSdkHelper.ANSWER_SLOT_KEY)
        GameState currentGameState = GameStateService.getSessionFromAlexaSessionAttributes(sessionAttributes)
        AnswerValidationBean answerValidation = GameStateService.processPlayersAnswer(currentGameState,
                sessionAttributes[SessionAttributes.CORRECT_ANSWER_INDEX] as int, playerAnswer)

        logger.exiting(this.class.name, Constants.HANDLE_METHOD)

        return null
    }

}
