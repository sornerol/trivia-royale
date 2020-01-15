package com.triviaroyale.data

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable

@DynamoDBTable(tableName = 'TriviaRoyale')
class Player {
    @DynamoDBHashKey(attributeName = 'hk')
    String alexaId

    @DynamoDBRangeKey(attributeName = 'sk')
    String sk

    @DynamoDBAttribute(attributeName = 'name')
    String name

    @DynamoDBAttribute(attributeName = 'quizCompletion')
    LinkedHashMap<String, String> quizCompletion

    @Override
    String toString() {
        return "Alexa ID: ${alexaId - 'PLAYER#'}  Name: $name  Quiz Completion: ${quizCompletion.toString()}"
    }
}
