package com.triviaroyale.data

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.transform.CompileStatic
import groovy.util.logging.Log

@CompileStatic
@Log
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
            log.severe('Could not create Question from JSON: ' + json)
            throw e
        }
        question
    }

    String toJson() {
        JsonOutput.toJson(this)
    }

}
