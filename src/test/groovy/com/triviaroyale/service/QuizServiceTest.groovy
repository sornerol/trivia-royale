package com.triviaroyale.service

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded
import com.triviaroyale.data.GameState
import com.triviaroyale.data.Player
import com.triviaroyale.data.Quiz
import com.triviaroyale.data.util.DynamoDBConstants
import com.triviaroyale.testing.service.DynamoDBService
import com.triviaroyale.util.Constants
import spock.lang.Shared
import spock.lang.Specification

class QuizServiceTest extends Specification {

    @Shared
    AmazonDynamoDB dynamoDB
    @Shared
    DynamoDBService dbService
    @Shared
    DynamoDBMapper mapper
    @Shared
    QuizService sut

    def setupSpec() {
        dynamoDB = DynamoDBEmbedded.create().amazonDynamoDB()
        dbService = new DynamoDBService(dynamoDB)
        dbService.initializeTestEnvironment()
        mapper = new DynamoDBMapper(dynamoDB)
        sut = new QuizService(dynamoDB)
    }

    def 'GetQuizIdAsString'() {
        setup:
        Quiz quiz = new Quiz()
        quiz.with {
            category = 'GENERAL'
            uniqueId = '12345'
        }

        expect:
        QuizService.getQuizIdAsString(quiz) == 'GENERAL|12345'
    }

    def "GetRandomPlayersForQuiz"() {
        setup:
        Quiz quiz = new Quiz()
        quiz.playerPool = [
                    '1' : [true, true, true],
                    '2' : [true, true, true],
                    '3' : [true, true, true],
                    '4' : [true, true, true],
                    '5' : [true, true, true],
                    '6' : [true, true, true],
                    '7' : [true, true, true],
                    '8' : [true, true, true],
                    '9' : [true, true, true],
                    '10' : [true, true, true],
                    '11' : [true, true, true],
            ]

        expect:
        QuizService.getRandomPlayersForQuiz(quiz, 10).size() == 10
    }
// TODO: We can't easily unit test this method because it creates a new instance of QuestionService
//    def "UpdateSessionAttributesWithCurrentQuestion"() {
//    }

    def "LoadNextAvailableQuizForPlayer - no quiz available"() {
        setup:
        Player player = new Player()
        player.quizCompletion = [(Constants.GENERAL_CATEGORY):(Constants.CATEGORY_PROGRESS_INITIALIZER)]

        expect:
        !sut.loadNextAvailableQuizForPlayer(player)
    }

    def "LoadNextAvailableQuizForPlayer"() {
        setup:
        Player player = new Player()
        player.quizCompletion = [(Constants.GENERAL_CATEGORY):(Constants.CATEGORY_PROGRESS_INITIALIZER)]

        Quiz quiz = new Quiz()
        quiz.with {
            category = DynamoDBConstants.QUIZ_PREFIX + Constants.GENERAL_CATEGORY
            uniqueId = '12345#player1'
            playerPool = [
                    '1':[true, true, true],
                    '2':[true, true, true],
                    '3':[true, true, true],
            ]
            questions = [
                    'UNCATEGORIZED/Q1.json',
                    'UNCATEGORIZED/Q2.json',
                    'UNCATEGORIZED/Q3.json',
            ]
        }
        mapper.save(quiz)

        when:
        Quiz loadedQuiz = sut.loadNextAvailableQuizForPlayer(player)

        then:
        loadedQuiz.category == Constants.GENERAL_CATEGORY
        loadedQuiz.uniqueId == '12345#player1'
        loadedQuiz.playerPool.size() == 3
        loadedQuiz.questions.size() == 3
    }

//TODO: generateNewQuiz creates a new instance of QuestionService
//    def "GenerateNewQuiz"() {
//    }

    def "AddPerformanceToPool"() {
        setup:
        Quiz quiz = new Quiz()
        quiz.with {
            category = DynamoDBConstants.QUIZ_PREFIX + Constants.GENERAL_CATEGORY
            uniqueId = '23456#player1'
            playerPool = [:]
            questions = [
                    'UNCATEGORIZED/Q1.json',
                    'UNCATEGORIZED/Q2.json',
                    'UNCATEGORIZED/Q3.json',
            ]
        }
        for (int i = 0; i < Quiz.MAXIMUM_POOL_SIZE; i++) {
            quiz.playerPool.put(i.toString(), [true, true, true])
        }
        mapper.save(quiz)

        GameState gameState = new GameState()
        gameState.with {
            playerId = 'player2'
            quizId = 'GENERAL|23456#player1'
            playersPerformance = ['player2':[true, true, false]]
        }

        when:
        sut.addPerformanceToPool(gameState)
        Quiz retrievedQuiz = sut.loadQuizByCategoryAndId(Constants.GENERAL_CATEGORY, '23456#player1')

        then:
        retrievedQuiz.playerPool['player2'].size() == Constants.NUMBER_OF_QUESTIONS
        retrievedQuiz.playerPool.size() == Quiz.MAXIMUM_POOL_SIZE
    }

    def "AddPerformanceToPool - quiz not in database"() {
        setup:
        GameState gameState = new GameState()
        gameState.with {
            playerId = 'player3'
            quizId = 'GENERAL|23456#player3'
            playersPerformance = ['player3':[true, true, false]]
        }

        when:
        sut.addPerformanceToPool(gameState)
        Quiz retrievedQuiz = sut.loadQuizByCategoryAndId(Constants.GENERAL_CATEGORY, '23456#player3')

        then:
        !retrievedQuiz
    }

    def "LoadQuizByCategoryAndId"() {
        setup:
        Quiz quiz = new Quiz()
        quiz.with {
            category = DynamoDBConstants.QUIZ_PREFIX + Constants.GENERAL_CATEGORY
            uniqueId = '99999#player1'
            playerPool = [
                    '1':[true, true, true],
                    '2':[true, true, true],
                    '3':[true, true, true],
            ]
            questions = [
                    'UNCATEGORIZED/Q1.json',
                    'UNCATEGORIZED/Q2.json',
                    'UNCATEGORIZED/Q3.json',
            ]
        }
        mapper.save(quiz)

        when:
        Quiz retrievedQuiz = sut.loadQuizByCategoryAndId(Constants.GENERAL_CATEGORY, '99999#player1')

        then:
        retrievedQuiz.with {
            category == Constants.GENERAL_CATEGORY
            uniqueId == '99999#player1'
            playerPool.size() == 3
            questions.size() == 3
        }
    }

    def "LoadQuizByCategoryAndId - quiz not in database"() {
        expect:
        !sut.loadQuizByCategoryAndId(Constants.GENERAL_CATEGORY, 'noquizid')
    }

}
