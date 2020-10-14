package com.triviaroyale.handler

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.model.Response
import com.amazon.ask.response.ResponseBuilder
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
import com.triviaroyale.util.*
import groovy.transform.CompileStatic
import groovy.util.logging.Log

@CompileStatic
@Log
class AnswerQuestionIntentHandler {

    static Optional<Response> handle(HandlerInput input) {
        Map<String, Object> sessionAttributes = input.attributesManager.sessionAttributes

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
                shouldOfferSecondChance(sessionAttributes)) {
            log.info("Offering second chance for Session ID ${SessionAttributes.SESSION_ID}.")
            input.attributesManager.sessionAttributes = sessionAttributes
            Optional<Response> sellResponse
            if (PlayerService.numberOfSecondChancesAvailable(sessionAttributes)) {
                return askToUseSecondChance(input, answerValidation)
            }

            sellResponse = SecondChanceSeller.attemptSecondChanceSale(input, answerValidation)
            if (sellResponse) {
                PlayerService playerService = new PlayerService(AmazonDynamoDBClientBuilder.defaultClient())
                Player player = PlayerService.getPlayerFromSessionAttributes(sessionAttributes)
                playerService.setIspSessionId(player, sessionAttributes)
                AlexaSdkHelper.saveCurrentSession(sessionAttributes)
                return sellResponse
            }
        }

        String responseMessage = answerValidation.validationMessage
        if (sessionAttributes[SessionAttributes.GAME_STATE] == GameStateStatus.GAME_OVER) {
            responseMessage += " ${GameStateService.getPlayerStatusMessage(currentGameState)}"
        }
        String repromptMessage
        if (sessionAttributes[SessionAttributes.GAME_STATE] != GameStateStatus.ACTIVE) {
            input.attributesManager.sessionAttributes = sessionAttributes
            GameStateHelper.finalizeGameState(currentGameState)
            return ResponseHelper.askToStartNewGame(input, responseMessage)
        }

        sessionAttributes = QuizService.updateSessionAttributesWithCurrentQuestion(sessionAttributes)
        responseMessage += " Question ${currentGameState.currentQuestionIndex + 1}: " +
                "${sessionAttributes[SessionAttributes.LAST_RESPONSE]}"
        repromptMessage = sessionAttributes[SessionAttributes.LAST_RESPONSE]

        input.attributesManager.sessionAttributes = sessionAttributes
        ResponseBuilder responseBuilder = ResponseHelper.generateResponse(input, responseMessage, repromptMessage)

        responseBuilder.build()
    }

    protected static boolean shouldOfferSecondChance(Map<String, Object> sessionAttributes) {
        (int) sessionAttributes[SessionAttributes.QUESTION_NUMBER] < Constants.NUMBER_OF_QUESTIONS &&
                !(Boolean) sessionAttributes[SessionAttributes.SECOND_CHANCE_USED]
    }

    protected static Optional<Response> askToUseSecondChance(HandlerInput input, AnswerValidationBean validationBean) {
        Map<String, Object> sessionAttributes = input.attributesManager.sessionAttributes
        sessionAttributes.put(SessionAttributes.APP_STATE, AppState.ASK_TO_USE_SECOND_CHANCE)
        sessionAttributes.put(SessionAttributes.LAST_RESPONSE, Messages.ASK_TO_USE_SECOND_CHANCE)
        input.attributesManager.sessionAttributes = sessionAttributes

        String responseMessage = "${validationBean.validationMessage} ${Messages.ASK_TO_USE_SECOND_CHANCE}"
        String repromptMessage = Messages.ASK_TO_USE_SECOND_CHANCE
        ResponseHelper.generateResponse(input, responseMessage, repromptMessage).build()
    }

}
