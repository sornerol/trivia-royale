package com.triviaroyale.util

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.triviaroyale.data.Player
import com.triviaroyale.service.PlayerService
import groovy.transform.CompileStatic

@CompileStatic
class PlayerHelper {

    static Player consumeSecondChance(Player player) {
        PlayerService playerService = new PlayerService(AmazonDynamoDBClientBuilder.defaultClient())
        playerService.consumeSecondChance(player)
    }

}
