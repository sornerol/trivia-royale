package com.triviaroyale.data

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable
import com.triviaroyale.data.util.DynamoDBConstants
import com.triviaroyale.util.Constants

@DynamoDBTable(tableName = DynamoDBConstants.TABLE_NAME)
class GameState {
    @DynamoDBHashKey(attributeName = DynamoDBConstants.HASH_KEY)
    String playerId

    @DynamoDBRangeKey(attributeName = DynamoDBConstants.RANGE_KEY)
    String sessionId

    @DynamoDBAttribute(attributeName = DynamoDBConstants.SESSION_STATUS_KEY)
    String status

    @DynamoDBAttribute
    String quizId

    @DynamoDBAttribute
    int currentQuestionIndex

    @DynamoDBAttribute
    LinkedHashMap<String, Integer> playersHealth

    //TODO: We need to store right/wrong for each question for the player and maybe the player's opponents

    @Override
    String toString() {
        return "Session ID: $sessionId.  " +
                "Player ID: $playerId.  " +
                "Status: $status.\n" +
                "Quiz ID: $quizId. " +
                "Current question: $currentQuestionIndex of $Constants.NUMBER_OF_QUESTIONS.\n" +
                "Players' health: ${playersHealth.toString()}"
    }
}
