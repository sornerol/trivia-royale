package com.lorenjamison.triviaroyale

import com.amazon.ask.Skill
import com.amazon.ask.Skills
import com.amazon.ask.SkillStreamHandler
import com.lorenjamison.triviaroyale.handler.NewGameIntentHandler
import com.lorenjamison.triviaroyale.handler.AnswerQuestionIntentHandler
import com.lorenjamison.triviaroyale.handler.CancelAndStopIntentHandler
import com.lorenjamison.triviaroyale.handler.CreatePlayerIntentHandler
import com.lorenjamison.triviaroyale.handler.HearRulesIntentHandler
import com.lorenjamison.triviaroyale.handler.LaunchRequestHandler
import com.lorenjamison.triviaroyale.handler.RepeatIntentHandler
import com.lorenjamison.triviaroyale.handler.SessionEndedRequestHandler

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