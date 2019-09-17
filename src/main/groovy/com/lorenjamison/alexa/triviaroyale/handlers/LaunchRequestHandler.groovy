package com.lorenjamison.alexa.triviaroyale.handlers

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.dispatcher.request.handler.RequestHandler
import com.amazon.ask.model.LaunchRequest
import com.amazon.ask.model.Response
import com.amazon.ask.response.ResponseBuilder

import static com.amazon.ask.request.Predicates.requestType

class LaunchRequestHandler implements RequestHandler{

    @Override
    boolean canHandle(HandlerInput input) {
        input.matches(requestType(LaunchRequest.class))
    }

    @Override
    Optional<Response> handle(HandlerInput input) {
        //TODO: Implement LaunchRequestHandler.handle
    }
}
