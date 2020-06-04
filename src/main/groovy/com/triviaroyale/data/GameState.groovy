package com.triviaroyale.data

import com.amazonaws.services.dynamodbv2.datamodeling.*
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperFieldModel.DynamoDBAttributeType
import com.triviaroyale.data.util.DynamoDBConstants
import com.triviaroyale.data.util.GameStateStatus
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
    List<String> questions

    @DynamoDBAttribute
    int currentQuestionIndex

    @DynamoDBAttribute
    Map<String, Integer> playersHealth

    @DynamoDBAttribute
    Map<String, List<Boolean>> playersPerformance

}
