package com.triviaroyale.handler

import com.amazon.ask.dispatcher.request.handler.HandlerInput
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
class LaunchRequestHandler {

    static AmazonDynamoDB dynamoDB
    static PlayerService playerService
    static GameStateService gameStateService

    static Optional<Response> handle(HandlerInput input) {
        log.fine(Constants.ENTERING_LOG_MESSAGE)

        Map<String, Object> sessionAttributes = input.attributesManager.sessionAttributes
        dynamoDB = AmazonDynamoDBClientBuilder.defaultClient()
        playerService = new PlayerService(dynamoDB)

        String playerId = AlexaSdkHelper.getUserId(input)
        Player player = playerService.loadPlayer(playerId)
        String responseMessage = player ? Messages.WELCOME_EXISTING_PLAYER : Messages.WELCOME_NEW_PLAYER
        String repromptMessage

        if (!player) {
            log.info("Creating new player entry for ${playerId}.")
            player = initializeNewPlayer(playerId)
        }

        sessionAttributes = PlayerService.updatePlayerSessionAttributes(sessionAttributes, player)
        gameStateService = new GameStateService(dynamoDB)
        GameState gameState = gameStateService.loadActiveGameState(player.alexaId)

        if (gameState) {
            log.info("Found active gameState ${gameState.sessionId}. Asking to resume.")
            sessionAttributes = GameStateService.updateGameStateSessionAttributes(sessionAttributes, gameState)
            sessionAttributes.put(SessionAttributes.APP_STATE, AppState.RESUME_EXISTING_GAME)
            responseMessage += " $Messages.ASK_TO_RESUME_GAME"
            repromptMessage = Messages.ASK_TO_RESUME_GAME
        } else {
            sessionAttributes.put(SessionAttributes.APP_STATE, AppState.NEW_GAME)
            responseMessage += " $Messages.ASK_TO_START_NEW_GAME"
            repromptMessage = Messages.ASK_TO_START_NEW_GAME
        }

        sessionAttributes.put(SessionAttributes.LAST_RESPONSE, responseMessage)

        input.attributesManager.sessionAttributes = sessionAttributes
        ResponseBuilder response = AlexaSdkHelper.generateResponse(input, responseMessage, repromptMessage)
        log.fine(Constants.EXITING_LOG_MESSAGE)

        response.build()
    }

    static Player initializeNewPlayer(String playerId) {
        Player newPlayer = new Player()
        newPlayer.with {
            alexaId = playerId
            quizCompletion = [:]
            quizCompletion.put(Constants.GENERAL_CATEGORY, '!')
        }
        playerService.savePlayer(newPlayer)

        newPlayer
    }

}
