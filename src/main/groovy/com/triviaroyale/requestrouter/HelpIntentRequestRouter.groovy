package com.triviaroyale.requestrouter

import static com.amazon.ask.request.Predicates.intentName

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.dispatcher.request.handler.RequestHandler
import com.amazon.ask.model.Response
import com.triviaroyale.handler.HearRulesIntentHandler
import groovy.transform.CompileStatic
import groovy.util.logging.Log

@CompileStatic
@Log
class HelpIntentRequestRouter implements RequestHandler {

    @Override
    boolean canHandle(HandlerInput input) {
        input.matches(intentName('AMAZON.HelpIntent'))
    }

    @Override
    Optional<Response> handle (HandlerInput input) {
        log.fine('Request envelope: ' + input.requestEnvelopeJson.toString())
        HearRulesIntentHandler.handle(input)
    }

}
