package com.triviaroyale.handler

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.model.Response
import com.amazon.ask.model.services.monetization.InSkillProductsResponse
import com.amazon.ask.model.services.monetization.MonetizationServiceClient
import com.amazon.ask.request.RequestHelper
import com.triviaroyale.util.Messages
import com.triviaroyale.util.ResponseHelper
import groovy.transform.CompileStatic

@CompileStatic
class WhatCanIBuyIntentHandler {

    static Optional<Response> handle(HandlerInput input) {
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
        String preText = inSkillProductsResponse.inSkillProducts.size() > 0 ?
                Messages.WHAT_CAN_I_BUY : Messages.NO_PRODUCTS_TO_BUY

        ResponseHelper.informationalResponse(input, preText)
    }

}
