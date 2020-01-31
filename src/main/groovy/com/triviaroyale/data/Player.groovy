package com.triviaroyale.data

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable
import com.triviaroyale.data.util.DynamoDBConstants
import groovy.transform.CompileStatic

@CompileStatic
@DynamoDBTable(tableName = DynamoDBConstants.TABLE_NAME)
class Player {

    @DynamoDBHashKey(attributeName = DynamoDBConstants.HASH_KEY)
    String alexaId

    //Our table requires a range (sort) key, but we don't really have a need for sorting players.
    @DynamoDBRangeKey(attributeName = DynamoDBConstants.RANGE_KEY)
    String rk = 'METADATA'

    @DynamoDBAttribute(attributeName = 'name')
    String name

    @DynamoDBAttribute(attributeName = 'quizCompletion')
    Map<String, Long> quizCompletion

    @Override
    String toString() {
        "Alexa ID: $alexaId.  Name: $name.  Quiz Completion: ${quizCompletion.toString()}."
    }

}
