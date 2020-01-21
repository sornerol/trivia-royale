package com.triviaroyale.util

import spock.lang.Specification

class ConfigurationTest extends Specification {

    def 'Load test file'(){
        setup:
        Properties testProperties = Configuration.loadFromClasspath()
        expect:
        testProperties[Configuration.TEST_PROPERTY] == 'foobar'
    }
}
