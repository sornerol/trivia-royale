package com.triviaroyale.util

import groovy.transform.CompileStatic

@CompileStatic
class Messages {

    public static final String WELCOME_NEW_PLAYER = 'Welcome to Trivia Royale! Test your brain power against other \
players in this battle royale style trivia game.'

    public static final String WELCOME_EXISTING_PLAYER = 'Welcome back to Trivia Royale!'

    public static final String HELP_MESSAGE =
            "Test your brain power against ${Constants.NUMBER_OF_PLAYERS - 1} other players in a multiple choice \
trivia quiz. The rules are simple: Correct answers allow you to steal health points from players with incorrect \
answers. As the quiz progresses, wrong answers affect your health more. If your health drops to zero, you will be \
eliminated. The player with the highest health score at the end of the game is the winner."

    public static final String ASK_TO_START_NEW_GAME =
            'A new round of Trivia Royale is about to start. Would you like to play?'

    public static final String ASK_TO_RESUME_GAME =
            'You are currently in the middle of a game of Trivia Royale. Would you like to pick up where you left off?'

    public static final String ASK_TO_PLAY_AFTER_HELP = 'Would you like to play Trivia Royale?'

    public static final String CONFIRM_START_OVER = 'Are you sure you want to give up and start a new game?'

    public static final String STARTING_NEW_GAME = 'Okay. Hold on while I find some opponents. ' +
            '<audio src="https://trivia-royale-assets.s3.amazonaws.com/gamestart.mp3" />'

    public static final String CANT_UNDERSTAND = "Sorry, I didn't understand you."

    public static final String HOW_TO_ANSWER = 'Tell me the letter of the answer you choose.'

    public static final String NOT_IN_GAME = 'You are not currently playing a game of Trivia Royale.'

    public static final String EXIT_SKILL =
            '''Thank you for playing Trivia Royale. If you enjoyed playing, would you mind leaving a review in the
Alexa Skills Store?'''

    public static final String SECOND_CHANCE_OFFER = 'You can stay in the game with a Second Chance. A Second Chance ' +
            "will reset your health to $Constants.STARTING_HEALTH, and you can continue playing. Would you like to " +
            'learn more?'

    public static final String SECOND_CHANCE_ACCEPTED = "Your health has been restored. Let's get back to the quiz."

    public static final String HEALTH_BELOW_ZERO = 'Uh-oh, your health has dropped below zero.'

    public static final String CONGRATULATIONS = 'Congratulations on your win!'

    public static final String WHAT_CAN_I_BUY = 'You can buy a Second Chance to avoid elimination in a quiz. ' +
            "A Second Chance resets your health to $Constants.STARTING_HEALTH and allows you to keep playing. " +
            'I will offer you a second chance whenever your health drops below zero.'

    public static final String NO_PRODUCTS_TO_BUY = "I don't have anything that you can buy. Sorry about that."

    public static final String PRODUCT_NOT_FOUND = "Sorry, I couldn't find any products."

}
