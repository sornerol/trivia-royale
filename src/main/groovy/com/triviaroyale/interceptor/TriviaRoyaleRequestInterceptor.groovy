package com.triviaroyale.interceptor

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.dispatcher.request.interceptor.RequestInterceptor
import groovy.transform.CompileStatic
import groovy.util.logging.Log

@CompileStatic
@Log
class TriviaRoyaleRequestInterceptor implements RequestInterceptor {

    @Override
    void process(HandlerInput input) {
        log.info(input.requestEnvelopeJson.toString())
    }

}
