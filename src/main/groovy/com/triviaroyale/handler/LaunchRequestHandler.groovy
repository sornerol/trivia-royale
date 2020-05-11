package com.triviaroyale.handler

import static com.amazon.ask.request.Predicates.requestType

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.dispatcher.request.handler.RequestHandler
import com.amazon.ask.model.LaunchRequest
import com.amazon.ask.model.Response
import com.amazon.ask.response.ResponseBuilder
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.triviaroyale.data.Player
import com.triviaroyale.service.PlayerService
import com.triviaroyale.util.*
import groovy.transform.CompileStatic

@CompileStatic
class LaunchRequestHandler implements RequestHandler {

    @Override
    boolean canHandle(HandlerInput input) {
        input.matches(requestType(LaunchRequest))
    }

    @Override
    Optional<Response> handle(HandlerInput input) {
        Map<String, Object> sessionAttributes = input.attributesManager.sessionAttributes
        AmazonDynamoDB dynamoDB = AmazonAWSResourceHelper.openDynamoDBClient()
        PlayerService playerService = new PlayerService(dynamoDB)

        Player player = playerService.loadPlayer(AlexaSdkHelper.getUserId(input))
        String responseMessage
        String repromptMessage

        if (player == null) {
            sessionAttributes.put(SessionAttributes.GAME_STATE, AppState.NEW_PLAYER_SETUP)
            responseMessage = Messages.WELCOME_NEW_PLAYER + Messages.RULES + Messages.ASK_FOR_NAME
            repromptMessage = Messages.ASK_FOR_NAME
        } else {
            sessionAttributes.put(SessionAttributes.GAME_STATE, AppState.NEW_GAME)

            responseMessage = "${Messages.WELCOME_EXISTING_PLAYER} " +
                    "${Messages.ASK_TO_START_NEW_GAME}"
            repromptMessage = Messages.ASK_TO_START_NEW_GAME
        }

        sessionAttributes.put(SessionAttributes.LAST_RESPONSE, responseMessage)
        ResponseBuilder response = AlexaSdkHelper.responseWithSimpleCard(input, responseMessage, repromptMessage)
        response.build()
    }

}
