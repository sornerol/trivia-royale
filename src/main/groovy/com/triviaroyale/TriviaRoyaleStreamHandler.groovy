package com.triviaroyale

import com.amazon.ask.Skill
import com.amazon.ask.SkillStreamHandler
import com.amazon.ask.Skills
import com.triviaroyale.handler.*
import groovy.transform.CompileStatic

@CompileStatic
class TriviaRoyaleStreamHandler extends SkillStreamHandler {

    private static Skill getSkill() {
        String loggingPropertiesPath = TriviaRoyaleStreamHandler.classLoader.getResource('logging.properties').file
        System.setProperty('java.util.logging.config.file', loggingPropertiesPath)

        Skills.standard()
                .addRequestHandlers(
                        new AnswerQuestionIntentHandler(),
                        new CancelAndStopIntentHandler(),
                        new CreatePlayerIntentHandler(),
                        new HearRulesIntentHandler(),
                        new LaunchRequestHandler(),
                        new NewGameIntentHandler(),
                        new RepeatIntentHandler(),
                        new SessionEndedRequestHandler(),
                        new StartOverIntentHandler(),
                        new ResumeGameIntentHandler())
                .build()
    }

    TriviaRoyaleStreamHandler() {
        super(skill)
    }

}
