package com.triviaroyale.handler

import static com.amazon.ask.request.Predicates.intentName
import static com.amazon.ask.request.Predicates.sessionAttribute

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.dispatcher.request.handler.RequestHandler
import com.amazon.ask.model.Response
import com.amazon.ask.response.ResponseBuilder
import com.triviaroyale.util.*
import groovy.transform.CompileStatic
import groovy.util.logging.Log

@CompileStatic
@Log
class StartOverIntentHandler implements RequestHandler {

    @Override
    boolean canHandle(HandlerInput input) {
        input.matches(intentName('AMAZON.StartOverIntent') &
                sessionAttribute(SessionAttributes.APP_STATE, AppState.IN_GAME.toString()))
    }

    @Override
    Optional<Response> handle(HandlerInput input) {
        log.fine(Constants.ENTERING_LOG_MESSAGE)
        Map<String, Object> sessionAttributes = input.attributesManager.sessionAttributes
        sessionAttributes[SessionAttributes.APP_STATE] = AppState.START_OVER_REQUEST

        sessionAttributes[SessionAttributes.LAST_RESPONSE] = Messages.CONFIRM_START_OVER

        input.attributesManager.sessionAttributes = sessionAttributes
        ResponseBuilder responseBuilder = AlexaSdkHelper.generateResponse(input,
                Messages.CONFIRM_START_OVER,
                Messages.CONFIRM_START_OVER)
        log.fine(Constants.EXITING_LOG_MESSAGE)
        responseBuilder.build()
    }

}
