package com.triviaroyale.service

import com.amazonaws.services.s3.AmazonS3
import com.triviaroyale.data.Question
import groovy.transform.CompileStatic

@CompileStatic
class QuestionService {

    public static final String GENERAL_CATEGORY = 'GENERAL'

    final AmazonS3 s3
    final String bucket

    QuestionService(AmazonS3 s3, String bucket) {
        this.s3 = s3
        this.bucket = bucket
    }

    List<Question> fetchRandomQuestionsForCategory(int numberOfQuestions, String category) {

    }

    private List<String> fetchQuestionPoolForCategory(String category) {

    }

    private String fetchQuestionFromS3(String objectId) {

    }

}
