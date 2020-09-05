package com.triviaroyale.handler

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.model.Response
import com.amazon.ask.response.ResponseBuilder
import com.triviaroyale.data.GameState
import com.triviaroyale.data.Player
import com.triviaroyale.service.GameStateService
import com.triviaroyale.service.PlayerService
import com.triviaroyale.util.*
import groovy.transform.CompileStatic
import groovy.util.logging.Log

@CompileStatic
@Log
class LaunchRequestHandler {

    static Optional<Response> handle(HandlerInput input) {
        log.fine(Constants.ENTERING_LOG_MESSAGE)

        Map<String, Object> sessionAttributes = input.attributesManager.sessionAttributes

        Player player = PlayerService.getPlayerFromSessionAttributes(sessionAttributes)
        boolean isNewPlayer = PlayerService.isNewPlayer(player)
        String responseMessage = isNewPlayer ? Messages.WELCOME_EXISTING_PLAYER : Messages.WELCOME_NEW_PLAYER
        String repromptMessage

        sessionAttributes = PlayerService.updatePlayerSessionAttributes(sessionAttributes, player)
        GameState gameState = GameStateService.getSessionFromAlexaSessionAttributes(sessionAttributes)

        if (sessionAttributes[SessionAttributes.APP_STATE] == AppState.RESUME_EXISTING_GAME) {
            log.info("Found active gameState ${gameState.sessionId}. Asking to resume.")
            sessionAttributes = GameStateService.updateGameStateSessionAttributes(sessionAttributes, gameState)
            responseMessage += " $Messages.ASK_TO_RESUME_GAME"
            repromptMessage = Messages.ASK_TO_RESUME_GAME
        } else {
            responseMessage += " $Messages.ASK_TO_START_NEW_GAME"
            repromptMessage = Messages.ASK_TO_START_NEW_GAME
        }

        sessionAttributes.put(SessionAttributes.LAST_RESPONSE, responseMessage)

        input.attributesManager.sessionAttributes = sessionAttributes
        ResponseBuilder response = AlexaSdkHelper.generateResponse(input, responseMessage, repromptMessage)
        log.fine(Constants.EXITING_LOG_MESSAGE)

        response.build()
    }

}
