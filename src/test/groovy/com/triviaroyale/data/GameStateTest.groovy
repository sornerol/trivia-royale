package com.triviaroyale.data

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.triviaroyale.data.util.DynamoDBConstants
import com.triviaroyale.data.util.GameStateStatus
import com.triviaroyale.testing.service.DynamoDBService
import spock.lang.Shared
import spock.lang.Specification

class GameStateTest extends Specification {
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

    def 'Create new active GameState'() {
        setup:
        GameState testGameState = new GameState()
        testGameState.with {
            sessionId = DynamoDBConstants.SESSION_PREFIX + System.currentTimeMillis()
            playerId = DynamoDBConstants.PLAYER_PREFIX + 'testId123'
            status = GameStateStatus.ACTIVE
            quizId = DynamoDBConstants.QUIZ_PREFIX + 'testQuizId123'
            currentQuestionIndex = 0
            playersHealth = new LinkedHashMap<String, Integer>()
        }

        Map<String, AttributeValue> attributeValues = new HashMap<String, AttributeValue>()
        attributeValues.put(':status', new AttributeValue().withS(GameStateStatus.ACTIVE as String))
        attributeValues.put(':hk', new AttributeValue().withS(testGameState.playerId))
        DynamoDBQueryExpression<GameState> queryExpression = new DynamoDBQueryExpression<GameState>()
                .withIndexName('sessionStatus')
                .withKeyConditionExpression('hk = :hk and sessionStatus = :status')
                .withExpressionAttributeValues(attributeValues)

        when:
        mapper.save(testGameState)
        GameState testGameStateRetrieved = mapper.query(GameState.class, queryExpression)[0]

        then:
        testGameStateRetrieved.playerId == testGameState.playerId
        testGameStateRetrieved.status == GameStateStatus.ACTIVE
    }
}
