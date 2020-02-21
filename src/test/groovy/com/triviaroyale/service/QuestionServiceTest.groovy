package com.triviaroyale.service

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.AnonymousAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3Client
import com.triviaroyale.data.Question
import io.findify.s3mock.S3Mock
import spock.lang.Shared
import spock.lang.Specification

class QuestionServiceTest extends Specification {

    //TODO: The path needs to be genericized to work in a CD build environment
    public static final String S3_MOCK_PATH = '/home/loren/trivia-royale-s3'
    public static final int S3_MOCK_PORT = 8001
    public static final String ENDPOINT = 'http://localhost'
    public static final String REGION = 'us-west-2'
    public static final String BUCKET = 'triviaroyale'

    @Shared
    S3Mock api
    @Shared
    QuestionService sut

    def setupSpec() {
        api = new S3Mock.Builder().withPort(S3_MOCK_PORT).withFileBackend(S3_MOCK_PATH).build()
        api.start()
        EndpointConfiguration endpoint = new EndpointConfiguration("$ENDPOINT:$S3_MOCK_PORT", REGION)
        AmazonS3 client = AmazonS3Client.builder().standard()
                .withPathStyleAccessEnabled(true)
                .withEndpointConfiguration(endpoint)
                .withCredentials(new AWSStaticCredentialsProvider(new AnonymousAWSCredentials()))
                .build()
        sut = new QuestionService(client, BUCKET)
    }

    def cleanupSpec() {
        api.shutdown()
    }

    def "fetchQuestionPoolForCategory"() {
        setup:
        Stack<String> questionPool
        when:
        questionPool = sut.fetchQuestionPoolForCategory(QuestionService.GENERAL_CATEGORY)
        then:
        questionPool.size() == 20
        println(questionPool.toListString())
    }

    def "FetchRandomQuestionsForCategory"() {
        setup:
        List<Question> questions
        when:
        questions = sut.fetchRandomQuestionsForCategory(10, QuestionService.GENERAL_CATEGORY)
        then:
        questions.size() == 10
    }

}
