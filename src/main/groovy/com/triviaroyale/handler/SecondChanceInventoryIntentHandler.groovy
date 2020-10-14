package com.triviaroyale.handler

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.model.Response
import com.triviaroyale.service.PlayerService
import com.triviaroyale.util.ResponseHelper
import groovy.transform.CompileStatic

@CompileStatic
class SecondChanceInventoryIntentHandler {

    static Optional <Response> handle(HandlerInput input) {
        Map<String, Object> sessionAttributes = input.attributesManager.sessionAttributes

        int secondChancesAvailable = PlayerService.numberOfSecondChancesAvailable(sessionAttributes)
        String chancesPluralization = secondChancesAvailable == 1 ? 'Chance' : 'Chances'
        String preText = "You have $secondChancesAvailable Second $chancesPluralization available. If you want to " +
                'buy more, just say Buy Second Chances.'
        ResponseHelper.informationalResponse(input, preText)
    }

}
