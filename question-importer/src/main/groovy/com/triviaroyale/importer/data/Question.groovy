package com.triviaroyale.importer.data

import groovy.json.JsonOutput
import groovy.transform.CompileStatic

@CompileStatic
class Question {

    String questionText
    String correctAnswer
    List<String> otherAnswers


    String toJson() {
        JsonOutput.toJson(this)
    }

}
