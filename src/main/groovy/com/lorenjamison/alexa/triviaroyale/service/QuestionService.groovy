package com.lorenjamison.alexa.triviaroyale.service

import com.lorenjamison.alexa.triviaroyale.data.Question
import com.lorenjamison.alexa.triviaroyale.data.Quiz

class QuestionService {
    static Question getQuizQuestion(long quizId, int questionNumber) {

    }

    static int chooseRandomCorrectAnswerIndex(Question question) {
        Random random = new Random(System.currentTimeMillis())
        random.nextInt(question.otherAnswers.size())
    }
}
