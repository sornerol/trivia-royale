package com.triviaroyale.data

import spock.lang.Specification

class QuestionTest extends Specification {
    public static final String TEST_QUESTION_JSON = '{"questionText":"Who let the dogs out?","correctAnswer":"Who","otherAnswers":["You","No one","Your mama"]}'
    public static final String TEST_QUESTION_TEXT = 'Who let the dogs out?'
    public static final String TEST_QUESTION_ANSWER = 'Who'
    public static final List<String> TEST_QUESTION_OTHER_ANSWERS = ['You', 'No one', 'Your mama']

    def 'Create question from JSON'() {
        setup:
        Question question
        when:
        question = Question.fromJson(TEST_QUESTION_JSON)
        then:
        question.questionText == TEST_QUESTION_TEXT
        question.correctAnswer == TEST_QUESTION_ANSWER
        question.otherAnswers == TEST_QUESTION_OTHER_ANSWERS
    }

    def 'Export question to JSON'() {
        setup:
        Question question = new Question()
        question.with {
            questionText = TEST_QUESTION_TEXT
            correctAnswer = TEST_QUESTION_ANSWER
            otherAnswers = TEST_QUESTION_OTHER_ANSWERS
        }
        when:
        String json = question.toJson()
        then:
        //I'm not comparing the output JSON to the JSON above since I'm not guaranteed that the generated JSON
        //will be in the same order every time.
        json.contains(TEST_QUESTION_TEXT)
        json.contains(TEST_QUESTION_ANSWER)
        json.contains(TEST_QUESTION_OTHER_ANSWERS[0])
    }
}
