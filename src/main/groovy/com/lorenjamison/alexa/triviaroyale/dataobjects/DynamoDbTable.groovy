package com.lorenjamison.alexa.triviaroyale.dataobjects

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

@Retention(RetentionPolicy.RUNTIME)
@interface DynamoDbTable {
    String value()
}
