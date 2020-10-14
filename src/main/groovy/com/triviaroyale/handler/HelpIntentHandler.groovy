package com.triviaroyale.handler

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.model.Response
import com.triviaroyale.util.Messages
import com.triviaroyale.util.ResponseHelper
import groovy.transform.CompileStatic

@CompileStatic
class HelpIntentHandler {

    static Optional<Response> handle(HandlerInput input) {
        ResponseHelper.informationalResponse(input, Messages.HELP_MESSAGE)
    }

}
