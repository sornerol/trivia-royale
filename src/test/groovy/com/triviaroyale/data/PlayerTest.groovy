package com.triviaroyale.data

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded
import com.triviaroyale.data.util.DynamoDBConstants
import com.triviaroyale.testing.service.DynamoDBService
import spock.lang.Shared
import spock.lang.Specification

class PlayerTest extends Specification {
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
        dbService = null
    }

    def "Create New Player"() {
        setup:
        Player testPlayer = new Player()
        testPlayer.with {
            alexaId = DynamoDBConstants.PLAYER_PREFIX + 'testPlayer123'
            name = 'Test Player'
            quizCompletion = [:]
        }

        testPlayer.quizCompletion.put('General', '0')

        when:
        mapper.save(testPlayer)
        Player testPlayerRetrieved = mapper.load(Player.class, testPlayer.alexaId, 'METADATA')

        then:
        testPlayerRetrieved.alexaId == testPlayer.alexaId
        testPlayerRetrieved.name == testPlayer.name
        testPlayerRetrieved.quizCompletion['General'] == '0'
    }
}
