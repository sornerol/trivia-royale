package com.triviaroyale.isp

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.model.Response
import com.amazon.ask.model.interfaces.connections.SendRequestDirective
import com.amazon.ask.model.services.monetization.InSkillProduct
import com.amazon.ask.model.services.monetization.InSkillProductsResponse
import com.amazon.ask.model.services.monetization.MonetizationServiceClient
import com.amazon.ask.request.RequestHelper
import com.triviaroyale.service.bean.AnswerValidationBean
import com.triviaroyale.util.Messages
import groovy.transform.CompileStatic
import groovy.util.logging.Log

@CompileStatic
@Log
class SecondChanceSeller {

    static Optional<Response> attemptSecondChanceSale(HandlerInput input,
                                                      AnswerValidationBean answerValidationBean) {
        RequestHelper requestHelper = RequestHelper.forHandlerInput(input)
        String locale = requestHelper.locale
        MonetizationServiceClient monetizationServiceClient = input.serviceClientFactory.monetizationService
        InSkillProductsResponse products = monetizationServiceClient.getInSkillProducts(
                locale,
                'PURCHASABLE',
                null,
                null,
                null,
                null)

        InSkillProduct secondChance = IspUtil.getInSkillProductByName(products, IspUtil.SECOND_CHANCE_PRODUCT)

        if (!secondChance) {
            log.info("Could not get Second Chance ISP for $answerValidationBean.updatedGameState.sessionId")
            return null
        }

        String secondChanceOffer = "$answerValidationBean.validationMessage $Messages.SECOND_CHANCE_OFFER"
        SendRequestDirective sellDirective = getSellDirective(secondChance.productId, secondChanceOffer)
        input.responseBuilder.addDirective(sellDirective).build()
    }

    static SendRequestDirective getSellDirective(String productId, String upsellMessage) {
        Map<String, Object> mapObject = [:]
        Map<String, Object> inskillProduct = [:]
        inskillProduct.put('productId', productId)
        mapObject.put('upsellMessage', upsellMessage)
        mapObject.put('InSkillProduct', inskillProduct)

        SendRequestDirective.builder()
                .withPayload(mapObject)
                .withName('Upsell')
                .withToken('correlationToken')
                .build()
    }

}
