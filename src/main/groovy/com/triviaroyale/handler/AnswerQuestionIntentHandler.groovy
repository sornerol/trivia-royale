package com.triviaroyale.handler

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.model.Response
import com.amazon.ask.response.ResponseBuilder
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.triviaroyale.data.GameState
import com.triviaroyale.data.Player
import com.triviaroyale.data.util.GameStateStatus
import com.triviaroyale.handler.exception.InvalidSlotException
import com.triviaroyale.isp.SecondChanceSeller
import com.triviaroyale.service.GameStateService
import com.triviaroyale.service.PlayerService
import com.triviaroyale.service.QuizService
import com.triviaroyale.service.bean.AnswerValidationBean
import com.triviaroyale.util.AlexaSdkHelper
import com.triviaroyale.util.Constants
import com.triviaroyale.util.Messages
import com.triviaroyale.util.SessionAttributes
import groovy.transform.CompileStatic
import groovy.util.logging.Log

@CompileStatic
@Log
class AnswerQuestionIntentHandler {

    static Optional<Response> handle(HandlerInput input) {
        Map<String, Object> sessionAttributes = input.attributesManager.sessionAttributes
        AmazonDynamoDB dynamoDB = AmazonDynamoDBClientBuilder.defaultClient()

        int playerAnswer
        try {
            playerAnswer = AlexaSdkHelper.getSlotId(input, AlexaSdkHelper.ANSWER_SLOT_KEY)
        } catch (InvalidSlotException ignored) {
            return FallbackRequestHandler.handle(input)
        }

        AnswerValidationBean answerValidation = GameStateService.processPlayersAnswer(sessionAttributes,
                playerAnswer)

        GameState currentGameState = answerValidation.updatedGameState
        sessionAttributes[SessionAttributes.APP_STATE] = answerValidation.updatedAppState
        sessionAttributes = GameStateService.updateGameStateSessionAttributes(sessionAttributes, currentGameState)

        if (sessionAttributes[SessionAttributes.GAME_STATE] == GameStateStatus.GAME_OVER &&
                (int) sessionAttributes[SessionAttributes.QUESTION_NUMBER] < Constants.NUMBER_OF_QUESTIONS &&
                !(Boolean) sessionAttributes[SessionAttributes.SECOND_CHANCE_USED]) {
            log.info("Attempting second chance offer for ${SessionAttributes.SESSION_ID}.")
            input.attributesManager.sessionAttributes = sessionAttributes
            Optional<Response> sellResponse = SecondChanceSeller.attemptSecondChanceSale(input, answerValidation)
            if (sellResponse) {
                PlayerService playerService = new PlayerService(dynamoDB)
                Player player = PlayerService.getPlayerFromSessionAttributes(sessionAttributes)
                playerService.setIspSessionId(player, sessionAttributes[SessionAttributes.SESSION_ID] as String)
                AlexaSdkHelper.saveCurrentSession(sessionAttributes)
                return sellResponse
            }
        }

        String responseMessage = answerValidation.validationMessage
        if (sessionAttributes[SessionAttributes.GAME_STATE] == GameStateStatus.GAME_OVER) {
            responseMessage += " ${GameStateService.getPlayerStatusMessage(currentGameState)}"
        }
        String repromtMessage
        if (sessionAttributes[SessionAttributes.GAME_STATE] == GameStateStatus.ACTIVE) {
            sessionAttributes = QuizService.updateSessionAttributesWithCurrentQuestion(sessionAttributes)
            responseMessage += " Question ${currentGameState.currentQuestionIndex + 1}: " +
                    "${sessionAttributes[SessionAttributes.LAST_RESPONSE]}"
            repromtMessage = sessionAttributes[SessionAttributes.LAST_RESPONSE]
        } else {
            QuizService quizService = new QuizService(dynamoDB)
            GameStateService gameStateService = new GameStateService(dynamoDB)
            gameStateService.saveGameState(currentGameState)

            log.fine('Session attributes at end of game: ' + sessionAttributes.toString())
            quizService.addPerformanceToPool(currentGameState)
            responseMessage += " ${Messages.ASK_TO_START_NEW_GAME}"
            repromtMessage = Messages.ASK_TO_START_NEW_GAME
            sessionAttributes.put(SessionAttributes.LAST_RESPONSE, Messages.ASK_TO_START_NEW_GAME)
        }

        input.attributesManager.sessionAttributes = sessionAttributes
        ResponseBuilder responseBuilder = AlexaSdkHelper.generateResponse(input, responseMessage, repromtMessage)

        responseBuilder.build()
    }

}
