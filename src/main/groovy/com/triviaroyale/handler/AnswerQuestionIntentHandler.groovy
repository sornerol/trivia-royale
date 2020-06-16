package com.triviaroyale.handler

import static com.amazon.ask.request.Predicates.intentName
import static com.amazon.ask.request.Predicates.sessionAttribute

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.dispatcher.request.handler.RequestHandler
import com.amazon.ask.model.Response
import com.amazon.ask.response.ResponseBuilder
import com.triviaroyale.data.GameState
import com.triviaroyale.service.GameStateService
import com.triviaroyale.util.*
import groovy.transform.CompileStatic
import groovy.util.logging.Log

@CompileStatic
@Log
class AnswerQuestionIntentHandler implements RequestHandler {

    @Override
    boolean canHandle(HandlerInput input) {
        input.matches(intentName('AnswerIntent') &
                sessionAttribute(SessionAttributes.APP_STATE, AppState.IN_GAME.toString()))
    }

    @Override
    Optional<Response> handle(HandlerInput input) {
        log.level = Constants.LOG_LEVEL
        log.entering(this.class.name, Constants.HANDLE_METHOD)

        Map<String, Object> sessionAttributes = input.attributesManager.sessionAttributes
        String playerAnswer = AlexaSdkHelper.getSlotValue(input, AlexaSdkHelper.ANSWER_SLOT_KEY)
        GameState currentGameState = GameStateService.getSessionFromAlexaSessionAttributes(sessionAttributes)
        AnswerValidationBean answerValidation = GameStateService.processPlayersAnswer(currentGameState,
                sessionAttributes[SessionAttributes.CORRECT_ANSWER_INDEX] as int, playerAnswer)

        currentGameState = answerValidation.updatedGameState
        sessionAttributes[SessionAttributes.APP_STATE] = answerValidation.updatedAppState
        String responseMessage = answerValidation.validationMessage
        String repromtMessage
        if (sessionAttributes[SessionAttributes.APP_STATE] == AppState.IN_GAME) {
            sessionAttributes = GameStateService.updateGameStateSessionAttributes(sessionAttributes, currentGameState)
            responseMessage += "Question ${currentGameState.currentQuestionIndex + 1}: " +
                    "${sessionAttributes[SessionAttributes.LAST_RESPONSE]}"
            repromtMessage = sessionAttributes[SessionAttributes.LAST_RESPONSE]
        } else {
            responseMessage += Messages.ASK_TO_START_NEW_GAME
            repromtMessage = Messages.ASK_TO_START_NEW_GAME
        }

        input.attributesManager.sessionAttributes = sessionAttributes
        ResponseBuilder responseBuilder = AlexaSdkHelper.responseWithSimpleCard(input, responseMessage, repromtMessage)
        log.exiting(this.class.name, Constants.HANDLE_METHOD)

        responseBuilder.build()
    }

}
