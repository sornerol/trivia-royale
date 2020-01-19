package com.triviaroyale

import com.amazon.ask.Skill
import com.amazon.ask.Skills
import com.amazon.ask.SkillStreamHandler
import com.triviaroyale.handler.NewGameIntentHandler
import com.triviaroyale.handler.AnswerQuestionIntentHandler
import com.triviaroyale.handler.CancelAndStopIntentHandler
import com.triviaroyale.handler.CreatePlayerIntentHandler
import com.triviaroyale.handler.HearRulesIntentHandler
import com.triviaroyale.handler.LaunchRequestHandler
import com.triviaroyale.handler.RepeatIntentHandler
import com.triviaroyale.handler.SessionEndedRequestHandler
import groovy.transform.CompileStatic

@CompileStatic
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
        super(skill)
    }

}
