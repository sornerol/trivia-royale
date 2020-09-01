package com.triviaroyale.requestrouter

import static com.amazon.ask.request.Predicates.requestType

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.dispatcher.request.handler.RequestHandler
import com.amazon.ask.model.Response
import com.amazon.ask.model.SessionEndedRequest
import com.triviaroyale.handler.SessionEndedRequestHandler
import groovy.transform.CompileStatic
import groovy.util.logging.Log

@CompileStatic
@Log
class SessionEndedRequestRouter implements RequestHandler {

    @Override
    boolean canHandle(HandlerInput input) {
        input.matches(requestType(SessionEndedRequest))
    }

    @Override
    Optional<Response> handle (HandlerInput input) {
        log.fine('Request envelope: ' + input.requestEnvelopeJson.toString())

        SessionEndedRequestHandler.handle(input)
    }

}
