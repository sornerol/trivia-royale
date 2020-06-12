package com.triviaroyale.util

import groovy.transform.CompileStatic

@CompileStatic
class Messages {

    public static final String WELCOME_NEW_PLAYER = 'Welcome to Trivia Royale!'

    public static final String WELCOME_EXISTING_PLAYER = 'Welcome back to Trivia Royale!'

    public static final String RULES =
            "Test your brain power against other players in a battle royale style trivia game. The rules are simple: \
Compete against ${Constants.NUMBER_OF_PLAYERS - 1} other players in a multiple choice trivia quiz. Correct answers \
will gain you health points, while wrong answers will make you lose health. If your health drops to zero, you will \
be eliminated. Can you survive?"

    public static final String ASK_FOR_NAME = 'To get started, tell me your name.'

    public static final String ASK_TO_START_NEW_GAME =
            'A new round of Trivia Royale is about to start. Would you like to play?'

    public static final String STARTING_NEW_GAME = 'Okay. Hold on while I find some opponents.'

    public static final String EXIT_SKILL =
            '''Thank you for playing Trivia Royale. If you enjoyed playing, would you mind leaving a review in the
Alexa Skills Store?'''

}
