package com.triviaroyale.data

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable

@DynamoDBTable(tableName = 'TriviaRoyale')
class Player {
    private String alexaId

    //Our table requires a range (sort) key, but we don't really have a need for sorting players.
    @DynamoDBRangeKey(attributeName = 'sk')
    String sk = 'METADATA'
    @DynamoDBAttribute(attributeName = 'name')
    String name
    @DynamoDBAttribute(attributeName = 'quizCompletion')
    LinkedHashMap<String, String> quizCompletion

    @DynamoDBHashKey(attributeName = 'hk')
    String getAlexaId() { return alexaId - 'PLAYER#' }
    void setAlexaId(String alexaId) { this.alexaId = 'PLAYER#' + alexaId }

    @Override
    String toString() {
        return "Alexa ID: ${getAlexaId()}  Name: $name  Quiz Completion: ${quizCompletion.toString()}"
    }
}
