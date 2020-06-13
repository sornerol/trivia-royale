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
import com.triviaroyale.util.*
import groovy.transform.CompileStatic
import groovy.util.logging.Log

@CompileStatic
@Log
class CreatePlayerIntentHandler implements RequestHandler {

    @Override
    boolean canHandle(HandlerInput input) {
        input.matches(intentName('NewPlayerIntent')
                & sessionAttribute(SessionAttributes.APP_STATE, AppState.NEW_PLAYER_SETUP.toString()))
    }

    @Override
    Optional<Response> handle(HandlerInput input) {
        log.level = Constants.LOG_LEVEL
        log.entering(this.class.name, Constants.HANDLE_METHOD)

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
        log.exiting(this.class.name, Constants.HANDLE_METHOD)

        response.build()
    }

}
