package com.lorenjamison.alexa.triviaroyale

import com.amazon.ask.Skill
import com.amazon.ask.Skills
import com.amazon.ask.SkillStreamHandler

import com.lorenjamison.alexa.triviaroyale.handlers.LaunchRequestHandler
import com.lorenjamison.alexa.triviaroyale.handlers.HelpIntentHandler
import com.lorenjamison.alexa.triviaroyale.handlers.CancelAndStopIntentHandler
import com.lorenjamison.alexa.triviaroyale.handlers.SessionEndedRequestHandler

class TriviaRoyaleStreamHandler extends SkillStreamHandler{
    private static Skill getSkill() {
        Skills.standard()
                .addRequestHandlers(
                        new LaunchRequestHandler(),
                        new HelpIntentHandler(),
                        new CancelAndStopIntentHandler(),
                        new SessionEndedRequestHandler())
                .build()
    }

    TriviaRoyaleStreamHandler() {
        super(getSkill())
    }
}