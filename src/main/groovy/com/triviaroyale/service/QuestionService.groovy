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
        List<Question> questionList = []
        Stack<String> questionPool = fetchQuestionPoolForCategory(category)
        Collections.shuffle(questionPool)
        for (int i = 0; i < numberOfQuestions; i++) {
            String nextQuestionKey = questionPool.pop()
            String nextQuestionJson = s3.getObjectAsString(bucket, nextQuestionKey)
            questionList.add(Question.fromJson(nextQuestionKey))
        }
        questionList
    }

    private Stack<String> fetchQuestionPoolForCategory(String category) {

    }

    private String fetchQuestionFromS3(String objectId) {

    }

}
