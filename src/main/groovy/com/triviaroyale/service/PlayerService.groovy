package com.triviaroyale.service

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.triviaroyale.data.GameState
import com.triviaroyale.data.Player
import com.triviaroyale.data.util.DynamoDBConstants
import com.triviaroyale.util.Constants
import com.triviaroyale.util.SessionAttributes
import groovy.transform.CompileStatic
import groovy.util.logging.Log

@CompileStatic
@Log
class PlayerService extends DynamoDBAccess {

    public static final String CATEGORY_PROGRESS_INITIALIZER = '!'

    PlayerService(AmazonDynamoDB dynamoDB) {
        super(dynamoDB)
    }

    static Player getPlayerFromSessionAttributes(Map<String, Object> sessionAttributes) {
        if (!sessionAttributes[SessionAttributes.PLAYER_ID]) {
            return null
        }

        Player player = new Player()
        player.with {
            alexaId = sessionAttributes[SessionAttributes.PLAYER_ID] as String
            quizCompletion =
                    sessionAttributes[SessionAttributes.PLAYER_QUIZ_COMPLETION] as LinkedHashMap<String, String>
        }
        player
    }

    static Map<String, Object> updatePlayerSessionAttributes(Map<String, Object> sessionAttributes,
                                                             Player player) {
        sessionAttributes.with {
            put(SessionAttributes.PLAYER_ID, player?.alexaId)
            put(SessionAttributes.PLAYER_QUIZ_COMPLETION, player?.quizCompletion)
        }

        sessionAttributes
    }

    static boolean isNewPlayer(Player player) {
        player.quizCompletion.size() <= 1 &&
                player.quizCompletion[Constants.GENERAL_CATEGORY] == CATEGORY_PROGRESS_INITIALIZER
    }

    Player loadPlayer(String alexaId) {
        String hashKey = DynamoDBConstants.PLAYER_PREFIX + alexaId
        Player player = mapper.load(Player, hashKey, DynamoDBConstants.METADATA)
        if (player) {
            player.alexaId = player.alexaId - DynamoDBConstants.PLAYER_PREFIX
        }
        player
    }

    Player initializeNewPlayer(String playerId) {
        log.info("Creating new player entry for ${playerId}.")
        Player newPlayer = new Player()
        newPlayer.with {
            alexaId = playerId
            quizCompletion = [:]
            quizCompletion.put(Constants.GENERAL_CATEGORY, CATEGORY_PROGRESS_INITIALIZER)
        }
        savePlayer(newPlayer)

        newPlayer
    }

    void savePlayer(Player player) {
        Player savedPlayer = player.clone() as Player
        savedPlayer.alexaId = DynamoDBConstants.PLAYER_PREFIX + player.alexaId
        mapper.save(savedPlayer)
    }

    Map<String, Object> updatePlayerQuizCompletion(Map<String, Object> sessionAttributes) {
        Player player = getPlayerFromSessionAttributes(sessionAttributes)

        GameState gameState = GameStateService.getSessionFromAlexaSessionAttributes(sessionAttributes)

        List<String> tokenizedQuizId = gameState.quizId.tokenize(Constants.QUIZ_ID_DELIMITER)

        player.quizCompletion.put(tokenizedQuizId[0], tokenizedQuizId[1])
        savePlayer(player)
        Map<String, Object> newSessionAttributes = updatePlayerSessionAttributes(sessionAttributes, player)

        newSessionAttributes
    }

}
