package com.triviaroyale

import com.amazon.ask.Skill
import com.amazon.ask.SkillStreamHandler
import com.amazon.ask.Skills
import com.triviaroyale.interceptor.LogRequestEnvelopeRequestInterceptor
import com.triviaroyale.interceptor.InitializeSessionRequestInterceptor
import com.triviaroyale.requestrouter.*
import groovy.transform.CompileStatic

@CompileStatic
class TriviaRoyaleStreamHandler extends SkillStreamHandler {

    private static Skill getSkill() {
        String loggingPropertiesPath = TriviaRoyaleStreamHandler.classLoader.getResource('logging.properties').file
        System.setProperty('java.util.logging.config.file', loggingPropertiesPath)

        Skills.standard()
                .addRequestHandlers(
                        new AnswerIntentRequestRouter(),
                        new CancelAndStopIntentRequestRouter(),
                        new GetStatusIntentRequestRouter(),
                        new HelpIntentRequestRouter(),
                        new LaunchRequestRouter(),
                        new NoIntentRequestRouter(),
                        new RepeatIntentRequestRouter(),
                        new SessionEndedRequestRouter(),
                        new StartOverIntentRequestRouter(),
                        new YesIntentRequestRouter(),
                        new BuyResponseRequestRouter(),
                        new WhatCanIBuyIntentRequestRouter(),
                        new RefundSecondChanceIntentRequestRouter(),
                        new BuySecondChanceIntentRequestRouter(),
                        new SecondChanceInventoryIntentRequestRouter(),
                        new FallbackIntentRequestRouter())
                .addRequestInterceptors(
                        new LogRequestEnvelopeRequestInterceptor(),
                        new InitializeSessionRequestInterceptor())
                .build()
    }

    TriviaRoyaleStreamHandler() {
        super(skill)
    }

}
