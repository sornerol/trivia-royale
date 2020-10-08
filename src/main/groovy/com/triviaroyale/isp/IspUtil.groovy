package com.triviaroyale.isp

import com.amazon.ask.model.services.monetization.InSkillProduct
import com.amazon.ask.model.services.monetization.InSkillProductsResponse
import groovy.transform.CompileStatic

@CompileStatic
class IspUtil {

    public static final String SECOND_CHANCE_PRODUCT = 'secondChance'

    static InSkillProduct getInSkillProductByName(InSkillProductsResponse products, String referenceName) {
        products.inSkillProducts.find { it.referenceName == referenceName }
    }

    static InSkillProduct getInSkillProductById(InSkillProductsResponse products, String productId) {
        products.inSkillProducts.find { it.productId == productId }
    }

}
