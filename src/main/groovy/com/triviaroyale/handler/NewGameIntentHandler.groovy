package com.triviaroyale.handler

import static com.amazon.ask.request.Predicates.intentName
import static com.amazon.ask.request.Predicates.sessionAttribute

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.dispatcher.request.handler.RequestHandler
import com.amazon.ask.model.Response
import com.amazon.ask.model.services.directive.DirectiveServiceClient
import com.amazon.ask.model.services.directive.SendDirectiveRequest
import com.amazon.ask.model.services.directive.SpeakDirective
import com.amazon.ask.response.ResponseBuilder
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.triviaroyale.data.GameState
import com.triviaroyale.data.Player
import com.triviaroyale.data.Quiz
import com.triviaroyale.data.util.GameStateStatus
import com.triviaroyale.service.GameStateService
import com.triviaroyale.service.PlayerService
import com.triviaroyale.service.QuestionService
import com.triviaroyale.service.QuizService
import com.triviaroyale.util.*
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

@Slf4j
@CompileStatic
class NewGameIntentHandler implements RequestHandler {

    @Override
    boolean canHandle(HandlerInput input) {
        input.matches(intentName('AMAZON.YesIntent') &
                sessionAttribute(SessionAttributes.APP_STATE, AppState.NEW_GAME))
    }

    @Override
    Optional<Response> handle(HandlerInput input) {
        log.debug('START NewGameIntentHandler.handle()')
        Map<String, Object> sessionAttributes = input.attributesManager.sessionAttributes

        //Play audio clip and let player know we're setting things up.
        DirectiveServiceClient directiveServiceClient = input.serviceClientFactory.directiveService
        SpeakDirective speakDirective = SpeakDirective.builder().withSpeech(Messages.STARTING_NEW_GAME).build()
        //TODO: add audio clip
        SendDirectiveRequest sendDirectiveRequest = SendDirectiveRequest.builder().withDirective(speakDirective).build()
        directiveServiceClient.enqueue(sendDirectiveRequest)

        AmazonDynamoDB dynamoDB = AmazonDynamoDBClientBuilder.defaultClient()
        QuizService quizService = new QuizService(dynamoDB)
        Player player = PlayerService.getPlayerFromSessionAttributes(sessionAttributes)
        Quiz quiz = quizService.loadNextAvailableQuizForPlayer(player)

        if (quiz == null) {
            AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient()
            QuestionService questionService = new QuestionService(s3, Constants.S3_QUESTION_BUCKET)
            List<String> newQuizQuestions =
                    questionService.fetchRandomQuestionsForCategory(Constants.NUMBER_OF_QUESTIONS)
            quiz = quizService.generateNewQuiz(newQuizQuestions, player.alexaId)
        }

        GameState newGame = initializeGameState(quiz, player)

        List<Tuple2<String, List<Boolean>>> opponents =
                QuizService.getRandomPlayersForQuiz(quiz, Constants.NUMBER_OF_PLAYERS - 1)

        newGame = GameStateService.initializePlayers(newGame, opponents)

        GameStateService gameStateService = new GameStateService(dynamoDB)
        gameStateService.saveGameState(newGame)
        sessionAttributes[SessionAttributes.APP_STATE] = AppState.IN_GAME
        sessionAttributes = GameStateService.updateGameStateSessionAttributes(sessionAttributes, newGame)
        sessionAttributes = QuizService.updateSessionAttributesWithCurrentQuestion(sessionAttributes)
        input.attributesManager.sessionAttributes = sessionAttributes

        String responseText = 'Question 1. ' + sessionAttributes[SessionAttributes.LAST_RESPONSE] as String
        String repropmptText = sessionAttributes[SessionAttributes.LAST_RESPONSE] as String
        ResponseBuilder response = AlexaSdkHelper.responseWithSimpleCard(input, responseText, repropmptText)
        response.build()
    }

    private static GameState initializeGameState(Quiz quiz, Player player) {
        GameState newGame = new GameState()
        newGame.with {
            playerId = player.alexaId
            sessionId = System.currentTimeMillis().toString()
            status = GameStateStatus.ACTIVE
            quizId = QuizService.getQuizIdAsString(quiz)
            questions = quiz.questionJson
            currentQuestionIndex = 0
            playersHealth = [:]
            playersPerformance = [:]
            playersHealth.put(player.alexaId, Constants.STARTING_HEALTH)
            playersPerformance.put(player.alexaId, [])
        }

        newGame
    }

}
