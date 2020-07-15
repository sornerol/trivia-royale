package com.triviaroyale.data

import com.amazonaws.services.dynamodbv2.datamodeling.*
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperFieldModel.DynamoDBAttributeType
import com.triviaroyale.data.util.DynamoDBConstants
import groovy.transform.CompileStatic

@CompileStatic
@DynamoDBTable(tableName = 'TriviaRoyale')
class Quiz {

    public static final int MAXIMUM_POOL_SIZE = 50
    @DynamoDBHashKey(attributeName = DynamoDBConstants.HASH_KEY)
    String category

    @DynamoDBRangeKey(attributeName = DynamoDBConstants.RANGE_KEY)
    String uniqueId

    @DynamoDBTyped(DynamoDBAttributeType.L)
    @DynamoDBAttribute
    Queue<Tuple2<String, List<Boolean>>> playerPool

    @DynamoDBAttribute
    List<String> questionJson

}
