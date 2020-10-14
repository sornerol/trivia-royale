package com.triviaroyale.handler

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.model.Response
import com.triviaroyale.service.PlayerService
import com.triviaroyale.util.AppState
import com.triviaroyale.util.Messages
import com.triviaroyale.util.ResponseHelper
import com.triviaroyale.util.SessionAttributes
import groovy.transform.CompileStatic
import groovy.util.logging.Log

@CompileStatic
@Log
class SecondChanceBuyResponseHandler {

    static Optional<Response> accepted(HandlerInput input) {
        Map<String, Object> sessionAttributes = input.attributesManager.sessionAttributes

        int secondChancesAvailable = PlayerService.numberOfSecondChancesAvailable(sessionAttributes)
        String chancesPluralization = secondChancesAvailable == 1 ? 'Chance' : 'Chances'
        String preText = "Thank you for your purchase. You currently have $secondChancesAvailable Second " +
                "$chancesPluralization available."
        generateResponse(input, preText)
    }

    static Optional<Response> declined(HandlerInput input) {
        generateResponse(input, null)
    }

    static Optional<Response> error(HandlerInput input) {
        declined(input)
    }

    protected static Optional<Response> generateResponse(HandlerInput input, String preText) {
        Map<String, Object> sessionAttributes = input.attributesManager.sessionAttributes

        if (!sessionAttributes[SessionAttributes.APP_STATE]) {
            sessionAttributes.put(SessionAttributes.APP_STATE, AppState.ASK_TO_PLAY_TRIVIA_ROYALE)
            input.attributesManager.sessionAttributes = sessionAttributes
        }
        switch (sessionAttributes[SessionAttributes.APP_STATE]) {
            case AppState.IN_GAME:
                return ResumeGameIntentHandler.handle(input, preText)
            case AppState.NEW_GAME:
                return ResponseHelper.askToStartNewGame(input, preText)
            case AppState.RESUME_EXISTING_GAME:
                return newSessionResponse(input, preText, Messages.ASK_TO_RESUME_GAME)
            case AppState.START_OVER_REQUEST:
                return newSessionResponse(input, preText, Messages.CONFIRM_START_OVER)
            case AppState.ASK_TO_PLAY_TRIVIA_ROYALE:
                return newSessionResponse(input, preText, Messages.ASK_TO_PLAY_TRIVIA_ROYALE)
            case AppState.ASK_TO_USE_SECOND_CHANCE:
                return newSessionResponse(input, preText, Messages.ASK_TO_USE_SECOND_CHANCE)
            default:
                //we shouldn't ever need to execute this. Logging a message, then generating a session-ending response
                log.severe('Could not resume after SecondChanceBuyResponse.')
                return ResponseHelper.generateEndSessionResponse(input, preText).build()
        }
    }

    protected static Optional<Response> newSessionResponse(HandlerInput input, String preText, String message) {
        Map<String, Object> sessionAttributes = input.attributesManager.sessionAttributes
        String responseMessage = preText ?
                "$preText $message" : message
        sessionAttributes.put(SessionAttributes.LAST_RESPONSE, message)
        input.attributesManager.sessionAttributes = sessionAttributes
        ResponseHelper.generateResponse(input,
                responseMessage,
                Messages.ASK_TO_USE_SECOND_CHANCE).build()
    }

}
