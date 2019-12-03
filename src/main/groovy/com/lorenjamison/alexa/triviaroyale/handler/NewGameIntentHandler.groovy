package com.lorenjamison.alexa.triviaroyale.handler

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.dispatcher.request.handler.RequestHandler
import com.amazon.ask.model.Response
import com.amazon.ask.model.services.directive.DirectiveServiceClient
import com.amazon.ask.model.services.directive.SendDirectiveRequest
import com.amazon.ask.model.services.directive.SpeakDirective
import com.amazon.ask.response.ResponseBuilder
import com.lorenjamison.alexa.triviaroyale.dataobject.Game
import com.lorenjamison.alexa.triviaroyale.service.GameService
import com.lorenjamison.alexa.triviaroyale.service.QuestionService
import com.lorenjamison.alexa.triviaroyale.util.AlexaSdkHelper
import com.lorenjamison.alexa.triviaroyale.util.Constants
import com.lorenjamison.alexa.triviaroyale.util.GameState
import com.lorenjamison.alexa.triviaroyale.util.Messages
import com.lorenjamison.alexa.triviaroyale.util.SessionAttributes

import static com.amazon.ask.request.Predicates.intentName
import static com.amazon.ask.request.Predicates.sessionAttribute

class NewGameIntentHandler implements RequestHandler {
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

        Game newGame = GameService.startNewGame(playerId)
        LinkedHashMap<Long, Integer> opponents = new LinkedHashMap<Long, Integer>()
        newGame.opponentList.each {
            opponent -> opponents.put(opponent, Constants.STARTING_HEALTH)
        }

        sessionAttributes.put(SessionAttributes.GAME_ID, newGame.id)
        sessionAttributes.put(SessionAttributes.OPPONENTS, opponents)
        sessionAttributes.put(SessionAttributes.CURRENT_HEALTH, Constants.STARTING_HEALTH)
        sessionAttributes.put(SessionAttributes.QUESTION_NUMBER, 1)

        String question = QuestionService.getQuizQuestion(newGame.quizId, (int) sessionAttributes[SessionAttributes.QUESTION_NUMBER])
        sessionAttributes.put(SessionAttributes.LAST_RESPONSE, question)
        ResponseBuilder response = AlexaSdkHelper.responseWithSimpleCard(input, question, question)
        response.build()
    }
}
