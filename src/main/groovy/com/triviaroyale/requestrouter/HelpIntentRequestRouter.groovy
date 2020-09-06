package com.triviaroyale.requestrouter

import static com.amazon.ask.request.Predicates.intentName

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.dispatcher.request.handler.RequestHandler
import com.amazon.ask.model.Response
import com.triviaroyale.handler.HelpIntentHandler
import com.triviaroyale.util.AlexaSdkHelper
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
        HandlerInput initializedInput = AlexaSdkHelper.initializeHandlerInput(input)
        log.fine('Request envelope: ' + initializedInput.requestEnvelopeJson.toString())
        HelpIntentHandler.handle(initializedInput)
    }

}
