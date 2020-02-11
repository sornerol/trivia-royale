package com.triviaroyale.service

import spock.lang.Shared
import spock.lang.Specification
import io.findify.s3mock.S3Mock

class QuestionServiceTest extends Specification {

    //TODO: The path needs to be genericized to work in a CD build environment
    public static final String S3_MOCK_PATH = '/home/loren/trivia-royale-s3'
    public static final int S3_MOCK_PORT = 8001

    @Shared
    S3Mock api

    def setupSpec() {
        api = new S3Mock.Builder().withPort(S3_MOCK_PORT).withFileBackend(S3_MOCK_PATH).build()
        api.start()
    }

    def cleanupSpec() {
        api.shutdown()
    }

    def "FetchRandomQuestionsForCategory"() {
    }

}
