package com.triviaroyale.handler

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.model.Response
import com.amazon.ask.response.ResponseBuilder
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.triviaroyale.data.GameState
import com.triviaroyale.service.GameStateService
import com.triviaroyale.service.PlayerService
import com.triviaroyale.service.QuizService
import com.triviaroyale.service.bean.AnswerValidationBean
import com.triviaroyale.util.*
import groovy.transform.CompileStatic
import groovy.util.logging.Log

@CompileStatic
@Log
class AnswerQuestionIntentHandler {

    static Optional<Response> handle(HandlerInput input) {
        log.fine(Constants.ENTERING_LOG_MESSAGE)

        Map<String, Object> sessionAttributes = input.attributesManager.sessionAttributes
        int playerAnswer = AlexaSdkHelper.getSlotId(input, AlexaSdkHelper.ANSWER_SLOT_KEY)
        GameState currentGameState = GameStateService.getSessionFromAlexaSessionAttributes(sessionAttributes)
        AnswerValidationBean answerValidation = GameStateService.processPlayersAnswer(currentGameState,
                sessionAttributes[SessionAttributes.CORRECT_ANSWER_INDEX] as int, playerAnswer)

        currentGameState = answerValidation.updatedGameState
        sessionAttributes = GameStateService.updateGameStateSessionAttributes(sessionAttributes, currentGameState)
        sessionAttributes[SessionAttributes.APP_STATE] = answerValidation.updatedAppState
        String responseMessage = answerValidation.validationMessage
        String repromtMessage
        if (sessionAttributes[SessionAttributes.APP_STATE] == AppState.IN_GAME) {
            sessionAttributes = QuizService.updateSessionAttributesWithCurrentQuestion(sessionAttributes)
            responseMessage += " Question ${currentGameState.currentQuestionIndex + 1}: " +
                    "${sessionAttributes[SessionAttributes.LAST_RESPONSE]}"
            repromtMessage = sessionAttributes[SessionAttributes.LAST_RESPONSE]
        } else {
            AmazonDynamoDB dynamoDB = AmazonDynamoDBClientBuilder.defaultClient()
            PlayerService playerService = new PlayerService(dynamoDB)
            QuizService quizService = new QuizService(dynamoDB)
            GameStateService gameStateService = new GameStateService(dynamoDB)
            gameStateService.saveGameState(currentGameState)

            sessionAttributes = playerService.updatePlayerQuizCompletion(sessionAttributes)
            log.fine('Session attributes at end of game: ' + sessionAttributes.toString())
            quizService.addPerformanceToPool(currentGameState)
            responseMessage += " ${Messages.ASK_TO_START_NEW_GAME}"
            repromtMessage = Messages.ASK_TO_START_NEW_GAME
            sessionAttributes.put(SessionAttributes.LAST_RESPONSE, Messages.ASK_TO_START_NEW_GAME)
        }

        input.attributesManager.sessionAttributes = sessionAttributes
        ResponseBuilder responseBuilder = AlexaSdkHelper.generateResponse(input, responseMessage, repromtMessage)
        log.fine(Constants.EXITING_LOG_MESSAGE)

        responseBuilder.build()
    }

}
