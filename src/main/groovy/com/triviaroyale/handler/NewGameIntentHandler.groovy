package com.triviaroyale.handler

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.model.Response
import com.amazon.ask.model.services.directive.DirectiveServiceClient
import com.amazon.ask.model.services.directive.Header
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
import groovy.util.logging.Log

@CompileStatic
@Log
class NewGameIntentHandler {

    static Optional<Response> handle(HandlerInput input) {
        log.fine(Constants.ENTERING_LOG_MESSAGE)

        Map<String, Object> sessionAttributes = input.attributesManager.sessionAttributes

        AmazonDynamoDB dynamoDB = AmazonDynamoDBClientBuilder.defaultClient()
        GameStateService gameStateService = new GameStateService(dynamoDB)

        if (sessionAttributes[SessionAttributes.APP_STATE] == AppState.START_OVER_REQUEST.toString() ||
                sessionAttributes[SessionAttributes.APP_STATE] == AppState.RESUME_EXISTING_GAME.toString()) {
            abortExistingGame(sessionAttributes, gameStateService)
            PlayerService playerService = new PlayerService(dynamoDB)
            playerService.updatePlayerQuizCompletion(sessionAttributes)
        }

        //Play audio clip and let player know we're setting things up.
        announceGameSetup(input)

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

        Map<String, List<Boolean>> opponents =
                QuizService.getRandomPlayersForQuiz(quiz, Constants.NUMBER_OF_PLAYERS - 1)

        newGame = GameStateService.initializePlayers(newGame, opponents)

        gameStateService.saveGameState(newGame)

        sessionAttributes[SessionAttributes.APP_STATE] = AppState.IN_GAME
        sessionAttributes = GameStateService.updateGameStateSessionAttributes(sessionAttributes, newGame)
        sessionAttributes = QuizService.updateSessionAttributesWithCurrentQuestion(sessionAttributes)
        input.attributesManager.sessionAttributes = sessionAttributes

        String responseText = 'Question 1. ' + sessionAttributes[SessionAttributes.LAST_RESPONSE] as String
        String repropmptText = sessionAttributes[SessionAttributes.LAST_RESPONSE] as String
        ResponseBuilder response = AlexaSdkHelper.generateResponse(input, responseText, repropmptText)
        log.fine(Constants.EXITING_LOG_MESSAGE)

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
        }

        newGame
    }

    private static void abortExistingGame(Map<String, Object> sessionAttributes, GameStateService gameStateService) {
        if (sessionAttributes[SessionAttributes.APP_STATE] == AppState.START_OVER_REQUEST.toString()) {
            GameState oldGameState = GameStateService.getSessionFromAlexaSessionAttributes(sessionAttributes)
            log.info("Setting session ID $oldGameState.sessionId to ABORTED.")
            oldGameState.status = GameStateStatus.ABORTED
            gameStateService.saveGameState(oldGameState)
        }
    }

    private static void announceGameSetup(HandlerInput input) {
        DirectiveServiceClient directiveServiceClient = input.serviceClientFactory.directiveService
        SpeakDirective speakDirective = SpeakDirective.builder().withSpeech(Messages.STARTING_NEW_GAME).build()
        Header header = Header.builder().withRequestId(input.requestEnvelope.request.requestId).build()

        SendDirectiveRequest sendDirectiveRequest = SendDirectiveRequest.builder()
                .withDirective(speakDirective)
                .withHeader(header)
                .build()
        directiveServiceClient.enqueue(sendDirectiveRequest)
    }

}
