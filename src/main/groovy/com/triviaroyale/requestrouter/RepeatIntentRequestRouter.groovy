package com.triviaroyale.requestrouter

import static com.amazon.ask.request.Predicates.intentName

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.dispatcher.request.handler.RequestHandler
import com.amazon.ask.model.Response
import com.triviaroyale.handler.RepeatIntentHandler
import com.triviaroyale.util.AlexaSdkHelper
import com.triviaroyale.util.SessionAttributes
import groovy.transform.CompileStatic
import groovy.util.logging.Log

@CompileStatic
@Log
class RepeatIntentRequestRouter implements RequestHandler {

    @Override
    boolean canHandle(HandlerInput input) {
        input.matches(intentName('AMAZON.RepeatIntent'))
    }

    @Override
    Optional<Response> handle (HandlerInput input) {
        if (!input.attributesManager.sessionAttributes[SessionAttributes.APP_STATE]) {
            log.severe('Received intent for uninitialized session. Exiting...')
            return AlexaSdkHelper.endSessionWithoutSpeech(input)
        }

        RepeatIntentHandler.handle(input)
    }

}
