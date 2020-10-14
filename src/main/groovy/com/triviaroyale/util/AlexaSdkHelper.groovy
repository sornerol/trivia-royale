package com.triviaroyale.util

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.model.IntentRequest
import com.amazon.ask.model.Slot
import com.amazon.ask.model.slu.entityresolution.StatusCode
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.triviaroyale.data.GameState
import com.triviaroyale.handler.exception.InvalidSlotException
import com.triviaroyale.service.GameStateService
import groovy.transform.CompileStatic
import groovy.util.logging.Log

@Log
@CompileStatic
class AlexaSdkHelper {

    static final String ANSWER_SLOT_KEY = 'answer'

    static int getSlotId(HandlerInput input, String key) throws InvalidSlotException {
        IntentRequest request = (IntentRequest) input.requestEnvelope.request
        Map<String, Slot> slots = request.intent.slots
        if (slots[key].resolutions.resolutionsPerAuthority[0].status.code != StatusCode.ER_SUCCESS_MATCH) {
            String message = 'Could not get slot ID. '
            message += "(Status code: ${slots[key].resolutions.resolutionsPerAuthority[0].status.code}"
            log.severe(message)
            throw new InvalidSlotException(message)
        }
        slots[key].resolutions.resolutionsPerAuthority[0].values[0].value.id.toInteger()
    }

    static void saveCurrentSession(Map<String, Object> sessionAttributes) {
        GameState gameState = GameStateService.getSessionFromAlexaSessionAttributes(sessionAttributes)
        if (!gameState) {
            return
        }
        AmazonDynamoDB dynamoDB = AmazonDynamoDBClientBuilder.defaultClient()
        GameStateService gameStateService = new GameStateService(dynamoDB)
        gameStateService.saveGameState(gameState)
    }

}
