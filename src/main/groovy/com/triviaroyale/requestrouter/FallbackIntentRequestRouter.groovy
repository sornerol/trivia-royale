package com.triviaroyale.requestrouter

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.dispatcher.request.handler.RequestHandler
import com.amazon.ask.model.Response
import com.triviaroyale.handler.FallbackRequestHandler
import groovy.transform.CompileStatic
import groovy.util.logging.Log

@CompileStatic
@Log
class FallbackIntentRequestRouter implements RequestHandler {

    @Override
    boolean canHandle(HandlerInput input) {
        true
    }

    @Override
    Optional<Response> handle (HandlerInput input) {
        log.fine('Request envelope: ' + input.requestEnvelopeJson.toString())

        FallbackRequestHandler.handle(input)
    }

}
