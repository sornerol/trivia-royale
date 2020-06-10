package com.triviaroyale.data

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.transform.CompileStatic

@CompileStatic
class Question {

    String questionText
    String correctAnswer
    List<String> otherAnswers

    static Question fromJson(String json) {
        JsonSlurper slurper = new JsonSlurper()
        Question question
        try {
            question = slurper.parseText(json) as Question
        } catch (IllegalArgumentException e) {
            println('Could not create Question from JSON: ' + json)
            throw e
        }
        question
    }

    String toJson() {
        JsonOutput.toJson(this)
    }

}
