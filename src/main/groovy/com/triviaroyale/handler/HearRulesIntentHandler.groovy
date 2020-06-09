package com.triviaroyale.handler

import static com.amazon.ask.request.Predicates.intentName

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.dispatcher.request.handler.RequestHandler
import com.amazon.ask.model.Response
import com.amazon.ask.response.ResponseBuilder
import com.triviaroyale.util.AlexaSdkHelper
import com.triviaroyale.util.AppState
import com.triviaroyale.util.Messages
import com.triviaroyale.util.SessionAttributes
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

@Slf4j
@CompileStatic
class HearRulesIntentHandler implements RequestHandler {

    @Override
    boolean canHandle(HandlerInput input) {
        input.matches(intentName('AMAZON.HelpIntent'))
    }

    @Override
    Optional<Response> handle(HandlerInput input) {
        log.debug('START HearRulesIntentHandler.handle()')

        Map<String, Object> sessionAttributes = input.attributesManager.sessionAttributes
        String responseMessage = Messages.RULES
        String repromptMessage

        String nextActionMessage
        if (sessionAttributes[SessionAttributes.APP_STATE] == AppState.NEW_PLAYER_SETUP) {
            nextActionMessage = Messages.ASK_FOR_NAME
        } else {
            nextActionMessage = sessionAttributes[SessionAttributes.LAST_RESPONSE]
        }

        responseMessage += " $nextActionMessage"
        repromptMessage = nextActionMessage

        ResponseBuilder response = AlexaSdkHelper.responseWithSimpleCard(input, responseMessage, repromptMessage)
        response.build()
    }

}
