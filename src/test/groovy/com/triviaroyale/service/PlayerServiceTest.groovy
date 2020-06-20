package com.triviaroyale.service

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded
import com.triviaroyale.data.Player
import com.triviaroyale.data.util.DynamoDBConstants
import com.triviaroyale.testing.service.DynamoDBService
import spock.lang.Shared
import spock.lang.Specification

class PlayerServiceTest extends Specification {
    
    public static final String PLAYER_ID_BASE = 'TEST-PLAYER-12345-'
    public static final String PLAYER_NAME = 'Tess'

    @Shared
    AmazonDynamoDB dynamoDB
    @Shared
    DynamoDBService dbService
    @Shared
    DynamoDBMapper mapper
    @Shared
    PlayerService sut


    def setupSpec() {
        dynamoDB = DynamoDBEmbedded.create().amazonDynamoDB()
        dbService = new DynamoDBService(dynamoDB)
        dbService.buildTestEnvironment()
        mapper = new DynamoDBMapper(dynamoDB)
        sut = new PlayerService(dynamoDB)
    }

    def cleanupSpec() {
        dynamoDB.shutdown()
    }

    def "LoadPlayer"() {
        setup:
        Player testPlayer = new Player()
        String playerId = PLAYER_ID_BASE + '1'
        testPlayer.with {
            alexaId = DynamoDBConstants.PLAYER_PREFIX + playerId
            name = PLAYER_NAME
            quizCompletion = [:]
            quizCompletion.put('General', '!')
        }
        mapper.save(testPlayer)

        when:
        Player testLoadedPlayer = sut.loadPlayer(playerId)

        then:
        testLoadedPlayer.alexaId == playerId

    }

    def "LoadNonExistingPlayer"() {
        setup:
        Player player

        when:
        player = sut.loadPlayer('ThisPlayerDoesNotExist')

        then:
        !player
    }

    def "SavePlayer"() {
        setup:
        Player testPlayer = new Player()
        String playerId = PLAYER_ID_BASE + '2'
        testPlayer.with {
            alexaId = playerId
            name = PLAYER_NAME
            quizCompletion = [:]
            quizCompletion.put('General', '!')
        }

        String hashKey = DynamoDBConstants.PLAYER_PREFIX + playerId

        when:
        sut.savePlayer(testPlayer)
        Player testPlayerRetrieved = mapper.load(Player, hashKey)

        then:
        testPlayerRetrieved.alexaId == hashKey

    }

}
