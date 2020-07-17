package com.triviaroyale.handler

import static com.amazon.ask.request.Predicates.intentName

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.dispatcher.request.handler.RequestHandler
import com.amazon.ask.model.Response
import com.amazon.ask.response.ResponseBuilder
import com.triviaroyale.util.*
import groovy.transform.CompileStatic
import groovy.util.logging.Log

@CompileStatic
@Log
class HearRulesIntentHandler implements RequestHandler {

    @Override
    boolean canHandle(HandlerInput input) {
        log.fine('Request envelope: ' + input.requestEnvelopeJson.toString())

        input.matches(intentName('AMAZON.HelpIntent'))
    }

    @Override
    Optional<Response> handle(HandlerInput input) {
        log.fine(Constants.ENTERING_LOG_MESSAGE)

        Map<String, Object> sessionAttributes = input.attributesManager.sessionAttributes
        String responseMessage = Messages.RULES
        String repromptMessage

        String nextActionMessage
        if (sessionAttributes[SessionAttributes.APP_STATE] == AppState.NEW_PLAYER_SETUP.toString()) {
            nextActionMessage = Messages.ASK_FOR_NAME
        } else {
            nextActionMessage = sessionAttributes[SessionAttributes.LAST_RESPONSE]
        }

        responseMessage += " $nextActionMessage"
        repromptMessage = nextActionMessage

        ResponseBuilder response = AlexaSdkHelper.generateResponse(input, responseMessage, repromptMessage)
        log.fine(Constants.EXITING_LOG_MESSAGE)

        response.build()
    }

}
