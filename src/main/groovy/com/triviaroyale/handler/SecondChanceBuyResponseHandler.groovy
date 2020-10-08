package com.triviaroyale.handler

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.model.Response
import com.amazon.ask.response.ResponseBuilder
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.triviaroyale.data.GameState
import com.triviaroyale.data.Player
import com.triviaroyale.data.util.GameStateStatus
import com.triviaroyale.service.GameStateService
import com.triviaroyale.service.PlayerService
import com.triviaroyale.service.QuizService
import com.triviaroyale.util.*
import groovy.transform.CompileStatic
import groovy.util.logging.Log

@CompileStatic
@Log
class SecondChanceBuyResponseHandler {

    static Optional<Response> accepted(HandlerInput input) {
        Map<String, Object> sessionAttributes = input.attributesManager.sessionAttributes
        AmazonDynamoDB dynamoDB = AmazonDynamoDBClientBuilder.defaultClient()

        GameState gameState = GameStateService.getSessionFromAlexaSessionAttributes(sessionAttributes)
        Player player = PlayerService.getPlayerFromSessionAttributes(sessionAttributes)
        PlayerService playerService = new PlayerService(dynamoDB)
        playerService.clearIspSessionId(player)

        gameState.with {
            status = GameStateStatus.ACTIVE
            secondChanceUsed = true
            playersHealth.put(gameState.playerId, Constants.STARTING_HEALTH)
        }
        sessionAttributes = GameStateService.updateGameStateSessionAttributes(sessionAttributes, gameState)
        input.attributesManager.sessionAttributes = sessionAttributes
        ResumeGameIntentHandler.handle(input, Messages.SECOND_CHANCE_ACCEPTED)
    }

    static Optional<Response> declined(HandlerInput input) {
        Map<String, Object> sessionAttributes = input.attributesManager.sessionAttributes

        AmazonDynamoDB dynamoDB = AmazonDynamoDBClientBuilder.defaultClient()
        PlayerService playerService = new PlayerService(dynamoDB)
        QuizService quizService = new QuizService(dynamoDB)

        GameState finalGameState = GameStateService.getSessionFromAlexaSessionAttributes(sessionAttributes)
        sessionAttributes = playerService.updatePlayerQuizCompletion(sessionAttributes)
        quizService.addPerformanceToPool(finalGameState)
        String responseMessage =
                "${GameStateService.getPlayerStatusMessage(finalGameState)} $Messages.ASK_TO_START_NEW_GAME"
        String repromtMessage = Messages.ASK_TO_START_NEW_GAME
        sessionAttributes.put(SessionAttributes.LAST_RESPONSE, Messages.ASK_TO_START_NEW_GAME)
        sessionAttributes.put(SessionAttributes.APP_STATE, AppState.NEW_GAME)
        input.attributesManager.sessionAttributes = sessionAttributes

        Player player = PlayerService.getPlayerFromSessionAttributes(sessionAttributes)
        playerService.clearIspSessionId(player)

        ResponseBuilder responseBuilder = AlexaSdkHelper.generateResponse(input, responseMessage, repromtMessage)
        responseBuilder.build()
    }

    static Optional<Response> error(HandlerInput input) {
        declined(input)
    }

}
