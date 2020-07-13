package com.triviaroyale.handler

import static com.amazon.ask.request.Predicates.intentName

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.dispatcher.request.handler.RequestHandler
import com.amazon.ask.model.Response
import com.amazon.ask.response.ResponseBuilder
import com.triviaroyale.util.AlexaSdkHelper
import com.triviaroyale.util.Constants
import com.triviaroyale.util.SessionAttributes
import groovy.transform.CompileStatic
import groovy.util.logging.Log

@CompileStatic
@Log
class RepeatIntentHandler implements RequestHandler {

    @Override
    boolean canHandle(HandlerInput input) {
        input.matches(intentName('AMAZON.RepeatIntent'))
    }

    @Override
    Optional<Response> handle(HandlerInput input) {
        log.entering(this.class.name, Constants.HANDLE_METHOD)

        Map<String, Object> sessionAttributes = input.attributesManager.sessionAttributes
        String responseMessage = sessionAttributes[SessionAttributes.LAST_RESPONSE]
        String repromptMessage = responseMessage

        ResponseBuilder response = AlexaSdkHelper.generateResponse(input, responseMessage, repromptMessage)
        log.exiting(this.class.name, Constants.HANDLE_METHOD)

        response.build()
    }

}
