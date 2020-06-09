package com.triviaroyale.handler

import static com.amazon.ask.request.Predicates.intentName
import static com.amazon.ask.request.Predicates.sessionAttribute

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.dispatcher.request.handler.RequestHandler
import com.amazon.ask.model.Response
import com.amazon.ask.response.ResponseBuilder
import com.triviaroyale.util.AlexaSdkHelper
import com.triviaroyale.util.AppState
import com.triviaroyale.util.SessionAttributes
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

@Slf4j
@CompileStatic
class RepeatIntentHandler implements RequestHandler {

    @Override
    boolean canHandle(HandlerInput input) {
        input.matches(intentName('AMAZON.RepeatIntent')) && (
                sessionAttribute(SessionAttributes.APP_STATE, AppState.IN_GAME.toString()) ||
                        sessionAttribute(SessionAttributes.APP_STATE, AppState.NEW_GAME.toString()))
    }

    @Override
    Optional<Response> handle(HandlerInput input) {
        log.debug('START RepeatIntentHandler.handle()')

        Map<String, Object> sessionAttributes = input.attributesManager.sessionAttributes
        String responseMessage = sessionAttributes[SessionAttributes.LAST_RESPONSE]
        String repromptMessage = responseMessage

        ResponseBuilder response = AlexaSdkHelper.responseWithSimpleCard(input, responseMessage, repromptMessage)
        response.build()
    }

}
