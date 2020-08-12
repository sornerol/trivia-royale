package com.triviaroyale.requestrouter

import static com.amazon.ask.request.Predicates.requestType

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.dispatcher.request.handler.RequestHandler
import com.amazon.ask.model.Response
import com.amazon.ask.model.SessionEndedRequest
import groovy.transform.CompileStatic

@CompileStatic
class SessionEndedRequestRouter implements RequestHandler {

    @Override
    boolean canHandle(HandlerInput input) {
        input.matches(requestType(SessionEndedRequest))
    }

    @Override
    Optional<Response> handle (HandlerInput input) {

    }

}
