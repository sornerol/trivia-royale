package com.triviaroyale.handler

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.model.Response
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.triviaroyale.data.GameState
import com.triviaroyale.data.Player
import com.triviaroyale.service.GameStateService
import com.triviaroyale.service.PlayerService
import com.triviaroyale.service.QuizService
import com.triviaroyale.util.GameStateHelper
import com.triviaroyale.util.Messages
import com.triviaroyale.util.PlayerHelper
import com.triviaroyale.util.ResponseHelper
import groovy.transform.CompileStatic
import groovy.util.logging.Log

@CompileStatic
@Log
class SecondChanceUpsellResponseHandler {

    static Optional<Response> accepted(HandlerInput input) {
        Map<String, Object> sessionAttributes = input.attributesManager.sessionAttributes

        Player player = PlayerService.getPlayerFromSessionAttributes(sessionAttributes)
        player = PlayerHelper.consumeSecondChance(player)
        sessionAttributes = PlayerService.updatePlayerSessionAttributes(sessionAttributes, player)
        GameState gameState = GameStateService.getSessionFromAlexaSessionAttributes(sessionAttributes)
        gameState = GameStateHelper.useSecondChance(gameState)
        sessionAttributes = GameStateService.updateGameStateSessionAttributes(sessionAttributes, gameState)

        input.attributesManager.sessionAttributes = sessionAttributes
        ResumeGameIntentHandler.handle(input, Messages.SECOND_CHANCE_USED)
    }

    static Optional<Response> declined(HandlerInput input) {
        Map<String, Object> sessionAttributes = input.attributesManager.sessionAttributes

        QuizService quizService = new QuizService(AmazonDynamoDBClientBuilder.defaultClient())
        GameState finalGameState = GameStateService.getSessionFromAlexaSessionAttributes(sessionAttributes)

        quizService.addPerformanceToPool(finalGameState)
        String preText = GameStateService.getPlayerStatusMessage(finalGameState)

        ResponseHelper.askToStartNewGame(input, preText)
    }

    static Optional<Response> error(HandlerInput input) {
        declined(input)
    }

}
