package com.triviaroyale.requestrouter

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.dispatcher.request.handler.RequestHandler
import com.amazon.ask.model.Response
import groovy.transform.CompileStatic

@CompileStatic
class FallbackIntentRequestRouter implements RequestHandler {

    @Override
    boolean canHandle(HandlerInput input) {
        true
    }

    @Override
    Optional<Response> handle (HandlerInput input) {

    }

}
