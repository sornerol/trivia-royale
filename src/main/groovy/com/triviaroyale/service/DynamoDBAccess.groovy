package com.triviaroyale.service

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
import groovy.transform.CompileStatic

@CompileStatic
class DynamoDBAccess {

    final AmazonDynamoDB dynamoDB
    final String partitionKeyPrefix
    DynamoDBMapper mapper

    protected DynamoDBAccess(AmazonDynamoDB dynamoDB, String partitionKeyPrefix) {
        this.dynamoDB = dynamoDB
        this.partitionKeyPrefix = partitionKeyPrefix
        this.mapper = new DynamoDBMapper(this.dynamoDB)
    }

}
