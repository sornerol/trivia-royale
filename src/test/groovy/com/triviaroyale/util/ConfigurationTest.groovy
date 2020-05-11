package com.triviaroyale.util

import spock.lang.Specification

class ConfigurationTest extends Specification {

    public static final String TEST_PROPERTY = 'test.property'

    def 'Load test file'(){
        setup:
        Properties testProperties = Configuration.loadFromClasspath()
        expect:
        testProperties[TEST_PROPERTY] == 'foobar'
    }
}
