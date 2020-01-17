package com.triviaroyale.data

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable
import com.triviaroyale.data.util.DynamoDBConstants

@DynamoDBTable(tableName = 'TriviaRoyale')
class Quiz {
    public static final int MAXIMUM_POOL_SIZE = 50
    @DynamoDBHashKey(attributeName = DynamoDBConstants.HASH_KEY)
    String category

    @DynamoDBRangeKey(attributeName = DynamoDBConstants.RANGE_KEY)
    String uniqueId

    @DynamoDBAttribute
    LinkedHashMap<String, List<Boolean>> playerPool

    @DynamoDBAttribute
    List<String> questionJson

}
