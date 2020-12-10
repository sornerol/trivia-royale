package com.triviaroyale.service

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded
import com.triviaroyale.data.Player
import com.triviaroyale.data.util.DynamoDBConstants
import com.triviaroyale.testing.service.DynamoDBService
import com.triviaroyale.util.AppState
import com.triviaroyale.util.Constants
import com.triviaroyale.util.SessionAttributes
import spock.lang.Shared
import spock.lang.Specification

class PlayerServiceTest extends Specification {

    public static final String PLAYER_ID_BASE = 'TEST-PLAYER-12345-'
    public static final Map<String, String> INITIAL_QUIZ_COMPLETION =
            [(Constants.GENERAL_CATEGORY): (Constants.CATEGORY_PROGRESS_INITIALIZER)]

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
        dbService.initializeTestEnvironment()
        mapper = new DynamoDBMapper(dynamoDB)
        sut = new PlayerService(dynamoDB)
    }

    def cleanupSpec() {
        dynamoDB.shutdown()
    }

    def "getPlayerFromSessionAttributes - id not set"() {
        setup:
        Map<String, Object> sessionAttributes = [:]
        when:
        Player player = PlayerService.getPlayerFromSessionAttributes(sessionAttributes)
        then:
        !player
    }

    def "getPlayerFromSessionAttributes - id set"() {
        setup:
        Map<String, Object> sessionAttributes = [:]
        sessionAttributes.with {
            put(SessionAttributes.PLAYER_ID, 'TESTPLAYER-123')
            put(SessionAttributes.PLAYER_QUIZ_COMPLETION,
                    PlayerServiceTest.INITIAL_QUIZ_COMPLETION)
            put(SessionAttributes.SECOND_CHANCES_PURCHASED, '3')
            put(SessionAttributes.SECOND_CHANCES_CONSUMED, '2')
        }
        when:
        Player player = PlayerService.getPlayerFromSessionAttributes(sessionAttributes)
        then:
        player.alexaId == 'TESTPLAYER-123'
        player.quizCompletion[Constants.GENERAL_CATEGORY] == Constants.CATEGORY_PROGRESS_INITIALIZER
        player.secondChancesPurchased == 3
        player.secondChancesConsumed == 2
    }

    def "updatePlayerSessionAttributes - non-null player"() {
        setup:
        Player player = new Player()
        player.with {
            alexaId = 'TEST-123'
            quizCompletion = INITIAL_QUIZ_COMPLETION
            secondChancesPurchased = 3
            secondChancesConsumed = 2
        }
        Map<String, Object> sessionAttributes = [:]
        when:
        sessionAttributes = PlayerService.updatePlayerSessionAttributes(sessionAttributes, player)
        then:
        sessionAttributes[SessionAttributes.PLAYER_ID] == 'TEST-123'
        sessionAttributes[SessionAttributes.PLAYER_QUIZ_COMPLETION] ==
                INITIAL_QUIZ_COMPLETION
        sessionAttributes[SessionAttributes.SECOND_CHANCES_PURCHASED] == 3
        sessionAttributes[SessionAttributes.SECOND_CHANCES_CONSUMED] == 2
    }

    def "updatePlayerSessionAttributes - null player"() {
        setup:
        Map<String, Object> sessionAttributes = [
                (SessionAttributes.PLAYER_ID)               : 'TEST-123',
                (SessionAttributes.PLAYER_QUIZ_COMPLETION)  :
                        INITIAL_QUIZ_COMPLETION,
                (SessionAttributes.SECOND_CHANCES_PURCHASED): 3,
                (SessionAttributes.SECOND_CHANCES_CONSUMED) : 2,
        ]
        when:
        sessionAttributes = PlayerService.updatePlayerSessionAttributes(sessionAttributes, null)
        then:
        !sessionAttributes[SessionAttributes.PLAYER_ID]
        !sessionAttributes[SessionAttributes.PLAYER_QUIZ_COMPLETION]
        !sessionAttributes[SessionAttributes.SECOND_CHANCES_PURCHASED]
        !sessionAttributes[SessionAttributes.SECOND_CHANCES_CONSUMED]
    }

    def "isNewPlayer true"() {
        setup:
        Player player = new Player()
        player.quizCompletion = INITIAL_QUIZ_COMPLETION
        expect:
        PlayerService.isNewPlayer(player)
    }

    def "isNewPlayer false"() {
        setup:
        Player player = new Player()
        player.quizCompletion = [(Constants.GENERAL_CATEGORY): '123456789#TEST-123']
        expect:
        !PlayerService.isNewPlayer(player)
    }

    def "numberOfSecondChancesAvailable"() {
        setup:
        Map<String, Object> sessionAttributes = [
                (SessionAttributes.SECOND_CHANCES_PURCHASED): 3,
                (SessionAttributes.SECOND_CHANCES_CONSUMED) : 2,
        ]
        expect:
        PlayerService.numberOfSecondChancesAvailable(sessionAttributes) == 1
    }

    def "LoadPlayer"() {
        setup:
        Player testPlayer = new Player()
        String playerId = PLAYER_ID_BASE + '1'
        testPlayer.with {
            alexaId = DynamoDBConstants.PLAYER_PREFIX + playerId
            quizCompletion = [:]
            quizCompletion.put(Constants.GENERAL_CATEGORY, '!')
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

    def 'initializeNewPlayer'() {
        setup:
        Player player

        when:
        player = sut.initializeNewPlayer('INITIALIZENEWPLAYER')

        then:
        player.alexaId == 'INITIALIZENEWPLAYER'
        player.quizCompletion == INITIAL_QUIZ_COMPLETION
        player.secondChancesPurchased == 0
        player.secondChancesConsumed == 0
    }

    def "SavePlayer"() {
        setup:
        Player testPlayer = new Player()
        String playerId = PLAYER_ID_BASE + '2'
        testPlayer.with {
            alexaId = playerId
            quizCompletion = [:]
            quizCompletion.put(Constants.GENERAL_CATEGORY, '!')
        }

        String hashKey = DynamoDBConstants.PLAYER_PREFIX + playerId

        when:
        sut.savePlayer(testPlayer)
        Player testPlayerRetrieved = mapper.load(Player, hashKey)

        then:
        testPlayerRetrieved.alexaId == hashKey
    }

    def 'setIspSession'() {
        setup:
        Map<String, Object> sessionAttributes = [
                (SessionAttributes.SESSION_ID): 'ISPSESSION001',
                (SessionAttributes.APP_STATE) : (AppState.NEW_GAME),
        ]
        Player player = new Player()
        player.with {
            alexaId = 'SETISPSESSION'
            quizCompletion = INITIAL_QUIZ_COMPLETION
        }

        when:
        player = sut.setIspSessionId(player, sessionAttributes)

        then:
        player.ispSessionId == 'ISPSESSION001'
        player.ispAppState == AppState.NEW_GAME
    }

    def 'consumeSecondChance'() {
        setup:
        Player player = new Player()
        player.with {
            alexaId = 'CONSUMESECONDCHANCE'
            quizCompletion = INITIAL_QUIZ_COMPLETION
            secondChancesPurchased = 3
            secondChancesConsumed = 0
        }

        when:
        player = sut.consumeSecondChance(player)

        then:
        player.secondChancesPurchased == 3
        player.secondChancesConsumed == 1
    }

    def 'updatePlayerQuizCompletion'() {
        setup:
        Map<String, Object> sessionAttributes = [
                (SessionAttributes.PLAYER_ID)               : 'UPDATEQUIZCOMPLETION',
                (SessionAttributes.PLAYER_QUIZ_COMPLETION)  : INITIAL_QUIZ_COMPLETION,
                (SessionAttributes.SECOND_CHANCES_PURCHASED): 3,
                (SessionAttributes.SECOND_CHANCES_CONSUMED) : 2,
                (SessionAttributes.SESSION_ID)              : 'UPDATEQUIZCOMPLETION123',
                (SessionAttributes.QUESTION_NUMBER)         : 7,
                (SessionAttributes.QUIZ_ID)                 : 'GENERAL' + Constants.QUIZ_ID_DELIMITER + '123456789',
        ]

        when:
        sessionAttributes = sut.updatePlayerQuizCompletion(sessionAttributes)

        then:
        sessionAttributes[SessionAttributes.PLAYER_QUIZ_COMPLETION] == ['GENERAL': '123456789']
    }

}
