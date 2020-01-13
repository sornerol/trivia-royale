package com.triviaroyale.data

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable

@DynamoDBTable(tableName = 'TriviaRoyale')
class Quiz {
    long id
    List<Long> questionList
}
