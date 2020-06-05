package com.triviaroyale.service

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.triviaroyale.data.Player
import com.triviaroyale.data.util.DynamoDBConstants
import com.triviaroyale.util.SessionAttributes
import groovy.transform.CompileStatic

@CompileStatic
class PlayerService extends DynamoDBAccess {

    PlayerService(AmazonDynamoDB dynamoDB) {
        super(dynamoDB)
    }

    static Player getPlayerFromSessionAttributes(Map<String, Object> sessionAttributes) {
        Player player = new Player()
        player.with {
            alexaId = sessionAttributes[SessionAttributes.PLAYER_ID] as String
            name = sessionAttributes[SessionAttributes.PLAYER_NAME] as String
            quizCompletion = sessionAttributes[SessionAttributes.PLAYER_QUIZ_COMPLETION] as LinkedHashMap<String, Long>
        }
        player
    }

    static Map<String, Object> updatePlayerSessionAttributes(Map<String, Object> sessionAttributes,
                                                             Player player) {
        sessionAttributes.with {
            put(SessionAttributes.PLAYER_ID, player.alexaId)
            put(SessionAttributes.PLAYER_NAME, player.name)
            put(SessionAttributes.PLAYER_QUIZ_COMPLETION, player.quizCompletion)
        }

        sessionAttributes
    }

    Player loadPlayer(String alexaId) {
        String hashKey = DynamoDBConstants.PLAYER_PREFIX + alexaId
        Player player = mapper.load(Player, hashKey, DynamoDBConstants.METADATA)
        if (player) {
            player.alexaId = player.alexaId - DynamoDBConstants.PLAYER_PREFIX
        }
        player
    }

    void savePlayer(Player player) {
        player.alexaId = DynamoDBConstants.PLAYER_PREFIX + player.alexaId
        mapper.save(player)
    }

}
