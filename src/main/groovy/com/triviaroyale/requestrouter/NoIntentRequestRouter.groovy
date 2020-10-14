package com.triviaroyale.requestrouter

import static com.amazon.ask.request.Predicates.intentName
import static com.amazon.ask.request.Predicates.sessionAttribute

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.dispatcher.request.handler.RequestHandler
import com.amazon.ask.model.Response
import com.triviaroyale.data.GameState
import com.triviaroyale.handler.CancelAndStopIntentHandler
import com.triviaroyale.handler.FallbackRequestHandler
import com.triviaroyale.handler.NewGameIntentHandler
import com.triviaroyale.handler.ResumeGameIntentHandler
import com.triviaroyale.service.GameStateService
import com.triviaroyale.util.AppState
import com.triviaroyale.util.GameStateHelper
import com.triviaroyale.util.ResponseHelper
import com.triviaroyale.util.SessionAttributes
import groovy.transform.CompileStatic
import groovy.util.logging.Log

@CompileStatic
@Log
class NoIntentRequestRouter implements RequestHandler {

    @Override
    boolean canHandle(HandlerInput input) {
        input.matches(intentName('AMAZON.NoIntent'))
    }

    @Override
    Optional<Response> handle(HandlerInput input) {
        if (!input.attributesManager.sessionAttributes[SessionAttributes.APP_STATE]) {
            log.severe('Received intent for uninitialized session. Exiting...')
            return ResponseHelper.endSessionWithoutSpeech(input)
        }

        if (input.matches(sessionAttribute(SessionAttributes.APP_STATE, AppState.NEW_GAME.toString()))) {
            return CancelAndStopIntentHandler.handle(input)
        }
        if (input.matches(sessionAttribute(SessionAttributes.APP_STATE, AppState.RESUME_EXISTING_GAME.toString()))) {
            return NewGameIntentHandler.handle(input)
        }
        if (input.matches(sessionAttribute(SessionAttributes.APP_STATE, AppState.START_OVER_REQUEST.toString()))) {
            return ResumeGameIntentHandler.handle(input)
        }
        if (input.matches(
                sessionAttribute(SessionAttributes.APP_STATE, AppState.ASK_TO_PLAY_TRIVIA_ROYALE.toString()))) {
            return ResponseHelper.endSessionWithoutSpeech(input)
        }
        if (input.matches(
                sessionAttribute(SessionAttributes.APP_STATE, AppState.ASK_TO_USE_SECOND_CHANCE.toString()))) {
            Map<String, Object> sessionAttributes = input.attributesManager.sessionAttributes
            GameState gameState = GameStateService.getSessionFromAlexaSessionAttributes(sessionAttributes)
            GameStateHelper.finalizeGameState(gameState)
            String pretext = GameStateService.getPlayerStatusMessage(gameState)
            return ResponseHelper.askToStartNewGame(input, pretext)
        }

        FallbackRequestHandler.handle(input)
    }

}
