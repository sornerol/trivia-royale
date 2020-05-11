package com.triviaroyale.util

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

@CompileStatic
@Slf4j
class Configuration {

    static Properties loadFromClasspath() {
        Properties properties = new Properties()
        try {
            InputStream input = Configuration.classLoader.getResourceAsStream('config.properties')
            properties.load(input)
        } catch (IOException e) {
            log.error('Could not find configuration file.')
            throw e
        }

        properties
    }

}
