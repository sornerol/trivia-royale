package com.triviaroyale.requestrouter

import static com.amazon.ask.request.Predicates.intentName

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.dispatcher.request.handler.RequestHandler
import com.amazon.ask.model.Response
import com.triviaroyale.handler.StartOverIntentHandler
import groovy.transform.CompileStatic
import groovy.util.logging.Log

@CompileStatic
@Log
class StartOverIntentRequestRouter implements RequestHandler {

    @Override
    boolean canHandle(HandlerInput input) {
        input.matches(intentName('AMAZON.StartOverIntent'))
    }

    @Override
    Optional<Response> handle (HandlerInput input) {
        log.fine('Request envelope: ' + input.requestEnvelopeJson.toString())

        StartOverIntentHandler.handle(input)
    }

}
