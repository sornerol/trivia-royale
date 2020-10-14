package com.triviaroyale.isp

import com.amazon.ask.model.interfaces.connections.SendRequestDirective
import com.amazon.ask.model.services.monetization.InSkillProduct
import com.amazon.ask.model.services.monetization.InSkillProductsResponse
import groovy.transform.CompileStatic

@CompileStatic
class IspUtil {

    public static final String SECOND_CHANCE_PRODUCT = 'secondChance'
    public static final String CORRELATION_TOKEN = 'correlationToken'
    public static final String PRODUCT_ID = 'productId'
    public static final String UPSELL_MESSAGE = 'upsellMessage'
    public static final String IN_SKILL_PRODUCT = 'InSkillProduct'

    static InSkillProduct getInSkillProductByName(InSkillProductsResponse products, String referenceName) {
        products.inSkillProducts.find { it.referenceName == referenceName }
    }

    static InSkillProduct getInSkillProductById(InSkillProductsResponse products, String productId) {
        products.inSkillProducts.find { it.productId == productId }
    }

    static SendRequestDirective getUpsellDirective(String productId, String upsellMessage) {
        Map<String, Object> payload = [:]
        Map<String, Object> inskillProduct = [:]
        inskillProduct.put(PRODUCT_ID, productId)
        payload.put(UPSELL_MESSAGE, upsellMessage)
        payload.put(IN_SKILL_PRODUCT, inskillProduct)

        SendRequestDirective.builder()
                .withPayload(payload)
                .withName('Upsell')
                .withToken(CORRELATION_TOKEN)
                .build()
    }

    static SendRequestDirective getBuyDirective(String productId) {
        Map<String, Object> payload = [:]
        Map<String, Object> inskillProduct = [:]
        inskillProduct.put(PRODUCT_ID, productId)
        payload.put(IN_SKILL_PRODUCT, inskillProduct)

        SendRequestDirective.builder()
                .withPayload(payload)
                .withName('Buy')
                .withToken(CORRELATION_TOKEN)
                .build()
    }

}
