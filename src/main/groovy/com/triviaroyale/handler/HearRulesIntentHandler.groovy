package com.triviaroyale.handler

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.dispatcher.request.handler.RequestHandler
import com.amazon.ask.model.Response
import com.amazon.ask.response.ResponseBuilder
import com.triviaroyale.util.AlexaSdkHelper
import com.triviaroyale.util.Messages
import com.triviaroyale.util.SessionAttributes

import static com.amazon.ask.request.Predicates.intentName

class HearRulesIntentHandler implements RequestHandler {
    @Override
    boolean canHandle(HandlerInput input) {
        input.matches(intentName("AMAZON.HelpIntent"))
    }

    @Override
    Optional<Response> handle(HandlerInput input) {
        Map<String, Object> sessionAttributes = input.attributesManager.sessionAttributes
        String responseMessage = Messages.RULES
        String repromptMessage

        String nextActionMessage = sessionAttributes[SessionAttributes.LAST_RESPONSE]
        responseMessage += nextActionMessage
        repromptMessage = nextActionMessage

        ResponseBuilder response = AlexaSdkHelper.responseWithSimpleCard(input, responseMessage, repromptMessage)
        response.build()
    }
}