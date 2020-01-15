package com.triviaroyale.data

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable
import com.triviaroyale.data.util.SessionStatus

@DynamoDBTable(tableName = 'TriviaRoyale')
class GameState {
    long id
    SessionStatus status
    long quizId
    int currentQuestionIndex
    long playerId
    LinkedHashMap<Long, Integer> playersHealth
}
