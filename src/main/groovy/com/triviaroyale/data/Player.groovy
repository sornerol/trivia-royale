package com.triviaroyale.data

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable
import com.triviaroyale.data.util.DynamoDBConstants

@DynamoDBTable(tableName = DynamoDBConstants.TABLE_NAME)
class Player {
    public static final String PLAYER_HASH_PREFIX = 'PLAYER#'

    private String alexaId

    //Our table requires a range (sort) key, but we don't really have a need for sorting players.
    @DynamoDBRangeKey(attributeName = DynamoDBConstants.RANGE_KEY)
    String rk = 'METADATA'

    @DynamoDBAttribute(attributeName = 'name')
    String name

    @DynamoDBAttribute(attributeName = 'quizCompletion')
    LinkedHashMap<String, String> quizCompletion

    @DynamoDBHashKey(attributeName = DynamoDBConstants.HASH_KEY)
    String getAlexaId() { return alexaId - PLAYER_HASH_PREFIX }
    void setAlexaId(String alexaId) { this.alexaId = PLAYER_HASH_PREFIX + alexaId }

    @Override
    String toString() {
        return "Alexa ID: ${getAlexaId()}.  Name: $name.  Quiz Completion: ${quizCompletion.toString()}."
    }
}
