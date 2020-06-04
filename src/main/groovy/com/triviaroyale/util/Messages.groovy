package com.triviaroyale.util

import groovy.transform.CompileStatic

@CompileStatic
class Messages {

    public static final String WELCOME_NEW_PLAYER = 'Welcome to Trivia Royale! '

    public static final String WELCOME_EXISTING_PLAYER = 'Welcome back to Trivia Royale! '

    public static final String RULES =
            """Test your brain power against other players in a battle royale style trivia game. In each game, you will
play against ${Constants.NUMBER_OF_PLAYERS - 1} other players. Each player starts with $Constants.STARTING_HEALTH
health points. I will ask you a series of $Constants.NUMBER_OF_QUESTIONS questions. You will lose health for answering
questions incorrectly. You can also gain health by answering questions correctly. If your health drops to zero, you
will be eliminated. Survive through all of the questions to earn leaderboard points. Compete to claim the top place on
the leaderboard every week. The more you play, the more points you can earn."""

    public static final String ASK_FOR_NAME = 'To get started, tell me your name.'

    public static final String ASK_TO_START_NEW_GAME =
            'A new round of Trivia Royale is about to start. Would you like to play?'

    public static final String STARTING_NEW_GAME = 'Okay. Hold on while I find some opponents.'

    public static final String EXIT_SKILL =
            '''Thank you for playing Trivia Royale. If you enjoyed playing, would you mind leaving a review in the
Alexa Skills Store?'''

}
