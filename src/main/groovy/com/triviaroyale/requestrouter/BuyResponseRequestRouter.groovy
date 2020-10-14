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
import com.triviaroyale.handler.SecondChanceUpsellResponseHandler
import com.triviaroyale.isp.IspUtil
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
    public static final String BUY = 'buy'
    public static final String UPSELL = 'upsell'

    @Override
    boolean canHandle(HandlerInput input, ConnectionsResponse connectionsResponse) {
        String name = getRequestName(input)
        name.equalsIgnoreCase(BUY) | name.equalsIgnoreCase(UPSELL)
    }

    @Override
    Optional<Response> handle(HandlerInput input, ConnectionsResponse connectionsResponse) {
        RequestHelper requestHelper = RequestHelper.forHandlerInput(input)
        String locale = requestHelper.locale
        MonetizationServiceClient client = input.serviceClientFactory.monetizationService
        String productId = input.requestEnvelopeJson
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
        String requestName = getRequestName(input)
        if (requestName.equalsIgnoreCase(BUY)) {
            return handleBuyResponse(input, connectionsResponse, inSkillProduct)
        }
        handleUpsellResponse(input, connectionsResponse, inSkillProduct)
    }

    protected static String getRequestName(HandlerInput input) {
        input.requestEnvelopeJson.get(REQUEST).get(NAME).asText()
    }

    protected static Optional<Response> handleBuyResponse(HandlerInput input,
                                                          ConnectionsResponse ignored,
                                                          InSkillProduct inSkillProduct) {
        String code = input.requestEnvelopeJson
                .get(REQUEST)
                .get(STATUS)
                .get(CODE)
                .asText()

        if (!inSkillProduct || code != SUCCESS_CODE) {
            log.severe('Buy error in Connections.Response. See RequestEnvelope for details.')
            return SecondChanceBuyResponseHandler.error(input)
        }

        String purchaseResult = input.requestEnvelopeJson
                .get(REQUEST)
                .get(PAYLOAD)
                .get(PURCHASE_RESULT)
                .asText()

        switch (purchaseResult) {
            case 'ACCEPTED':
                return SecondChanceBuyResponseHandler.accepted(input)
            case 'DECLINED':
                return SecondChanceBuyResponseHandler.declined(input)
            default:
                log.severe('Buy purchaseResult was ERROR. See RequestEnvelope for details.')
                return SecondChanceBuyResponseHandler.error(input)
        }
    }

    protected static Optional<Response> handleUpsellResponse(HandlerInput input,
                                                             ConnectionsResponse ignored,
                                                             InSkillProduct inSkillProduct) {
        String code = input.requestEnvelopeJson
                .get(REQUEST)
                .get(STATUS)
                .get(CODE)
                .asText()

        if (!inSkillProduct || code != SUCCESS_CODE) {
            log.severe('Upsell error in Connections.Response. See RequestEnvelope for details.')
            return SecondChanceUpsellResponseHandler.error(input)
        }

        String purchaseResult = input.requestEnvelopeJson
                .get(REQUEST)
                .get(PAYLOAD)
                .get(PURCHASE_RESULT)
                .asText()

        switch (purchaseResult) {
            case 'ACCEPTED':
                return SecondChanceUpsellResponseHandler.accepted(input)
            case 'DECLINED':
                return SecondChanceUpsellResponseHandler.declined(input)
            default:
                log.severe('Upsell purchaseResult was ERROR. See RequestEnvelope for details.')
                return SecondChanceUpsellResponseHandler.error(input)
        }
    }

}
