package com.triviaroyale.handler

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.model.Response
import com.amazon.ask.response.ResponseBuilder
import com.triviaroyale.service.QuizService
import com.triviaroyale.util.AlexaSdkHelper
import com.triviaroyale.util.AppState
import com.triviaroyale.util.Constants
import com.triviaroyale.util.SessionAttributes
import groovy.transform.CompileStatic
import groovy.util.logging.Log

@CompileStatic
@Log
class ResumeGameIntentHandler {

    static Optional<Response> handle(HandlerInput input) {
        log.fine(Constants.ENTERING_LOG_MESSAGE)
        Map<String, Object> sessionAttributes = input.attributesManager.sessionAttributes

        sessionAttributes[SessionAttributes.APP_STATE] = AppState.IN_GAME
        sessionAttributes = QuizService.updateSessionAttributesWithCurrentQuestion(sessionAttributes)
        input.attributesManager.sessionAttributes = sessionAttributes

        String responseText = "Question ${(sessionAttributes[SessionAttributes.QUESTION_NUMBER] as int) + 1}. " +
                "${sessionAttributes[SessionAttributes.LAST_RESPONSE] as String}"
        String repropmptText = sessionAttributes[SessionAttributes.LAST_RESPONSE] as String
        ResponseBuilder response = AlexaSdkHelper.generateResponse(input, responseText, repropmptText)
        log.fine(Constants.EXITING_LOG_MESSAGE)

        response.build()
    }

}
