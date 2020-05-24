package com.triviaroyale.service

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.triviaroyale.data.GameState
import com.triviaroyale.data.Player
import com.triviaroyale.data.Quiz
import com.triviaroyale.util.Constants
import groovy.transform.CompileStatic

@CompileStatic
class QuizService extends DynamoDBAccess {

    QuizService(AmazonDynamoDB dynamoDB) {
        super(dynamoDB)
    }

    static String getQuizIdAsString(Quiz quiz) {
        String quizId = quiz.category + '|' + quiz.uniqueId
        quizId
    }

    static List<Tuple2<String, List<Boolean>>> getRandomPlayersForQuiz(Quiz quiz, int numberOfPlayers) {
        List<Tuple2<String, List<Boolean>>> playerPool = quiz.playerPool
        List<Tuple2<String, List<Boolean>>> selectedPlayers = []
        Collections.shuffle(playerPool)
        for (int i = 0; i < numberOfPlayers; i++) {
            selectedPlayers.add(playerPool.pop())
        }

        selectedPlayers
    }

    Quiz loadNextAvailableQuizForPlayer(Player player, String category = Constants.GENERAL_CATEGORY) {

    }

    Quiz generateNewQuiz(List<String> questions, String playerId, String category = Constants.GENERAL_CATEGORY) {

    }

    void addPerformanceToPool(GameState completedGame) {

    }

}
