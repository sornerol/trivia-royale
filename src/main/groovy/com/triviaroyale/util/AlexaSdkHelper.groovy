package com.triviaroyale.util

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.model.IntentRequest
import com.amazon.ask.model.Slot
import com.amazon.ask.response.ResponseBuilder
import groovy.transform.CompileStatic

@CompileStatic
class AlexaSdkHelper {

    static final String NAME_SLOT_KEY = 'name'
    static final String ANSWER_SLOT_KEY = 'answer'

    static String getUserId(HandlerInput input) {
        input.requestEnvelope.context.system.user.userId
    }

    static String getSlotValue(HandlerInput input, String key) {
        IntentRequest request = (IntentRequest) input.requestEnvelope.request
        Map<String, Slot> slots = request.intent.slots
        slots[key].value
    }

    static int getSlotId(HandlerInput input, String key) {
        IntentRequest request = (IntentRequest) input.requestEnvelope.request
        Map<String, Slot> slots = request.intent.slots
        slots[key].resolutions.resolutionsPerAuthority[0].values[0].value.id.toInteger()
    }

    static ResponseBuilder generateResponse(HandlerInput input,
                                            String responseMessage,
                                            String repromptMessage) {
        ResponseBuilder responseBuilder = input.responseBuilder
        responseBuilder = responseBuilder
                .withSpeech(responseMessage)
                .withReprompt(repromptMessage)
                .withShouldEndSession(false)
        responseBuilder
    }

    static ResponseBuilder generateEndSessionResponse(HandlerInput input,
                                            String responseMessage) {
        ResponseBuilder responseBuilder = input.responseBuilder
        responseBuilder = responseBuilder
                .withSpeech(responseMessage)
                .withShouldEndSession(true)
        responseBuilder
    }

}
