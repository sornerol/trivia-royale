package com.triviaroyale.testing.service

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.model.*
import com.triviaroyale.data.util.DynamoDBConstants
import groovy.transform.CompileStatic

@CompileStatic
class DynamoDBService {

    public static final String SESSION_STATUS = 'sessionStatus'
    public static final long ONE_THOUSAND = 1000L

    AmazonDynamoDB dynamoDB

    DynamoDBService(AmazonDynamoDB dynamoDB) {
        if (dynamoDB == null) {
            throw new Exception('dynamoDB cannot be null')
        }
        this.dynamoDB = dynamoDB
    }

    CreateTableResult initializeTestEnvironment() {
        List<AttributeDefinition> attributeDefinitions = []
        attributeDefinitions.add(new AttributeDefinition(DynamoDBConstants.HASH_KEY, ScalarAttributeType.S))
        attributeDefinitions.add(new AttributeDefinition(DynamoDBConstants.RANGE_KEY, ScalarAttributeType.S))
        attributeDefinitions.add(new AttributeDefinition(SESSION_STATUS, ScalarAttributeType.S))

        List<KeySchemaElement> keySchemaElements = []
        keySchemaElements.add(new KeySchemaElement(DynamoDBConstants.HASH_KEY, KeyType.HASH))
        keySchemaElements.add(new KeySchemaElement(DynamoDBConstants.RANGE_KEY, KeyType.RANGE))

        List<LocalSecondaryIndex> localSecondaryIndexes = []
        Projection projection = new Projection()
        projection.projectionType = ProjectionType.ALL
        localSecondaryIndexes.add(
                new LocalSecondaryIndex()
                        .withIndexName(SESSION_STATUS)
                        .withProjection(projection)
                        .withKeySchema(
                                new KeySchemaElement(DynamoDBConstants.HASH_KEY, KeyType.HASH),
                                new KeySchemaElement(SESSION_STATUS, KeyType.RANGE)))

        ProvisionedThroughput provisionedThroughput = new ProvisionedThroughput(ONE_THOUSAND, ONE_THOUSAND)
        CreateTableRequest request = new CreateTableRequest()
                .withTableName(DynamoDBConstants.TABLE_NAME)
                .withAttributeDefinitions(attributeDefinitions)
                .withKeySchema(keySchemaElements)
                .withLocalSecondaryIndexes(localSecondaryIndexes)
                .withProvisionedThroughput(provisionedThroughput)

        dynamoDB.createTable(request)
    }

}
