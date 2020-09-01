package com.triviaroyale.requestrouter

import static com.amazon.ask.request.Predicates.intentName

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.dispatcher.request.handler.RequestHandler
import com.amazon.ask.model.Response
import com.triviaroyale.handler.CancelAndStopIntentHandler
import groovy.transform.CompileStatic
import groovy.util.logging.Log

@CompileStatic
@Log
class CancelAndStopIntentRequestRouter implements RequestHandler {

    @Override
    boolean canHandle(HandlerInput input) {
        input.matches(intentName('AMAZON.CancelIntent') | intentName('AMAZON.StopIntent'))
    }

    @Override
    Optional<Response> handle(HandlerInput input) {
        log.fine('Request envelope: ' + input.requestEnvelopeJson.toString())
        CancelAndStopIntentHandler.handle(input)
    }

}
