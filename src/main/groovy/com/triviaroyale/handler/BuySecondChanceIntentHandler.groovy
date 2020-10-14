package com.triviaroyale.handler

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.model.Response
import com.amazon.ask.model.interfaces.connections.SendRequestDirective
import com.amazon.ask.model.services.monetization.InSkillProduct
import com.amazon.ask.model.services.monetization.InSkillProductsResponse
import com.amazon.ask.model.services.monetization.MonetizationServiceClient
import com.amazon.ask.request.RequestHelper
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.triviaroyale.data.Player
import com.triviaroyale.isp.IspUtil
import com.triviaroyale.service.PlayerService
import com.triviaroyale.util.AlexaSdkHelper
import com.triviaroyale.util.Messages
import com.triviaroyale.util.ResponseHelper
import com.triviaroyale.util.SessionAttributes
import groovy.transform.CompileStatic

@CompileStatic
class BuySecondChanceIntentHandler {

    static Optional<Response> handle(HandlerInput input) {
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
            return ResponseHelper.informationalResponse(input, Messages.NO_PRODUCTS_TO_BUY)
        }

        PlayerService playerService = new PlayerService(AmazonDynamoDBClientBuilder.defaultClient())
        Map<String, Object> sessionAttributes = input.attributesManager.sessionAttributes
        if (sessionAttributes[SessionAttributes.SESSION_ID]) {
            Player player = PlayerService.getPlayerFromSessionAttributes(sessionAttributes)
            playerService.setIspSessionId(player, sessionAttributes)
            AlexaSdkHelper.saveCurrentSession(sessionAttributes)
        }
        SendRequestDirective sellDirective = IspUtil.getBuyDirective(secondChance.productId)
        input.responseBuilder.addDirective(sellDirective).build()
    }

}
