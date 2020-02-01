package com.triviaroyale.service

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
import groovy.transform.CompileStatic

@CompileStatic
class DynamoDBAccess {

    final AmazonDynamoDB dynamoDB
    DynamoDBMapper mapper

    protected DynamoDBAccess(AmazonDynamoDB dynamoDB) {
        this.dynamoDB = dynamoDB
        this.mapper = new DynamoDBMapper(this.dynamoDB)
    }

}
