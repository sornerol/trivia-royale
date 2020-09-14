package com.triviaroyale.service

import com.amazonaws.services.s3.AmazonS3
import com.triviaroyale.data.Question
import com.triviaroyale.util.Constants
import groovy.transform.CompileStatic

@CompileStatic
class QuestionService {

    public static final String MANIFEST_FILE = 'MANIFEST'

    final AmazonS3 s3
    final String bucket

    QuestionService(AmazonS3 s3, String bucket) {
        this.s3 = s3
        this.bucket = bucket
    }

    List<String> fetchRandomQuestionsForCategory(int numberOfQuestions, String category = Constants.GENERAL_CATEGORY) {
        List<String> questionList = []
        List<String> questionPool = fetchQuestionPoolForCategory(category)
        Collections.shuffle(questionPool)
        for (int i = 0; i < numberOfQuestions; i++) {
            String questionKey = questionPool.pop()
            questionList.add(questionKey)
        }
        questionList
    }

    Question fetchQuestion(String questionKey) {
        Question.fromJson(s3.getObjectAsString(bucket, questionKey))
    }

    protected List<String> fetchQuestionPoolForCategory(String category) {
        List<String> questionPool = []

        if (category == Constants.GENERAL_CATEGORY) {
            String manifest = s3.getObjectAsString(bucket, MANIFEST_FILE)
            manifest.eachLine { subCategory ->
                questionPool.addAll(fetchQuestionPoolForCategory(subCategory))
            }
        } else {
            String manifest = s3.getObjectAsString(bucket, "$category/$MANIFEST_FILE")
            manifest.eachLine { question ->
                questionPool.add("$category/$question".toString())
            }
        }
        questionPool
    }

}
