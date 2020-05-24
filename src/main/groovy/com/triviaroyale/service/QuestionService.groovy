package com.triviaroyale.service

import com.amazonaws.services.s3.AmazonS3
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
        Stack<String> questionPool = fetchQuestionPoolForCategory(category)
        Collections.shuffle(questionPool)
        for (int i = 0; i < numberOfQuestions; i++) {
            String nextQuestionKey = questionPool.pop()
            String nextQuestionJson = s3.getObjectAsString(bucket, nextQuestionKey)
            questionList.add(nextQuestionJson)
        }
        questionList
    }

    protected Stack<String> fetchQuestionPoolForCategory(String category) {
        Stack<String> questionPool = [] as Stack

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
