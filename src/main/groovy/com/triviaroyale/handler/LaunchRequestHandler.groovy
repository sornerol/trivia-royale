package com.triviaroyale.handler

import static com.amazon.ask.request.Predicates.requestType

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.dispatcher.request.handler.RequestHandler
import com.amazon.ask.model.LaunchRequest
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
class LaunchRequestHandler implements RequestHandler {

    @Override
    boolean canHandle(HandlerInput input) {
        input.matches(requestType(LaunchRequest))
    }

    @Override
    Optional<Response> handle(HandlerInput input) {
        println('START LaunchRequestHandler.handle()')
        Map<String, Object> sessionAttributes = input.attributesManager.sessionAttributes
        AmazonDynamoDB dynamoDB = AmazonDynamoDBClientBuilder.defaultClient()
        PlayerService playerService = new PlayerService(dynamoDB)

        Player player = playerService.loadPlayer(AlexaSdkHelper.getUserId(input))
        String responseMessage
        String repromptMessage

        if (player == null) {
            sessionAttributes.put(SessionAttributes.APP_STATE, AppState.NEW_PLAYER_SETUP)
            responseMessage = "$Messages.WELCOME_NEW_PLAYER $Messages.RULES $Messages.ASK_FOR_NAME"
            repromptMessage = Messages.ASK_FOR_NAME
        } else {
            sessionAttributes = PlayerService.updatePlayerSessionAttributes(sessionAttributes, player)
            sessionAttributes.put(SessionAttributes.APP_STATE, AppState.NEW_GAME)

            responseMessage = "$Messages.WELCOME_EXISTING_PLAYER $Messages.ASK_TO_START_NEW_GAME"
            repromptMessage = Messages.ASK_TO_START_NEW_GAME
        }

        sessionAttributes.put(SessionAttributes.LAST_RESPONSE, responseMessage)

        input.attributesManager.sessionAttributes = sessionAttributes
        ResponseBuilder response = AlexaSdkHelper.responseWithSimpleCard(input, responseMessage, repromptMessage)
        response.build()
    }

}
