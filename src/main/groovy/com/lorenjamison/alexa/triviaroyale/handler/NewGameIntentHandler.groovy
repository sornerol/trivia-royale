package com.lorenjamison.alexa.triviaroyale.handler

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.dispatcher.request.handler.RequestHandler
import com.amazon.ask.model.Response
import com.amazon.ask.model.services.directive.DirectiveServiceClient
import com.amazon.ask.model.services.directive.SendDirectiveRequest
import com.amazon.ask.model.services.directive.SpeakDirective
import com.amazon.ask.response.ResponseBuilder
import com.lorenjamison.alexa.triviaroyale.data.Question
import com.lorenjamison.alexa.triviaroyale.service.GameStateService
import com.lorenjamison.alexa.triviaroyale.service.QuestionService
import com.lorenjamison.alexa.triviaroyale.util.AlexaSdkHelper
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

        /*
        If we're having to create a brand new quiz (because there aren't any unplayed quizzes for the player)
        we'll need a bit of a delay to build the new quiz. We'll play an audio clip here as we're setting up the game
        to let the player know we're working.
         */
        DirectiveServiceClient directiveServiceClient = input.serviceClientFactory.directiveService
        SpeakDirective speakDirective = SpeakDirective.builder().withSpeech(Messages.STARTING_NEW_GAME).build()
        //TODO: add audio clip
        SendDirectiveRequest sendDirectiveRequest = SendDirectiveRequest.builder().withDirective(speakDirective).build()
        directiveServiceClient.enqueue(sendDirectiveRequest)

        long playerId = sessionAttributes[SessionAttributes.PLAYER_ID] as long
        com.lorenjamison.alexa.triviaroyale.data.GameState newGame = GameStateService.startNewGame(playerId)
        sessionAttributes = GameStateService.updateSessionAttributesWithGameState(sessionAttributes, newGame)
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
