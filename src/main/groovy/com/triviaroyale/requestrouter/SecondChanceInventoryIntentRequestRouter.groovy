package com.triviaroyale.requestrouter

import static com.amazon.ask.request.Predicates.intentName

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.dispatcher.request.handler.RequestHandler
import com.amazon.ask.model.Response
import com.triviaroyale.handler.SecondChanceInventoryIntentHandler
import groovy.transform.CompileStatic

@CompileStatic
class SecondChanceInventoryIntentRequestRouter implements RequestHandler {

    @Override
    boolean canHandle(HandlerInput input) {
        input.matches(intentName('SecondChanceInventoryIntent'))
    }

    @Override
    Optional<Response> handle(HandlerInput input) {
        SecondChanceInventoryIntentHandler.handle(input)
    }

}
