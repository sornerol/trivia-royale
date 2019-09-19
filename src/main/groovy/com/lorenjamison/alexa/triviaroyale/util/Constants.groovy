package com.lorenjamison.alexa.triviaroyale.util

class Constants {
    static final int NUMBER_OF_PLAYERS = 10
    static final int NUMBER_OF_QUESTIONS = 10
    static final int STARTING_HEALTH = 100

    static final SKILL_TITLE = "Trivia Royale"

    static final NEW_PLAYER_WELCOME_MESSAGE = "Welcome to Trivia Royale! Test your brain power against other players " +
            "in a battle royale style trivia game. Would you like to hear the rules?"

    static final RULES_MESSAGE = "In each game, you will play against ${NUMBER_OF_PLAYERS - 1} other players. Each " +
            "player starts with ${STARTING_HEALTH} health points. I will ask you a series of ${NUMBER_OF_QUESTIONS} " +
            "questions. You will lose health for answering questions incorrectly. You can also gain health by answering " +
            "questions correctly. If your health drops to zero, you will be eliminated. Survive through all of the " +
            "questions to earn leaderboard points. Compete to claim the top place on the leaderboard every week. The " +
            "more you play, the more points you can earn."

    static final EXISTING_PLAYER_WELCOME_MESSAGE = "Welcome back to Trivia Royale!"

    static final CHOOSE_CATEGORY_MESSAGE = "To get started, tell me which category you want to play."

    static final STARTING_NEW_GAME_MESSAGE = "Okay. Hold on while I find some opponents."
}
