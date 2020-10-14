package com.triviaroyale.requestrouter

import static com.amazon.ask.request.Predicates.intentName
import static com.amazon.ask.request.Predicates.sessionAttribute

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.dispatcher.request.handler.RequestHandler
import com.amazon.ask.model.Response
import com.triviaroyale.data.GameState
import com.triviaroyale.data.Player
import com.triviaroyale.handler.FallbackRequestHandler
import com.triviaroyale.handler.LaunchRequestHandler
import com.triviaroyale.handler.NewGameIntentHandler
import com.triviaroyale.handler.ResumeGameIntentHandler
import com.triviaroyale.service.GameStateService
import com.triviaroyale.service.PlayerService
import com.triviaroyale.util.*
import groovy.transform.CompileStatic
import groovy.util.logging.Log

@CompileStatic
@Log
class YesIntentRequestRouter implements RequestHandler {

    @Override
    boolean canHandle(HandlerInput input) {
        input.matches(intentName('AMAZON.YesIntent'))
    }

    @Override
    Optional<Response> handle(HandlerInput input) {
        if (!input.attributesManager.sessionAttributes[SessionAttributes.APP_STATE]) {
            log.severe('Received intent for uninitialized session. Exiting...')
            return ResponseHelper.endSessionWithoutSpeech(input)
        }
        if (input.matches(sessionAttribute(SessionAttributes.APP_STATE, AppState.NEW_GAME.toString()))) {
            return NewGameIntentHandler.handle(input)
        }
        if (input.matches(sessionAttribute(SessionAttributes.APP_STATE, AppState.RESUME_EXISTING_GAME.toString()))) {
            return ResumeGameIntentHandler.handle(input)
        }
        if (input.matches(sessionAttribute(SessionAttributes.APP_STATE, AppState.START_OVER_REQUEST.toString()))) {
            return NewGameIntentHandler.handle(input)
        }
        if (input.matches(
                sessionAttribute(SessionAttributes.APP_STATE, AppState.ASK_TO_PLAY_TRIVIA_ROYALE.toString()))) {
            return LaunchRequestHandler.handle(input)
        }
        if (input.matches(
                sessionAttribute(SessionAttributes.APP_STATE, AppState.ASK_TO_USE_SECOND_CHANCE.toString()))) {
            Map<String, Object> sessionAttributes = input.attributesManager.sessionAttributes

            Player player = PlayerService.getPlayerFromSessionAttributes(sessionAttributes)
            player = PlayerHelper.consumeSecondChance(player)
            sessionAttributes = PlayerService.updatePlayerSessionAttributes(sessionAttributes, player)
            GameState gameState = GameStateService.getSessionFromAlexaSessionAttributes(sessionAttributes)
            gameState = GameStateHelper.useSecondChance(gameState)
            sessionAttributes = GameStateService.updateGameStateSessionAttributes(sessionAttributes, gameState)

            input.attributesManager.sessionAttributes = sessionAttributes
            return ResumeGameIntentHandler.handle(input, Messages.SECOND_CHANCE_USED)
        }

        FallbackRequestHandler.handle(input)
    }

}
