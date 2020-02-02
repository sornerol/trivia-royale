package com.triviaroyale.service

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded
import com.triviaroyale.testing.service.DynamoDBService
import spock.lang.Shared
import spock.lang.Specification

class GameStateServiceTest extends Specification {

    @Shared
    AmazonDynamoDB dynamoDB
    @Shared
    DynamoDBService dbService
    @Shared
    DynamoDBMapper mapper

    def setupSpec() {
        dynamoDB = DynamoDBEmbedded.create().amazonDynamoDB()
        dbService = new DynamoDBService(dynamoDB)
        dbService.buildTestEnvironment()
        mapper = new DynamoDBMapper(dynamoDB)
    }

    def cleanupSpec() {
        dynamoDB.shutdown()
    }

    def "LoadActiveGameState"() {
    }

    def "SaveGameState"() {
    }

    def "GetSessionFromAlexaSessionAttributes"() {
    }

    def "UpdateSessionAttributesWithGameState"() {
    }
}
