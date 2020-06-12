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

import java.util.logging.Logger

@CompileStatic
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
        Logger logger = Logger.getLogger(this.class.name)
        logger.level = Constants.LOG_LEVEL
        logger.entering(this.class.name, Constants.HANDLE_METHOD)

        ResponseBuilder responseBuilder = input.responseBuilder
                .withSpeech(Messages.EXIT_SKILL)
                .withSimpleCard(Constants.SKILL_TITLE, Messages.EXIT_SKILL)
                .withShouldEndSession(true)

        logger.exiting(this.class.name, Constants.HANDLE_METHOD)
        responseBuilder.build()
    }

}
