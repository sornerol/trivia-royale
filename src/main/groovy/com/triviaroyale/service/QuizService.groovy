package com.triviaroyale.service

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.triviaroyale.data.GameState
import com.triviaroyale.data.Quiz
import com.triviaroyale.data.util.DynamoDBConstants
import groovy.transform.CompileStatic

@CompileStatic
class QuizService extends DynamoDBAccess {

    QuizService(AmazonDynamoDB dynamoDB) {
        super(dynamoDB)
    }

    Quiz loadNextAvailableQuizForPlayer() {

    }

    Quiz generateNewQuiz() {

    }

    void addPerformanceToPool(GameState completedGame) {

    }

}
