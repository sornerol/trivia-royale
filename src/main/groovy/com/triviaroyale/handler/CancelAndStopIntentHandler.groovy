package com.triviaroyale.handler

import static com.amazon.ask.request.Predicates.intentName
import static com.amazon.ask.request.Predicates.sessionAttribute

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.dispatcher.request.handler.RequestHandler
import com.amazon.ask.model.Response
import com.amazon.ask.response.ResponseBuilder
import com.triviaroyale.util.AppState
import com.triviaroyale.util.Constants
import com.triviaroyale.util.Messages
import com.triviaroyale.util.SessionAttributes
import groovy.transform.CompileStatic
import groovy.util.logging.Log

@CompileStatic
@Log
class CancelAndStopIntentHandler implements RequestHandler {

    @Override
    boolean canHandle(HandlerInput input) {
        input.matches(intentName('AMAZON.CancelIntent')) ||
                input.matches(intentName('AMAZON.StopIntent')) ||
                input.matches(intentName('AMAZON.NoIntent') &
                        sessionAttribute(SessionAttributes.APP_STATE, AppState.NEW_GAME.toString()))
    }

    @Override
    Optional<Response> handle(HandlerInput input) {
        log.fine(Constants.ENTERING_LOG_MESSAGE)

        ResponseBuilder responseBuilder = input.responseBuilder
                .withSpeech(Messages.EXIT_SKILL)
                .withSimpleCard(Constants.SKILL_TITLE, Messages.EXIT_SKILL)
                .withShouldEndSession(true)

        log.fine(Constants.EXITING_LOG_MESSAGE)
        responseBuilder.build()
    }

}
