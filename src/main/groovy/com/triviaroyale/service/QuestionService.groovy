package com.triviaroyale.service

import com.amazonaws.services.s3.AmazonS3
import com.triviaroyale.data.Question
import groovy.transform.CompileStatic

@CompileStatic
class QuestionService {

    public static final String GENERAL_CATEGORY = 'GENERAL'
    public static final String MANIFEST_FILE = 'MANIFEST'

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
            questionList.add(Question.fromJson(nextQuestionJson))
        }
        questionList
    }

    protected Stack<String> fetchQuestionPoolForCategory(String category) {
        Stack<String> questionPool = new Stack<String>()

        if (category == GENERAL_CATEGORY) {
            String manifest = s3.getObjectAsString(bucket, MANIFEST_FILE)
            manifest.eachLine { subCategory ->
                questionPool.addAll(fetchQuestionPoolForCategory(subCategory))
            }
        } else {
            String manifest = s3.getObjectAsString(bucket, category + '/' + MANIFEST_FILE)
            manifest.eachLine {question ->
                questionPool.add(category + '/' + question)
            }
        }
        questionPool
    }

}
