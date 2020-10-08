package com.triviaroyale.requestrouter

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.dispatcher.request.handler.impl.ConnectionsResponseHandler
import com.amazon.ask.model.Response
import com.amazon.ask.model.interfaces.connections.ConnectionsResponse
import com.amazon.ask.model.services.monetization.InSkillProduct
import com.amazon.ask.model.services.monetization.InSkillProductsResponse
import com.amazon.ask.model.services.monetization.MonetizationServiceClient
import com.amazon.ask.request.RequestHelper
import com.triviaroyale.handler.SecondChanceBuyResponseHandler
import com.triviaroyale.isp.IspUtil
import com.triviaroyale.util.AlexaSdkHelper
import groovy.transform.CompileStatic
import groovy.util.logging.Log

@CompileStatic
@Log
class BuyResponseRequestRouter implements ConnectionsResponseHandler {

    public static final String SUCCESS_CODE = '200'
    public static final String REQUEST = 'request'
    public static final String PAYLOAD = 'payload'
    public static final String PURCHASE_RESULT = 'purchaseResult'
    public static final String STATUS = 'status'
    public static final String CODE = 'code'
    public static final String PRODUCT_ID = 'productId'
    public static final String NAME = 'name'

    @Override
    boolean canHandle(HandlerInput input, ConnectionsResponse connectionsResponse) {
        String name = input.requestEnvelopeJson.get(REQUEST).get(NAME).asText()
        name.equalsIgnoreCase('buy') || name.equalsIgnoreCase('upsell')
    }

    @Override
    Optional<Response> handle(HandlerInput input, ConnectionsResponse connectionsResponse) {
        HandlerInput initializedInput = AlexaSdkHelper.initializeHandlerInput(input, true)

        RequestHelper requestHelper = RequestHelper.forHandlerInput(initializedInput)
        String locale = requestHelper.locale
        MonetizationServiceClient client = initializedInput.serviceClientFactory.monetizationService
        String productId = initializedInput.requestEnvelopeJson
                .get(REQUEST)
                .get(PAYLOAD)
                .get(PRODUCT_ID)
                .asText()

        InSkillProductsResponse response = client.getInSkillProducts(
                locale,
                null,
                null,
                null,
                null,
                null)

        //TODO: Refactor to be usable with other in-skill products
        InSkillProduct inSkillProduct = IspUtil.getInSkillProductById(response, productId)
        String code = initializedInput.requestEnvelopeJson
                .get(REQUEST)
                .get(STATUS)
                .get(CODE)
                .asText()

        if (!inSkillProduct || code != SUCCESS_CODE) {
            log.severe('Error detected in Connections.Response. See RequestEnvelope for details.')
            return SecondChanceBuyResponseHandler.error(initializedInput)
        }

        String purchaseResult = initializedInput.requestEnvelopeJson
                .get(REQUEST)
                .get(PAYLOAD)
                .get(PURCHASE_RESULT)
                .asText()

        switch (purchaseResult) {
            case 'ACCEPTED':
                return SecondChanceBuyResponseHandler.accepted(initializedInput)
            case 'DECLINED':
                return SecondChanceBuyResponseHandler.declined(initializedInput)
            default:
                log.severe('PurchaseResult was ERROR. See RequestEnvelope for details.')
                return SecondChanceBuyResponseHandler.error(initializedInput)
        }
    }

}
