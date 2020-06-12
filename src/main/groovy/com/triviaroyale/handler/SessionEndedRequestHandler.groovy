package com.triviaroyale.handler

import static com.amazon.ask.request.Predicates.requestType

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.dispatcher.request.handler.RequestHandler
import com.amazon.ask.model.Response
import com.amazon.ask.model.SessionEndedRequest
import com.triviaroyale.util.Constants
import groovy.transform.CompileStatic

import java.util.logging.Logger

@CompileStatic
class SessionEndedRequestHandler implements RequestHandler {

    @Override
    boolean canHandle(HandlerInput input) {
        input.matches(requestType(SessionEndedRequest))
    }

    @Override
    Optional<Response> handle(HandlerInput input) {
        Logger logger = Logger.getLogger(this.class.name)
        logger.level = Constants.LOG_LEVEL
        logger.entering(this.class.name, 'handle')

        logger.exiting(this.class.name, 'handle')

        return null
    }

}
