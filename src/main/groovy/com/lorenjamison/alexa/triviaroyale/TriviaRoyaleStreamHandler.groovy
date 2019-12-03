package com.lorenjamison.alexa.triviaroyale

import com.amazon.ask.Skill
import com.amazon.ask.Skills
import com.amazon.ask.SkillStreamHandler
import com.amazon.ask.dispatcher.request.handler.impl.ConnectionsRequestHandler
import com.lorenjamison.alexa.triviaroyale.handler.AnswerQuestionIntentHandler
import com.lorenjamison.alexa.triviaroyale.handler.CreatePlayerIntentHandler
import com.lorenjamison.alexa.triviaroyale.handler.HearRulesIntentHandler
import com.lorenjamison.alexa.triviaroyale.handler.LaunchRequestHandler

import com.lorenjamison.alexa.triviaroyale.handler.CancelAndStopIntentHandler
import com.lorenjamison.alexa.triviaroyale.handler.NewGameIntentHandler
import com.lorenjamison.alexa.triviaroyale.handler.RepeatIntentHandler
import com.lorenjamison.alexa.triviaroyale.handler.SessionEndedRequestHandler

class TriviaRoyaleStreamHandler extends SkillStreamHandler {
    private static Skill getSkill() {
        Skills.standard()
                .addRequestHandlers(
                        new AnswerQuestionIntentHandler(),
                        new CancelAndStopIntentHandler(),
                        new CreatePlayerIntentHandler(),
                        new HearRulesIntentHandler(),
                        new LaunchRequestHandler(),
                        new NewGameIntentHandler(),
                        new RepeatIntentHandler(),
                        new SessionEndedRequestHandler())
                .build()
    }

    TriviaRoyaleStreamHandler() {
        super(getSkill())
    }
}