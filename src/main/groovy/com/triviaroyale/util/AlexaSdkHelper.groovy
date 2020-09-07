package com.triviaroyale.util

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.model.IntentRequest
import com.amazon.ask.model.Response
import com.amazon.ask.model.Slot
import com.amazon.ask.response.ResponseBuilder
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.triviaroyale.data.GameState
import com.triviaroyale.data.Player
import com.triviaroyale.service.GameStateService
import com.triviaroyale.service.PlayerService
import groovy.transform.CompileStatic
import groovy.util.logging.Log

@Log
@CompileStatic
class AlexaSdkHelper {

    static final String ANSWER_SLOT_KEY = 'answer'

    static String getUserId(HandlerInput input) {
        input.requestEnvelope.context.system.user.userId
    }

    static int getSlotId(HandlerInput input, String key) {
        IntentRequest request = (IntentRequest) input.requestEnvelope.request
        Map<String, Slot> slots = request.intent.slots
        slots[key].resolutions.resolutionsPerAuthority[0].values[0].value.id.toInteger()
    }

    static ResponseBuilder generateResponse(HandlerInput input,
                                            String responseMessage,
                                            String repromptMessage) {
        ResponseBuilder responseBuilder = input.responseBuilder
        responseBuilder = responseBuilder
                .withSpeech(responseMessage)
                .withReprompt(repromptMessage)
                .withShouldEndSession(false)
        responseBuilder
    }

    static ResponseBuilder generateEndSessionResponse(HandlerInput input,
                                                      String responseMessage) {
        ResponseBuilder responseBuilder = input.responseBuilder
        responseBuilder = responseBuilder
                .withSpeech(responseMessage)
                .withShouldEndSession(true)
        responseBuilder
    }

    static Optional<Response> endSessionWithoutSpeech(HandlerInput input) {
        ResponseBuilder responseBuilder = input.responseBuilder
        responseBuilder.withShouldEndSession(true).build()
    }

    static HandlerInput initializeHandlerInput(HandlerInput input) {
        Map<String, Object> sessionAttributes = input.attributesManager.sessionAttributes
        String playerId = getUserId(input)
        AmazonDynamoDB dynamoDB = AmazonDynamoDBClientBuilder.defaultClient()
        if (!sessionAttributes[SessionAttributes.PLAYER_ID]) {
            PlayerService playerService = new PlayerService(dynamoDB)
            Player player = playerService.loadPlayer(playerId)
            player = player ?: playerService.initializeNewPlayer(playerId)
            sessionAttributes = PlayerService.updatePlayerSessionAttributes(sessionAttributes, player)
        }
        if (!sessionAttributes[SessionAttributes.SESSION_ID]) {
            GameStateService gameStateService = new GameStateService(dynamoDB)
            GameState gameState = gameStateService.loadActiveGameState(playerId)
            if (gameState) {
                sessionAttributes = GameStateService.updateGameStateSessionAttributes(sessionAttributes, gameState)
            }
        }

        HandlerInput newHandlerInput = input
        newHandlerInput.attributesManager.sessionAttributes = sessionAttributes

        newHandlerInput
    }

}
