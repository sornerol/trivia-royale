package com.triviaroyale.data

import com.amazonaws.services.dynamodbv2.datamodeling.*
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperFieldModel.DynamoDBAttributeType
import com.triviaroyale.data.util.DynamoDBConstants
import com.triviaroyale.data.util.GameStateStatus
import com.triviaroyale.util.Constants
import groovy.transform.CompileStatic

@CompileStatic
@DynamoDBTable(tableName = DynamoDBConstants.TABLE_NAME)
class GameState {

    @DynamoDBHashKey(attributeName = DynamoDBConstants.HASH_KEY)
    String playerId

    @DynamoDBRangeKey(attributeName = DynamoDBConstants.RANGE_KEY)
    String sessionId

    @DynamoDBTyped(DynamoDBAttributeType.S)
    @DynamoDBAttribute(attributeName = DynamoDBConstants.SESSION_STATUS_KEY)
    GameStateStatus status

    @DynamoDBAttribute
    String quizId

    @DynamoDBAttribute
    int currentQuestionIndex

    @DynamoDBAttribute
    Map<String, Integer> playersHealth

    //TODO: We need to store right/wrong for each question for the player and maybe the player's opponents

    @Override
    String toString() {
        "Session ID: $sessionId.  " +
                "Player ID: $playerId.  " +
                "Status: $status.\n" +
                "Quiz ID: $quizId. " +
                "Current question: $currentQuestionIndex of $Constants.NUMBER_OF_QUESTIONS.\n" +
                "Players' health: ${playersHealth.toString()}"
    }

}
