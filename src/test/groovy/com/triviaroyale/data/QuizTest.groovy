package com.triviaroyale.data

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded
import com.triviaroyale.testing.service.DynamoDbService
import spock.lang.Shared
import spock.lang.Specification

class QuizTest extends Specification {
    @Shared
    AmazonDynamoDB dynamoDB
    @Shared
    DynamoDbService dbService
    @Shared
    DynamoDBMapper mapper

    def setupSpec() {
        dynamoDB = DynamoDBEmbedded.create().amazonDynamoDB()
        dbService = new DynamoDbService(dynamoDB)
        dbService.buildTestEnvironment()
        mapper = new DynamoDBMapper(dynamoDB)
    }

    def cleanupSpec() {
        dynamoDB.shutdown()
        dbService = null
    }

    def 'Create new quiz'() {
        setup:
        when:
        then:
    }

    def 'Search for next unplayed quiz'() {
        setup:
        when:
        then:
    }

    def 'Update player pool for quiz'() {
        setup:
        when:
        then:
    }
}
