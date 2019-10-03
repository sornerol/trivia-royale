package com.lorenjamison.alexa.triviaroyale.util

import com.lorenjamison.alexa.triviaroyale.dataobject.Category
import com.lorenjamison.alexa.triviaroyale.dataobject.Player
import com.lorenjamison.alexa.triviaroyale.service.CategoryService

class Messages {
    static final NEW_PLAYER_WELCOME_MESSAGE = "Welcome to Trivia Royale! Test your brain power against other players " +
            "in a battle royale style trivia game. Would you like to hear the rules?"

    static final RULES_MESSAGE = "In each game, you will play against ${Constants.NUMBER_OF_PLAYERS - 1} other players. " +
            "Each player starts with ${Constants.STARTING_HEALTH} health points. I will ask you a series of " +
            "${Constants.NUMBER_OF_QUESTIONS} questions. You will lose health for answering questions incorrectly. " +
            "You can also gain health by answering questions correctly. If your health drops to zero, you will be " +
            "eliminated. Survive through all of the questions to earn leaderboard points. Compete to claim the top " +
            "place on the leaderboard every week. The more you play, the more points you can earn. "

    static  final String REQUEST_NAME_MESSAGE = "To get started, tell me your name."

    static final EXISTING_PLAYER_WELCOME_MESSAGE = "Welcome back to Trivia Royale! "

    static final CHOOSE_CATEGORY_MESSAGE = "A new round of Trivia Royale is about to start. Which category do you want to play? "

    static final STARTING_NEW_GAME_MESSAGE = "Okay. Hold on while I find some opponents."

    static String getAvailableCategoryListMessage(Player player) {
        List<Category> availableCategories = CategoryService.getCategoriesAvailableForPlayer(player)
        String categoryListMessage = "You can choose from these categories. "
        for (int i = 0; i < availableCategories.size(); i++) {
            if (i + 1 >= availableCategories.size()) {
                categoryListMessage += "or "
            }
            categoryListMessage += "${availableCategories[i].name}. "
        }
        categoryListMessage
    }
}
