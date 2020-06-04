package com.triviaroyale.handler

import static com.amazon.ask.request.Predicates.requestType

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.dispatcher.request.handler.RequestHandler
import com.amazon.ask.model.Response
import com.amazon.ask.model.SessionEndedRequest
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

@Slf4j
@CompileStatic
class SessionEndedRequestHandler implements RequestHandler {

    @Override
    boolean canHandle(HandlerInput input) {
        input.matches(requestType(SessionEndedRequest))
    }

    @Override
    Optional<Response> handle(HandlerInput input) {
        log.debug('START SessionEndedRequestHandler.handle()')

        return null
    }

}
