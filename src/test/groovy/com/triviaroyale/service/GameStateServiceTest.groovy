package com.triviaroyale.service

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded
import com.triviaroyale.data.GameState
import com.triviaroyale.data.util.DynamoDBConstants
import com.triviaroyale.data.util.GameStateStatus
import com.triviaroyale.service.exception.GameStateNotFoundException
import com.triviaroyale.util.Constants
import com.triviaroyale.util.SessionAttributes
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
        dbService.initializeTestEnvironment()
        mapper = new DynamoDBMapper(dynamoDB)
        sut = new GameStateService(dynamoDB)
    }

    def cleanupSpec() {
        dynamoDB.shutdown()
    }

    def 'getSessionFromAlexaSessionAttributes'() {
        setup:
        Map<String, Object> sessionAttributes = [:]
        sessionAttributes.with {
            put(SessionAttributes.PLAYER_ID, 'PLAYER-12345')
            put(SessionAttributes.SESSION_ID, 'SESSION-12345')
            put(SessionAttributes.GAME_STATE, GameStateStatus.ACTIVE)
            put(SessionAttributes.QUIZ_ID, 'QUIZ-12345')
            put(SessionAttributes.QUESTION_LIST, ['Question1'])
            put(SessionAttributes.QUESTION_NUMBER, 0)
            put(SessionAttributes.SECOND_CHANCE_USED, false)
            put(SessionAttributes.PLAYERS_HEALTH, ['PLAYER-12345':100])
            put(SessionAttributes.PLAYERS_PERFORMANCE, ['PLAYER-12345':[true]])
        }
        when:
        GameState gameState = GameStateService.getSessionFromAlexaSessionAttributes(sessionAttributes)
        then:
        gameState.with {
            playerId == 'PLAYER-12345'
            sessionId == 'SESSION-12345'
            status == GameStateStatus.ACTIVE
            quizId == 'QUIZ-12345'
            questions == ['Question1']
            currentQuestionIndex == 0
            secondChanceUsed
            playersHealth == ['PLAYER-12345':100]
            playersPerformance == ['PLAYER-12345':[true]]
        }
    }

    def 'getSessionFromAlexaSessionAttributes - null sessionId'() {
        setup:
        Map<String, Object> sessionAttributes = [:]
        expect:
        !GameStateService.getSessionFromAlexaSessionAttributes(sessionAttributes)
    }

    def 'updateGameStateSessionAttributes'() {
        setup:
        GameState gameState = new GameState()
        gameState.with {
            playerId = 'PLAYER-12345'
            sessionId = 'SESSION-12345'
            status = GameStateStatus.ACTIVE
            quizId = 'QUIZ-12345'
            questions = ['Question1']
            currentQuestionIndex = 0
            secondChanceUsed = false
            playersHealth = ['PLAYER-12345':100]
            playersPerformance = ['PLAYER-12345':[true]]
        }
        Map<String,Object> sessionAttributes = [:]
        when:
        sessionAttributes = GameStateService.updateGameStateSessionAttributes(sessionAttributes, gameState)
        then:
        sessionAttributes[SessionAttributes.PLAYER_ID] == 'PLAYER-12345'
        sessionAttributes[SessionAttributes.SESSION_ID] == 'SESSION-12345'
        sessionAttributes[SessionAttributes.GAME_STATE] == GameStateStatus.ACTIVE
        sessionAttributes[SessionAttributes.QUIZ_ID] == 'QUIZ-12345'
        sessionAttributes[SessionAttributes.QUESTION_LIST] == ['Question1']
        sessionAttributes[SessionAttributes.QUESTION_NUMBER] == 0
        sessionAttributes[SessionAttributes.SECOND_CHANCE_USED] == false
        sessionAttributes[SessionAttributes.PLAYERS_HEALTH] == ['PLAYER-12345':100]
        sessionAttributes[SessionAttributes.PLAYERS_PERFORMANCE] == ['PLAYER-12345':[true]]
    }

    def 'updateGameStateSessionAttributes - null gameState'() {
        setup:
        Map<String,Object> sessionAttributes = [:]
        when:
        sessionAttributes = GameStateService.updateGameStateSessionAttributes(sessionAttributes, null)
        then:
        sessionAttributes[SessionAttributes.PLAYER_ID] == null
        sessionAttributes[SessionAttributes.SESSION_ID] == null
        sessionAttributes[SessionAttributes.GAME_STATE] == null
        sessionAttributes[SessionAttributes.QUIZ_ID] == null
        sessionAttributes[SessionAttributes.QUESTION_LIST] == null
        sessionAttributes[SessionAttributes.QUESTION_NUMBER] == null
        sessionAttributes[SessionAttributes.SECOND_CHANCE_USED] == null
        sessionAttributes[SessionAttributes.PLAYERS_HEALTH] == null
        sessionAttributes[SessionAttributes.PLAYERS_PERFORMANCE] == null
    }

    def 'initializePlayers'() {
        setup:
        GameState gameState = new GameState()
        gameState.with {
            playerId = 'player1'
            playersHealth = [:]
            playersPerformance = [:]
        }

        Map<String, List<Boolean>> players = [
                '1': [true, true, true],
                '2': [true, true, true],
                '3': [true, true, true],
        ]
        when:
        GameState initializedGameState = GameStateService.initializePlayers(gameState, players)

        then:
        initializedGameState.with {
            playersPerformance['player1'] == []
            playersPerformance.size() == 4
            playersHealth['player1'] == Constants.STARTING_HEALTH
            playersHealth.size() == 4
        }
    }

//TODO: Write unit tests to cover answer validation scenarios

//    def 'processPlayersAnswer - correct answer'() {
//        setup:
//        Map<String, Object> sessionAttributes = [:]
//        sessionAttributes.with {
//            put(SessionAttributes.QUESTION_NUMBER, 0)
//        }
//        when:
//        then:
//    }
//
//    def 'processPlayersAnswer - incorrect answer'() {
//        setup:
//        when:
//        then:
//    }

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
            playersPerformance = [
                    '112233': [true, false, true, false, true],
                    '112234': [false, false, false, false, false],
                    '112235': [true, true, true, true, true],
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
        testLoadedGameState.playersPerformance['112235'] == [true, true, true, true, true]
        !testNonExistingGameState
    }

    def 'loadGameStateById'() {
        setup:
        GameState testGameState = new GameState()
        String testPlayerId = PLAYER_ID_BASE + '2'
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
            playersPerformance = [
                    '112233': [true, false, true, false, true],
                    '112234': [false, false, false, false, false],
                    '112235': [true, true, true, true, true],
            ]
        }
        mapper.save(testGameState)

        when:
        GameState retrievedGameState = sut.loadGameStateById(testPlayerId, testSessionId)

        then:
        retrievedGameState.playerId == testPlayerId
        retrievedGameState.sessionId == testSessionId
    }

    def 'loadGameStateById - gameState not found'() {
        when:
        sut.loadGameStateById('DOESNTEXIST', 'DOESNTEXIST')

        then:
        thrown(GameStateNotFoundException)
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
            playersPerformance = [
                    '112233': [true, false, true, false, true],
                    '112234': [false, false, false, false, false],
                    '112235': [true, true, true, true, true],
            ]
        }

        when:
        sut.saveGameState(testGameState)
        GameState testGameStateRetrieved = mapper.load(GameState, hashKey, rangeKey)

        then:
        testGameStateRetrieved.playerId == hashKey
        testGameStateRetrieved.sessionId == rangeKey
        testGameStateRetrieved.playersHealth.containsKey('112234')
        testGameStateRetrieved.playersPerformance['112234'] == [false, false, false, false, false]
    }

}
