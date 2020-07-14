package com.triviaroyale.handler

import static com.amazon.ask.request.Predicates.requestType

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.dispatcher.request.handler.RequestHandler
import com.amazon.ask.model.Response
import com.amazon.ask.model.SessionEndedRequest
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.triviaroyale.data.GameState
import com.triviaroyale.service.GameStateService
import com.triviaroyale.util.AppState
import com.triviaroyale.util.Constants
import com.triviaroyale.util.SessionAttributes
import groovy.transform.CompileStatic
import groovy.util.logging.Log

@CompileStatic
@Log
class SessionEndedRequestHandler implements RequestHandler {

    @Override
    boolean canHandle(HandlerInput input) {
        input.matches(requestType(SessionEndedRequest))
    }

    @Override
    Optional<Response> handle(HandlerInput input) {
        log.fine(Constants.ENTERING_LOG_MESSAGE)
        Map<String, Object> sessionAttributes = input.attributesManager.sessionAttributes
        if (sessionAttributes[SessionAttributes.APP_STATE] == AppState.IN_GAME.toString()) {
            GameState gameState = GameStateService.getSessionFromAlexaSessionAttributes(sessionAttributes)
            AmazonDynamoDB dynamoDB = AmazonDynamoDBClientBuilder.defaultClient()
            GameStateService gameStateService = new GameStateService(dynamoDB)
            gameStateService.saveGameState(gameState)
        }
        log.fine(Constants.EXITING_LOG_MESSAGE)

        null
    }

}
