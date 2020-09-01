package com.triviaroyale.requestrouter

import static com.amazon.ask.request.Predicates.intentName
import static com.amazon.ask.request.Predicates.sessionAttribute

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.dispatcher.request.handler.RequestHandler
import com.amazon.ask.model.Response
import com.triviaroyale.handler.AnswerQuestionIntentHandler
import com.triviaroyale.handler.FallbackRequestHandler
import com.triviaroyale.util.AppState
import com.triviaroyale.util.SessionAttributes
import groovy.transform.CompileStatic
import groovy.util.logging.Log

@CompileStatic
@Log
class AnswerIntentRequestRouter implements RequestHandler {

    @Override
    boolean canHandle(HandlerInput input) {
        input.matches(intentName('AnswerIntent'))
    }

    @Override
    Optional<Response> handle (HandlerInput input) {
        log.fine('Request envelope: ' + input.requestEnvelopeJson.toString())

        if (!input.matches(sessionAttribute(SessionAttributes.APP_STATE, AppState.IN_GAME.toString()))) {
            return FallbackRequestHandler.handle(input)
        }

        AnswerQuestionIntentHandler.handle(input)
    }

}