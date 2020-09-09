package com.triviaroyale.requestrouter

import static com.amazon.ask.request.Predicates.intentName
import static com.amazon.ask.request.Predicates.sessionAttribute

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.dispatcher.request.handler.RequestHandler
import com.amazon.ask.model.Response
import com.triviaroyale.handler.CancelAndStopIntentHandler
import com.triviaroyale.handler.FallbackRequestHandler
import com.triviaroyale.handler.NewGameIntentHandler
import com.triviaroyale.handler.RepeatIntentHandler
import com.triviaroyale.util.AlexaSdkHelper
import com.triviaroyale.util.AppState
import com.triviaroyale.util.SessionAttributes
import groovy.transform.CompileStatic
import groovy.util.logging.Log

@CompileStatic
@Log
class NoIntentRequestRouter implements RequestHandler {

    @Override
    boolean canHandle(HandlerInput input) {
        input.matches(intentName('AMAZON.NoIntent'))
    }

    @Override
    Optional<Response> handle(HandlerInput input) {
        log.info('Request envelope: ' + input.requestEnvelopeJson.toString())
        if (!input.attributesManager.sessionAttributes[SessionAttributes.APP_STATE]) {
            log.severe('Received intent for uninitialized session. Exiting...')
            return AlexaSdkHelper.endSessionWithoutSpeech(input)
        }

        if (input.matches(sessionAttribute(SessionAttributes.APP_STATE, AppState.NEW_GAME.toString()))) {
            return CancelAndStopIntentHandler.handle(input)
        }
        if (input.matches(sessionAttribute(SessionAttributes.APP_STATE, AppState.RESUME_EXISTING_GAME.toString()))) {
            return NewGameIntentHandler.handle(input)
        }
        if (input.matches(sessionAttribute(SessionAttributes.APP_STATE, AppState.START_OVER_REQUEST.toString()))) {
            return RepeatIntentHandler.handle(input)
        }
        if (input.matches(sessionAttribute(SessionAttributes.APP_STATE, AppState.HELP_REQUEST.toString()))) {
            return AlexaSdkHelper.endSessionWithoutSpeech(input)
        }

        FallbackRequestHandler.handle(input)
    }

}
