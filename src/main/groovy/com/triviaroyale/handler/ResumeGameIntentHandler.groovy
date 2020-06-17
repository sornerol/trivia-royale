package com.triviaroyale.handler

import static com.amazon.ask.request.Predicates.intentName
import static com.amazon.ask.request.Predicates.sessionAttribute

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.dispatcher.request.handler.RequestHandler
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
class ResumeGameIntentHandler implements RequestHandler {

    @Override
    boolean canHandle(HandlerInput input) {
        input.matches(intentName('AMAZON.YesIntent')) &&
                input.matches(sessionAttribute(SessionAttributes.APP_STATE, AppState.RESUME_EXISTING_GAME.toString()))
    }

    @Override
    Optional<Response> handle(HandlerInput input) {
        log.level = Constants.LOG_LEVEL
        log.entering(this.class.name, Constants.HANDLE_METHOD)
        Map<String, Object> sessionAttributes = input.attributesManager.sessionAttributes

        sessionAttributes[SessionAttributes.APP_STATE] = AppState.IN_GAME
        sessionAttributes = QuizService.updateSessionAttributesWithCurrentQuestion(sessionAttributes)
        input.attributesManager.sessionAttributes = sessionAttributes

        String responseText = "Question ${sessionAttributes[SessionAttributes.QUESTION_NUMBER] + 1}. " +
                "${sessionAttributes[SessionAttributes.LAST_RESPONSE] as String}"
        String repropmptText = sessionAttributes[SessionAttributes.LAST_RESPONSE] as String
        ResponseBuilder response = AlexaSdkHelper.generateResponse(input, responseText, repropmptText)
        log.exiting(this.class.name, Constants.HANDLE_METHOD)

        response.build()
    }

}
