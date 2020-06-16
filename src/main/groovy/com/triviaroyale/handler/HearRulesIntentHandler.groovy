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
        input.matches(intentName('AMAZON.HelpIntent'))
    }

    @Override
    Optional<Response> handle(HandlerInput input) {
        log.level = Constants.LOG_LEVEL
        log.entering(this.class.name, Constants.HANDLE_METHOD)

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

        ResponseBuilder response = AlexaSdkHelper.responseWithSimpleCard(input, responseMessage, repromptMessage)
        log.exiting(this.class.name, Constants.HANDLE_METHOD)

        response.build()
    }

}
