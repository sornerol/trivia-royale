package com.triviaroyale.data

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable

@DynamoDBTable(tableName = 'TriviaRoyale')
class Quiz {
    @DynamoDBHashKey
    String category

    @DynamoDBRangeKey
    String uniqueId
    
    List<Long> questionList
}
