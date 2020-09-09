package com.triviaroyale.requestrouter

import static com.amazon.ask.request.Predicates.requestType

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.dispatcher.request.handler.RequestHandler
import com.amazon.ask.model.LaunchRequest
import com.amazon.ask.model.Response
import com.triviaroyale.handler.LaunchRequestHandler
import com.triviaroyale.util.AlexaSdkHelper
import groovy.transform.CompileStatic
import groovy.util.logging.Log

@CompileStatic
@Log
class LaunchRequestRouter implements RequestHandler {

    @Override
    boolean canHandle(HandlerInput input) {
        input.matches(requestType(LaunchRequest))
    }

    @Override
    Optional<Response> handle (HandlerInput input) {
        log.info('Request envelope: ' + input.requestEnvelopeJson.toString())
        HandlerInput initializedInput = AlexaSdkHelper.initializeHandlerInput(input)
        LaunchRequestHandler.handle(initializedInput)
    }

}
