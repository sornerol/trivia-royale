package com.lorenjamison.alexa.triviaroyale.util

import com.amazon.ask.dispatcher.request.handler.HandlerInput

class AlexaSdkHelper {
    static getUserId(HandlerInput input) {
        return input.requestEnvelope.context.system.user.userId
    }
}
