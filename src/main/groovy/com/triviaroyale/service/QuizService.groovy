package com.triviaroyale.service

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.triviaroyale.data.GameState
import com.triviaroyale.data.Player
import com.triviaroyale.data.Question
import com.triviaroyale.data.Quiz
import com.triviaroyale.util.Constants
import com.triviaroyale.util.SessionAttributes
import groovy.transform.CompileStatic

import java.security.SecureRandom

@CompileStatic
class QuizService extends DynamoDBAccess {

    public static final char FIRST_ANSWER_LETTER = 'A'

    QuizService(AmazonDynamoDB dynamoDB) {
        super(dynamoDB)
    }

    static String getQuizIdAsString(Quiz quiz) {
        String quizId = quiz.category + '|' + quiz.uniqueId
        quizId
    }

    static List<Tuple2<String, List<Boolean>>> getRandomPlayersForQuiz(Quiz quiz, int numberOfPlayers) {
        List<Tuple2<String, List<Boolean>>> playerPool = quiz.playerPool
        List<Tuple2<String, List<Boolean>>> selectedPlayers = []
        Collections.shuffle(playerPool)
        for (int i = 0; i < numberOfPlayers; i++) {
            selectedPlayers.add(playerPool.pop())
        }

        selectedPlayers
    }

    static Map<String, Object> updateSessionAttributesWithCurrentQuestion(Map<String, Object> sessionAttributes) {
        List<String> questionList = sessionAttributes[SessionAttributes.QUESTION_LIST] as List<String>
        Question currentQuestion = Question.fromJson(
                questionList[sessionAttributes[SessionAttributes.QUESTION_NUMBER] as int])
        SecureRandom random = new SecureRandom()
        int correctAnswerIndex = random.nextInt(currentQuestion.otherAnswers.size() + 1)
        String questionText = generateQuestionText(currentQuestion, correctAnswerIndex)

        sessionAttributes.put(SessionAttributes.LAST_RESPONSE, questionText)
        sessionAttributes.put(SessionAttributes.CORRECT_ANSWER_INDEX, correctAnswerIndex)

        sessionAttributes
    }

    Quiz loadNextAvailableQuizForPlayer(Player player, String category = Constants.GENERAL_CATEGORY) {

    }

    Quiz generateNewQuiz(List<String> questions, String playerId, String category = Constants.GENERAL_CATEGORY) {

    }

    void addPerformanceToPool(GameState completedGame) {

    }

    protected static String generateQuestionText(Question question, int correctAnswerIndex) {
        int possibleAnswers = question.otherAnswers.size() + 1
        List<String> answers = []
        Collections.shuffle(question.otherAnswers)
        String answerLetter = FIRST_ANSWER_LETTER.toString()
        for (int i = 0; i < possibleAnswers; i++) {
            String formattedAnswer
            if (i == correctAnswerIndex) {
                formattedAnswer = "${answerLetter}. $question.correctAnswer\n"
            } else {
                formattedAnswer = "${answerLetter}. ${question.otherAnswers.pop()}\n"
            }
            answers[i] = formattedAnswer
            answerLetter++
        }

        String questionText = question.questionText + '\n'
        answers.each {
            answer -> questionText += answer
        }

        questionText
    }

}
