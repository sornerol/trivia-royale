package com.triviaroyale.service

import com.triviaroyale.data.Question

class QuestionService {
    static Question getQuizQuestion(long quizId, int questionNumber) {

    }

    static int chooseRandomCorrectAnswerIndex(Question question) {
        Random random = new Random(System.currentTimeMillis())
        random.nextInt(question.otherAnswers.size())
    }
}
