package com.triviaroyale.util

import groovy.transform.CompileStatic

@CompileStatic
class Configuration {

    public static final String TEST_PROPERTY = 'test.property'

    static Properties loadFromClasspath()
    {
        Properties properties = new Properties()
        try {
            InputStream input = Configuration.class.classLoader.getResourceAsStream('config.properties')
            properties.load(input)
        } catch (Exception e) {
            e.printStackTrace()
            throw e
        }

        properties
    }

}
