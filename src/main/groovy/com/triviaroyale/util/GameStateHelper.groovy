package com.triviaroyale.util

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.triviaroyale.data.GameState
import com.triviaroyale.data.util.GameStateStatus
import com.triviaroyale.service.GameStateService
import com.triviaroyale.service.QuizService
import groovy.transform.CompileStatic

@CompileStatic
class GameStateHelper {

    static void finalizeGameState(GameState gameState) {
        QuizService quizService = new QuizService(AmazonDynamoDBClientBuilder.defaultClient())
        GameStateService gameStateService = new GameStateService(AmazonDynamoDBClientBuilder.defaultClient())
        gameStateService.saveGameState(gameState)
        quizService.addPerformanceToPool(gameState)
    }

    static GameState useSecondChance(GameState gameState) {
        gameState.with {
            status = GameStateStatus.ACTIVE
            secondChanceUsed = true
            playersHealth.put(gameState.playerId, Constants.STARTING_HEALTH)
        }

        gameState
    }

}
