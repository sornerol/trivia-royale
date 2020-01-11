package com.lorenjamison.triviaroyale.service

import com.lorenjamison.triviaroyale.data.Question

class QuestionService {
    static Question getQuizQuestion(long quizId, int questionNumber) {

    }

    static int chooseRandomCorrectAnswerIndex(Question question) {
        Random random = new Random(System.currentTimeMillis())
        random.nextInt(question.otherAnswers.size())
    }
}
