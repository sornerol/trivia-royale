package com.triviaroyale.requestrouter

import static com.amazon.ask.request.Predicates.intentName

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.dispatcher.request.handler.RequestHandler
import com.amazon.ask.model.Response
import com.triviaroyale.handler.RefundSecondChanceIntentHandler
import com.triviaroyale.util.AlexaSdkHelper
import groovy.transform.CompileStatic

@CompileStatic
class RefundSecondChanceIntentRequestRouter implements RequestHandler {

    @Override
    boolean canHandle(HandlerInput input) {
        input.matches(intentName('RefundSecondChanceIntent'))
    }

    @Override
    Optional<Response> handle(HandlerInput input) {
        HandlerInput initializedInput = AlexaSdkHelper.initializeHandlerInput(input)
        RefundSecondChanceIntentHandler.handle(initializedInput)
    }

}
