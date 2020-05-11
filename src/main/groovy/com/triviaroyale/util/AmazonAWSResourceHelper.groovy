package com.triviaroyale.util

import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import groovy.transform.CompileStatic

@CompileStatic
class AmazonAWSResourceHelper {

    public static final String ACCESS_KEY_PROPERTY = 'aws.accesskey'
    public static final String SECRET_KEY_PROPERTY = 'aws.secretkey'
    public static final String REGION_KEY = 'aws.region'

    static AmazonDynamoDB openDynamoDBClient() {
        Properties properties = Configuration.loadFromClasspath()
        BasicAWSCredentials credentials = new BasicAWSCredentials(properties[ACCESS_KEY_PROPERTY] as String,
                properties[SECRET_KEY_PROPERTY] as String)
        AmazonDynamoDB dynamoDB = AmazonDynamoDBClientBuilder.standard()
                .withRegion(properties[REGION_KEY] as String)
                .withCredentials(credentials as AWSCredentialsProvider)
                .build()

        dynamoDB
    }

    static AmazonS3 openS3Client() {
        Properties properties = Configuration.loadFromClasspath()
        BasicAWSCredentials credentials = new BasicAWSCredentials(properties[ACCESS_KEY_PROPERTY] as String,
                properties[SECRET_KEY_PROPERTY] as String)
        AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                .withRegion(properties[REGION_KEY] as String)
                .withCredentials(credentials as AWSCredentialsProvider)
                .build()

        s3
    }

}
