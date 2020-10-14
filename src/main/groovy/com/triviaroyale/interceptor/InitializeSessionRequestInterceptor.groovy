package com.triviaroyale.interceptor

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.dispatcher.request.interceptor.RequestInterceptor
import com.amazon.ask.model.services.monetization.InSkillProduct
import com.amazon.ask.model.services.monetization.InSkillProductsResponse
import com.amazon.ask.model.services.monetization.MonetizationServiceClient
import com.amazon.ask.request.RequestHelper
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.triviaroyale.data.GameState
import com.triviaroyale.data.Player
import com.triviaroyale.isp.IspUtil
import com.triviaroyale.service.GameStateService
import com.triviaroyale.service.PlayerService
import com.triviaroyale.util.SessionAttributes
import groovy.transform.CompileStatic
import groovy.util.logging.Log

@CompileStatic
@Log
class InitializeSessionRequestInterceptor implements RequestInterceptor {

    @Override
    void process(HandlerInput input) {
        Map<String, Object> sessionAttributes = input.attributesManager.sessionAttributes
        RequestHelper requestHelper = RequestHelper.forHandlerInput(input)
        String playerId = requestHelper.userId.get()

        PlayerService playerService = new PlayerService(AmazonDynamoDBClientBuilder.defaultClient())

        GameStateService gameStateService = new GameStateService(AmazonDynamoDBClientBuilder.defaultClient())
        Player player = sessionAttributes[SessionAttributes.PLAYER_ID] ?
                PlayerService.getPlayerFromSessionAttributes(sessionAttributes) : loadPlayerFromDatabase(playerId)
        Player inventoryRefreshedPlayer = refreshIspInventory(input, player)

        GameState gameState

        if (!sessionAttributes[SessionAttributes.SESSION_ID]) {
            if (inventoryRefreshedPlayer.ispSessionId &&
                    requestHelper.requestType.equalsIgnoreCase('Connections.Response')) {
                log.info("ISP session: Retrieving session $inventoryRefreshedPlayer.ispSessionId " +
                        "for $inventoryRefreshedPlayer.alexaId")
                gameState = gameStateService.loadGameStateById(inventoryRefreshedPlayer.alexaId,
                        inventoryRefreshedPlayer.ispSessionId)
                sessionAttributes.put(SessionAttributes.APP_STATE, inventoryRefreshedPlayer.ispAppState)
            } else {
                log.info("Checking for an active gameState for player $playerId")
                gameState = gameStateService.loadActiveGameState(playerId)
            }

            if (gameState) {
                sessionAttributes = GameStateService.updateGameStateSessionAttributes(sessionAttributes, gameState)
            }
        }

        inventoryRefreshedPlayer.ispSessionId = null
        inventoryRefreshedPlayer.ispAppState = null

        if (player != inventoryRefreshedPlayer) {
            log.info("Updating player ID $inventoryRefreshedPlayer.alexaId in database")
            playerService.savePlayer(inventoryRefreshedPlayer)
        }
        sessionAttributes = PlayerService.updatePlayerSessionAttributes(sessionAttributes, inventoryRefreshedPlayer)
        input.attributesManager.sessionAttributes = sessionAttributes
    }

    protected static Player loadPlayerFromDatabase(String playerId) {
        log.info("Loading player ID $playerId from database.")
        PlayerService playerService = new PlayerService(AmazonDynamoDBClientBuilder.defaultClient())
        Player player = playerService.loadPlayer(playerId) ?: playerService.initializeNewPlayer(playerId)

        player
    }

    protected static Player refreshIspInventory(HandlerInput input, Player player) {
        RequestHelper requestHelper = RequestHelper.forHandlerInput(input)
        String locale = requestHelper.locale
        MonetizationServiceClient client = input.serviceClientFactory.monetizationService
        InSkillProductsResponse response = client.getInSkillProducts(
                locale,
                null,
                null,
                null,
                null,
                null)
        InSkillProduct secondChance = IspUtil.getInSkillProductByName(response, IspUtil.SECOND_CHANCE_PRODUCT)

        if (!secondChance) {
            log.info('Second Chance product not found, so not updating player inventory')
            return player
        }

        Player refreshedPlayer = (Player) player.clone()
        refreshedPlayer.secondChancesPurchased = secondChance.activeEntitlementCount * 3
        if (refreshedPlayer.secondChancesConsumed > refreshedPlayer.secondChancesPurchased) {
            refreshedPlayer.secondChancesConsumed = refreshedPlayer.secondChancesPurchased
        }

        refreshedPlayer
    }

}
