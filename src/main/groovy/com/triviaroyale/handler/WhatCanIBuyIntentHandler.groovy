package com.triviaroyale.handler

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.model.Response
import com.amazon.ask.model.services.monetization.InSkillProductsResponse
import com.amazon.ask.model.services.monetization.MonetizationServiceClient
import com.amazon.ask.request.RequestHelper
import com.amazon.ask.response.ResponseBuilder
import com.triviaroyale.util.AlexaSdkHelper
import com.triviaroyale.util.AppState
import com.triviaroyale.util.Messages
import com.triviaroyale.util.SessionAttributes
import groovy.transform.CompileStatic

@CompileStatic
class WhatCanIBuyIntentHandler {

    static Optional<Response> handle(HandlerInput input) {
        Map<String, Object> sessionAttributes = input.attributesManager.sessionAttributes
        RequestHelper requestHelper = RequestHelper.forHandlerInput(input)
        String locale = requestHelper.locale
        MonetizationServiceClient client = input.serviceClientFactory.monetizationService
        InSkillProductsResponse inSkillProductsResponse = client.getInSkillProducts(locale,
                'PURCHASABLE',
                null,
                null,
                null,
                null)

        //TODO: This will need to be more robust if other products are added.
        String responseMessage = inSkillProductsResponse.inSkillProducts.size() > 0 ?
                Messages.WHAT_CAN_I_BUY : Messages.NO_PRODUCTS_TO_BUY

        if (!sessionAttributes[SessionAttributes.APP_STATE]) {
            sessionAttributes.put(SessionAttributes.LAST_RESPONSE, Messages.ASK_TO_PLAY_AFTER_HELP)
            sessionAttributes.put(SessionAttributes.APP_STATE, AppState.HELP_REQUEST)
        }
        responseMessage += " ${sessionAttributes[SessionAttributes.LAST_RESPONSE]}"
        String repromptMessage = sessionAttributes[SessionAttributes.LAST_RESPONSE]

        ResponseBuilder response = AlexaSdkHelper.generateResponse(input, responseMessage, repromptMessage)

        response.build()
    }

}
