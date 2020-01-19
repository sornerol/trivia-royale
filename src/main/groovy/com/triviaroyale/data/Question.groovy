package com.triviaroyale.data

import groovy.transform.CompileStatic

@CompileStatic
class Question {

    long id
    String questionText
    String correctAnswer
    List<String> otherAnswers

}
