package com.triviaroyale.data

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded
import com.amazonaws.services.dynamodbv2.model.CreateTableResult
import com.triviaroyale.data.util.DynamoDBConstants
import com.triviaroyale.testing.service.DynamoDbService
import spock.lang.Shared
import spock.lang.Specification

class PlayerTest extends Specification {
    public static final String PLAYER_ID = 'testPlayer'
    public static final String PLAYER_NAME = 'Test'

    @Shared
    AmazonDynamoDB dynamoDB
    @Shared
    DynamoDbService dbService
    @Shared
    DynamoDBMapper mapper

    def setupSpec() {
        dynamoDB = DynamoDBEmbedded.create().amazonDynamoDB()
        dbService = new DynamoDbService(dynamoDB)
        mapper = new DynamoDBMapper(dynamoDB)
    }

    def cleanupSpec() {
        dynamoDB.shutdown()
        dbService = null
    }

    def "Create New Player"() {
        setup:
        AmazonDynamoDB dynamoDB = DynamoDBEmbedded.create().amazonDynamoDB()
        DynamoDbService dbService = new DynamoDbService(dynamoDB)
        DynamoDBMapper mapper = new DynamoDBMapper(dynamoDB)

        dbService.createTable(DynamoDBConstants.TABLE_NAME,
                DynamoDBConstants.HASH_KEY,
                DynamoDBConstants.RANGE_KEY)

        Player testPlayer = new Player()
        testPlayer.alexaId = PLAYER_ID
        testPlayer.name = PLAYER_NAME
        testPlayer.quizCompletion = [:]
        testPlayer.quizCompletion.put('General', '0')

        when:
        mapper.save(testPlayer)
        Player testPlayerRetrieved = mapper.load(Player.class, PLAYER_ID, 'METADATA')

        then:
        testPlayerRetrieved.alexaId == PLAYER_ID
        testPlayerRetrieved.name == PLAYER_NAME
        testPlayerRetrieved.quizCompletion['General'] == '0'
    }
}
