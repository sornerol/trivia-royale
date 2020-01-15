package com.triviaroyale.testing.service

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.model.*

class DynamoDbService {
    AmazonDynamoDB dynamoDB

    DynamoDbService(AmazonDynamoDB dynamoDB) {
        if (dynamoDB == null) throw new Exception('dynamoDB cannot be null')
        this.dynamoDB = dynamoDB
    }

    CreateTableResult createTable(String tableName, String hashKeyName, String rangeKeyName) {
        List<AttributeDefinition> attributeDefinitions = new ArrayList<AttributeDefinition>()
        attributeDefinitions.add(new AttributeDefinition(hashKeyName, ScalarAttributeType.S))
        attributeDefinitions.add(new AttributeDefinition(rangeKeyName, ScalarAttributeType.S))

        List<KeySchemaElement> keySchemaElements = new ArrayList<KeySchemaElement>()
        keySchemaElements.add(new KeySchemaElement(hashKeyName, KeyType.HASH))
        keySchemaElements.add(new KeySchemaElement(rangeKeyName, KeyType.RANGE))

        ProvisionedThroughput provisionedThroughput = new ProvisionedThroughput(1000L, 1000L)
        CreateTableRequest request = new CreateTableRequest()
                .withTableName(tableName)
                .withAttributeDefinitions(attributeDefinitions)
                .withKeySchema(keySchemaElements)
                .withProvisionedThroughput(provisionedThroughput)

        dynamoDB.createTable(request)
    }
}
