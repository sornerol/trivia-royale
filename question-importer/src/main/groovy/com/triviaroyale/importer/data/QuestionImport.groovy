package com.triviaroyale.importer.data

import com.opencsv.bean.CsvBindByName
import com.triviaroyale.importer.data.Question
import groovy.transform.CompileStatic

@CompileStatic
class QuestionImport {

    public static final Character OTHER_ANSWERS_DELIMITER = '|'

    @CsvBindByName
    String id

    @CsvBindByName
    String category

    @CsvBindByName
    String questionText

    @CsvBindByName
    String correctAnswer

    @CsvBindByName
    String otherAnswers

    Question toQuestion() {
        Question question = new Question()
        question.questionText = questionText
        question.correctAnswer = correctAnswer

        List<String> otherAnswerList = otherAnswers.tokenize(OTHER_ANSWERS_DELIMITER)
        question.otherAnswers = otherAnswerList

        question
    }

}
