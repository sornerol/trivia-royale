package com.triviaroyale.requestrouter

import static com.amazon.ask.request.Predicates.intentName

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.dispatcher.request.handler.RequestHandler
import com.amazon.ask.model.Response
import com.triviaroyale.handler.BuySecondChanceIntentHandler
import groovy.transform.CompileStatic

@CompileStatic
class BuySecondChanceIntentRequestRouter implements RequestHandler {

    @Override
    boolean canHandle(HandlerInput input) {
        input.matches(intentName('BuySecondChanceIntent'))
    }

    @Override
    Optional<Response> handle(HandlerInput input) {
        BuySecondChanceIntentHandler.handle(input)
    }

}
