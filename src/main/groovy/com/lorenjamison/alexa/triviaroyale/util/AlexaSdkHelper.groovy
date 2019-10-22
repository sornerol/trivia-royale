package com.lorenjamison.alexa.triviaroyale.util

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.model.IntentRequest
import com.amazon.ask.model.Slot

class AlexaSdkHelper {
    //TODO: Not sure if this is the best place for these constants to live
    static final String NAME_SLOT_KEY = "name"
    static final String CATEGORY_SLOT_KEY = "category"

    static String getUserId(HandlerInput input) {
        return input.requestEnvelope.context.system.user.userId
    }

    static String getSlotValue(HandlerInput input, String key) {
        IntentRequest request = (IntentRequest) input.requestEnvelope.request
        Map<String, Slot> slots = request.intent.slots
        slots[key].value
    }
}
