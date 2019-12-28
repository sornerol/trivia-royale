package com.lorenjamison.alexa.triviaroyale.handler

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.dispatcher.request.handler.RequestHandler
import com.amazon.ask.model.Response
import com.amazon.ask.model.services.directive.DirectiveServiceClient
import com.amazon.ask.model.services.directive.SendDirectiveRequest
import com.amazon.ask.model.services.directive.SpeakDirective
import com.amazon.ask.response.ResponseBuilder
import com.lorenjamison.alexa.triviaroyale.data.Question
import com.lorenjamison.alexa.triviaroyale.data.Session
import com.lorenjamison.alexa.triviaroyale.service.SessionService
import com.lorenjamison.alexa.triviaroyale.service.QuestionService
import com.lorenjamison.alexa.triviaroyale.util.AlexaSdkHelper
import com.lorenjamison.alexa.triviaroyale.util.Constants
import com.lorenjamison.alexa.triviaroyale.util.GameState
import com.lorenjamison.alexa.triviaroyale.util.Messages
import com.lorenjamison.alexa.triviaroyale.util.SessionAttributes

import static com.amazon.ask.request.Predicates.intentName
import static com.amazon.ask.request.Predicates.sessionAttribute

class NewGameIntentHandler implements RequestHandler {
    public static final int FIRST_QUESTION = 1

    @Override
    boolean canHandle(HandlerInput input) {
        input.matches(intentName("NewGameIntent") & sessionAttribute(SessionAttributes.GAME_STATE, GameState.NEW_GAME))
    }

    @Override
    Optional<Response> handle(HandlerInput input) {
        Map<String, Object> sessionAttributes = input.attributesManager.sessionAttributes

        DirectiveServiceClient directiveServiceClient = input.serviceClientFactory.directiveService
        SpeakDirective speakDirective = SpeakDirective.builder().withSpeech(Messages.STARTING_NEW_GAME).build()
        //TODO: add audio clip
        SendDirectiveRequest sendDirectiveRequest = SendDirectiveRequest.builder().withDirective(speakDirective).build()
        directiveServiceClient.enqueue(sendDirectiveRequest)

        long playerId = (long) sessionAttributes[SessionAttributes.PLAYER_ID]

        Session newGame = SessionService.startNewSession(playerId)

        sessionAttributes.put(SessionAttributes.GAME_ID, newGame.id)
        sessionAttributes.put(SessionAttributes.PLAYERS_HEALTH, newGame.playersHealth)
        sessionAttributes.put(SessionAttributes.QUESTION_NUMBER, FIRST_QUESTION)

        Question question = QuestionService.getQuizQuestion(newGame.quizId, FIRST_QUESTION)

        int correctAnswerIndex = QuestionService.chooseRandomCorrectAnswerIndex(question)
        sessionAttributes.put(SessionAttributes.CORRECT_ANSWER_INDEX, correctAnswerIndex)
        sessionAttributes.put(SessionAttributes.CORRECT_ANSWER_TEXT, question.correctAnswer)
        String questionString = Messages.buildQuestionMessage(question, correctAnswerIndex)
        sessionAttributes.put(SessionAttributes.LAST_RESPONSE, questionString)
        ResponseBuilder response = AlexaSdkHelper.responseWithSimpleCard(input, questionString, questionString)
        response.build()
    }
}
