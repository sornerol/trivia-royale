package com.triviaroyale.handler

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.model.Response
import com.amazon.ask.model.interfaces.connections.SendRequestDirective
import com.amazon.ask.model.services.monetization.InSkillProduct
import com.amazon.ask.model.services.monetization.InSkillProductsResponse
import com.amazon.ask.model.services.monetization.MonetizationServiceClient
import com.amazon.ask.request.RequestHelper
import com.amazon.ask.response.ResponseBuilder
import com.triviaroyale.isp.IspUtil
import com.triviaroyale.util.AlexaSdkHelper
import com.triviaroyale.util.AppState
import com.triviaroyale.util.Messages
import com.triviaroyale.util.SessionAttributes
import groovy.transform.CompileStatic

@CompileStatic
class RefundSecondChanceIntentHandler {

    static Optional<Response> handle(HandlerInput input) {
        Map<String, Object> sessionAttributes = input.attributesManager.sessionAttributes
        RequestHelper requestHelper = RequestHelper.forHandlerInput(input)
        String locale = requestHelper.locale
        MonetizationServiceClient client = input.serviceClientFactory.monetizationService
        InSkillProductsResponse inSkillProductsResponse = client.getInSkillProducts(locale,
                null,
                null,
                null,
                null,
                null)

        InSkillProduct secondChance = IspUtil.getInSkillProductByName(inSkillProductsResponse,
                IspUtil.SECOND_CHANCE_PRODUCT)
        if (secondChance) {
            AlexaSdkHelper.saveCurrentSession(sessionAttributes)
            return input.responseBuilder
                    .addDirective(getCancelDirective(secondChance.productId))
                    .build()
        }

        if (!sessionAttributes[SessionAttributes.APP_STATE]) {
            sessionAttributes.put(SessionAttributes.LAST_RESPONSE, Messages.ASK_TO_PLAY_AFTER_HELP)
            sessionAttributes.put(SessionAttributes.APP_STATE, AppState.HELP_REQUEST)
        }
        String responseMessage = "$Messages.PRODUCT_NOT_FOUND ${sessionAttributes[SessionAttributes.LAST_RESPONSE]}"
        String repromptMessage = sessionAttributes[SessionAttributes.LAST_RESPONSE]

        ResponseBuilder response = AlexaSdkHelper.generateResponse(input, responseMessage, repromptMessage)

        response.build()
    }

    static SendRequestDirective getCancelDirective(String productId) {
        Map<String, Object> payload = [:]
        Map<String, Object> inskillProduct = [:]
        inskillProduct.put('productId', productId)
        payload.put('InSkillProduct', inskillProduct)

        SendRequestDirective directive = SendRequestDirective.builder()
                .withPayload(payload)
                .withName('Cancel')
                .withToken('correlationToken')
                .build()

        directive
    }

}
