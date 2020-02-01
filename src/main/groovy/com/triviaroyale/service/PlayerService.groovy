package com.triviaroyale.service

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.triviaroyale.data.Player
import com.triviaroyale.data.util.DynamoDBConstants
import groovy.transform.CompileStatic

@CompileStatic
class PlayerService extends DynamoDBAccess {

    PlayerService(AmazonDynamoDB dynamoDB) {
        super(dynamoDB, DynamoDBConstants.PLAYER_PREFIX)
    }

    Player loadPlayer(String alexaId) {
        String hashKey = DynamoDBConstants.PLAYER_PREFIX + alexaId
        Player player = mapper.load(Player, hashKey, DynamoDBConstants.METADATA)
        player.alexaId = player.alexaId - DynamoDBConstants.PLAYER_PREFIX
        player
    }

    void savePlayer(Player player) {
        player.alexaId = DynamoDBConstants.PLAYER_PREFIX + player.alexaId
        mapper.save(player)
    }

}
