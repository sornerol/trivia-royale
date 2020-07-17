package com.triviaroyale.handler

import static com.amazon.ask.request.Predicates.requestType

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.dispatcher.request.handler.RequestHandler
import com.amazon.ask.model.LaunchRequest
import com.amazon.ask.model.Response
import com.amazon.ask.response.ResponseBuilder
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.triviaroyale.data.GameState
import com.triviaroyale.data.Player
import com.triviaroyale.service.GameStateService
import com.triviaroyale.service.PlayerService
import com.triviaroyale.util.*
import groovy.transform.CompileStatic
import groovy.util.logging.Log

@CompileStatic
@Log
class LaunchRequestHandler implements RequestHandler {

    @Override
    boolean canHandle(HandlerInput input) {
        log.fine('Request envelope: ' + input.requestEnvelopeJson.toString())

        input.matches(requestType(LaunchRequest))
    }

    @Override
    Optional<Response> handle(HandlerInput input) {
        log.fine(Constants.ENTERING_LOG_MESSAGE)

        Map<String, Object> sessionAttributes = input.attributesManager.sessionAttributes
        AmazonDynamoDB dynamoDB = AmazonDynamoDBClientBuilder.defaultClient()
        PlayerService playerService = new PlayerService(dynamoDB)

        String playerId = AlexaSdkHelper.getUserId(input)
        Player player = playerService.loadPlayer(playerId)
        String responseMessage
        String repromptMessage

        if (player == null) {
            log.info("Player ID ${playerId} not found")
            sessionAttributes.put(SessionAttributes.APP_STATE, AppState.NEW_PLAYER_SETUP)
            responseMessage = "$Messages.WELCOME_NEW_PLAYER $Messages.RULES $Messages.ASK_FOR_NAME"
            repromptMessage = Messages.ASK_FOR_NAME
        } else {
            log.info("Found ${player.alexaId}. Name: ${player.name}.")
            GameStateService gameStateService = new GameStateService(dynamoDB)
            GameState gameState = gameStateService.loadActiveGameState(player.alexaId)
            responseMessage = Messages.WELCOME_EXISTING_PLAYER
            if (gameState) {
                log.info("Found active gameState ${gameState.sessionId}. Asking to resume.")
                sessionAttributes = GameStateService.updateGameStateSessionAttributes(sessionAttributes, gameState)
                sessionAttributes.put(SessionAttributes.APP_STATE, AppState.RESUME_EXISTING_GAME)
                responseMessage += " $Messages.ASK_TO_RESUME_GAME"
                repromptMessage = Messages.ASK_TO_RESUME_GAME
            } else {
                sessionAttributes = PlayerService.updatePlayerSessionAttributes(sessionAttributes, player)
                sessionAttributes.put(SessionAttributes.APP_STATE, AppState.NEW_GAME)
                responseMessage += " $Messages.ASK_TO_START_NEW_GAME"
                repromptMessage = Messages.ASK_TO_START_NEW_GAME
            }
        }

        sessionAttributes.put(SessionAttributes.LAST_RESPONSE, responseMessage)

        input.attributesManager.sessionAttributes = sessionAttributes
        ResponseBuilder response = AlexaSdkHelper.generateResponse(input, responseMessage, repromptMessage)
        log.fine(Constants.EXITING_LOG_MESSAGE)

        response.build()
    }

}
