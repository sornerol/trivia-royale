package com.triviaroyale.service

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded
import com.triviaroyale.data.GameState
import com.triviaroyale.data.util.DynamoDBConstants
import com.triviaroyale.data.util.GameStateStatus
import com.triviaroyale.testing.service.DynamoDBService
import spock.lang.Shared
import spock.lang.Specification

class GameStateServiceTest extends Specification {

    public static final String PLAYER_ID_BASE = 'TEST-PLAYER-9876-'

    @Shared
    AmazonDynamoDB dynamoDB
    @Shared
    DynamoDBService dbService
    @Shared
    DynamoDBMapper mapper
    @Shared
    GameStateService sut

    def setupSpec() {
        dynamoDB = DynamoDBEmbedded.create().amazonDynamoDB()
        dbService = new DynamoDBService(dynamoDB)
        dbService.buildTestEnvironment()
        mapper = new DynamoDBMapper(dynamoDB)
        sut = new GameStateService(dynamoDB)
    }

    def cleanupSpec() {
        dynamoDB.shutdown()
    }

    def "LoadActiveGameState"() {
        setup:
        GameState testGameState = new GameState()
        String testPlayerId = PLAYER_ID_BASE + '1'
        String testSessionId = '12345678'
        int currentQuestion = 4
        testGameState.with {
            playerId = DynamoDBConstants.PLAYER_PREFIX + testPlayerId
            sessionId = DynamoDBConstants.SESSION_PREFIX + testSessionId
            status = GameStateStatus.ACTIVE
            quizId = 'GENERAL#12345660#TESTPLAYER-1234'
            currentQuestionIndex = currentQuestion
            playersHealth = [
                    '112233': 90,
                    '112234': 80,
                    '112235': 70,
            ]
        }
        mapper.save(testGameState)
        when:
        GameState testLoadedGameState = sut.loadActiveGameState(testPlayerId)
        GameState testNonExistingGameState = sut.loadActiveGameState('foobar')

        then:
        testLoadedGameState.playerId == testPlayerId
        testLoadedGameState.sessionId == testSessionId
        testLoadedGameState.currentQuestionIndex == currentQuestion
        testLoadedGameState.playersHealth.containsKey('112235')
        !testNonExistingGameState

    }

    def "SaveGameState"() {
        setup:
        GameState testGameState = new GameState()
        String testPlayerId = PLAYER_ID_BASE + '2'
        String testSessionId = '12345679'
        String hashKey = DynamoDBConstants.PLAYER_PREFIX + testPlayerId
        String rangeKey = DynamoDBConstants.SESSION_PREFIX + testSessionId
        int currentQuestion = 5
        testGameState.with {
            playerId = testPlayerId
            sessionId = testSessionId
            status = GameStateStatus.ACTIVE
            quizId = 'GENERAL#12345660#TESTPLAYER-1234'
            currentQuestionIndex = currentQuestion
            playersHealth = [
                    '112233': 90,
                    '112234': 80,
                    '112235': 70,
            ]
        }

        when:
        sut.saveGameState(testGameState)
        GameState testGameStateRetrieved = mapper.load(GameState, hashKey, rangeKey)
        println(testGameStateRetrieved.toString())

        then:
        testGameStateRetrieved.playerId == hashKey
        testGameStateRetrieved.sessionId == rangeKey
        testGameStateRetrieved.playersHealth.containsKey('112234')
    }

}
