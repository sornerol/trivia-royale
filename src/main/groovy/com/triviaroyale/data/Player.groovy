package com.triviaroyale.data

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable

@DynamoDBTable(tableName = 'TriviaRoyale')
class Player {
    @DynamoDBHashKey
    String alexaId

    @DynamoDBRangeKey
    String name = 'METADATA'

    @DynamoDBAttribute(attributeName = 'quizCompletion')
    LinkedHashMap<String, String> quizCompletion
}
