package com.triviaroyale.handler

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.model.Response
import com.amazon.ask.response.ResponseBuilder
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.triviaroyale.data.GameState
import com.triviaroyale.data.Player
import com.triviaroyale.service.GameStateService
import com.triviaroyale.service.LeaderboardService
import com.triviaroyale.service.PlayerService
import com.triviaroyale.util.*
import groovy.transform.CompileStatic
import groovy.util.logging.Log
import redis.clients.jedis.Jedis

@CompileStatic
@Log
class LaunchRequestHandler {

    static Optional<Response> handle(HandlerInput input) {
        log.fine(Constants.ENTERING_LOG_MESSAGE)

        Map<String, Object> sessionAttributes = input.attributesManager.sessionAttributes

        Player player = PlayerService.getPlayerFromSessionAttributes(sessionAttributes)
        String responseMessage = PlayerService.isNewPlayer(player) ?
                Messages.WELCOME_NEW_PLAYER : Messages.WELCOME_EXISTING_PLAYER

        LeaderboardService leaderboardService
        try {
            leaderboardService = new LeaderboardService(new Jedis(Constants.REDIS_URL))
        } catch (e) {
            log.severe("LEADERBOARD: Error while connecting to Redis: ${e.message}")
            leaderboardService = null
        }
        if (leaderboardService && player.activeLeaderboard == LeaderboardService.currentSeasonKey) {
            try {
                int currentRank = leaderboardService.getRankForCurrentSeason(player.alexaId)
                if (currentRank) {
                    responseMessage += " Your current weekly leaderboard rank is ${currentRank}. Complete quizzes to " +
                            'earn more points and climb the leaderboard.'
                }
            } catch (e) {
                log.severe("LEADERBOARD: Error while getting leaderboard rank: ${e.message}")
            }
        } else if (leaderboardService) {
            responseMessage += " ${Messages.NEW_SEASON_STARTED}"
            player.activeLeaderboard = LeaderboardService.currentSeasonKey
            PlayerService playerService = new PlayerService(AmazonDynamoDBClientBuilder.defaultClient())
            playerService.savePlayer(player)
            sessionAttributes = PlayerService.updatePlayerSessionAttributes(sessionAttributes, player)
            try {
                if (player.activeLeaderboard) {
                    int lastSeasonPlace = leaderboardService.getRankForPreviousSeason(player.alexaId)
                    if (lastSeasonPlace) {
                        responseMessage += " Your rank for last season was ${lastSeasonPlace}."
                    }
                }
            } catch (e) {
                log.severe("LEADERBOARD: Error while getting previouos season rank: ${e.message}")
            }
        }

        GameState gameState = GameStateService.getSessionFromAlexaSessionAttributes(sessionAttributes)

        if (!gameState) {
            return ResponseHelper.askToStartNewGame(input, responseMessage)
        }

        log.info("Found active gameState ${gameState.sessionId}. Asking to resume.")
        sessionAttributes = GameStateService.updateGameStateSessionAttributes(sessionAttributes, gameState)
        sessionAttributes.put(SessionAttributes.APP_STATE, AppState.RESUME_EXISTING_GAME)
        responseMessage += " $Messages.ASK_TO_RESUME_GAME"
        String repromptMessage = Messages.ASK_TO_RESUME_GAME

        sessionAttributes.put(SessionAttributes.LAST_RESPONSE, responseMessage)

        input.attributesManager.sessionAttributes = sessionAttributes
        ResponseBuilder response = ResponseHelper.generateResponse(input, responseMessage, repromptMessage)
        log.fine(Constants.EXITING_LOG_MESSAGE)

        response.build()
    }

}
