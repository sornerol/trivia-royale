package com.triviaroyale.handler

import static com.amazon.ask.request.Predicates.intentName
import static com.amazon.ask.request.Predicates.sessionAttribute

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.dispatcher.request.handler.RequestHandler
import com.amazon.ask.model.Response
import com.amazon.ask.response.ResponseBuilder
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.triviaroyale.data.Player
import com.triviaroyale.service.PlayerService
import com.triviaroyale.util.AlexaSdkHelper
import com.triviaroyale.util.AppState
import com.triviaroyale.util.Messages
import com.triviaroyale.util.SessionAttributes
import groovy.transform.CompileStatic

@CompileStatic
class CreatePlayerIntentHandler implements RequestHandler {

    @Override
    boolean canHandle(HandlerInput input) {
        input.matches(intentName('NewPlayerIntent')
                & sessionAttribute(SessionAttributes.APP_STATE, AppState.NEW_PLAYER_SETUP))
    }

    @Override
    Optional<Response> handle(HandlerInput input) {
        Map<String, Object> sessionAttributes = input.attributesManager.sessionAttributes
        Player player = new Player()

        player.with {
            name = AlexaSdkHelper.getSlotValue(input, AlexaSdkHelper.NAME_SLOT_KEY)
            alexaId = AlexaSdkHelper.getUserId(input)
            quizCompletion = [:]
        }

        AmazonDynamoDB dynamoDB = AmazonDynamoDBClientBuilder.defaultClient()

        PlayerService playerService = new PlayerService(dynamoDB)
        playerService.savePlayer(player)

        sessionAttributes = PlayerService.updatePlayerSessionAttributes(sessionAttributes, player)
        sessionAttributes.put(SessionAttributes.APP_STATE, AppState.NEW_GAME)

        String responseMessage = 'Thank you. ' + Messages.ASK_TO_START_NEW_GAME
        String repromptMessage = Messages.ASK_TO_START_NEW_GAME

        sessionAttributes.put(SessionAttributes.LAST_RESPONSE, responseMessage)

        input.attributesManager.sessionAttributes = sessionAttributes
        ResponseBuilder response = AlexaSdkHelper.responseWithSimpleCard(input, responseMessage, repromptMessage)
        response.build()
    }

}
