package com.lorenjamison.alexa.triviaroyale.util

import com.lorenjamison.alexa.triviaroyale.data.Category
import com.lorenjamison.alexa.triviaroyale.data.Player
import com.lorenjamison.alexa.triviaroyale.data.Question
import com.lorenjamison.alexa.triviaroyale.service.CategoryService

class Messages {
    static final String WELCOME_NEW_PLAYER = "Welcome to Trivia Royale! "

    static final String WELCOME_EXISTING_PLAYER = "Welcome back to Trivia Royale! "

    static final String RULES = "Test your brain power against other players in a battle royale style trivia game. " +
            "In each game, you will play against ${Constants.NUMBER_OF_PLAYERS - 1} other players. Each player starts " +
            "with ${Constants.STARTING_HEALTH} health points. I will ask you a series of " +
            "${Constants.NUMBER_OF_QUESTIONS} questions. You will lose health for answering questions incorrectly. " +
            "You can also gain health by answering questions correctly. If your health drops to zero, you will be " +
            "eliminated. Survive through all of the questions to earn leaderboard points. Compete to claim the top " +
            "place on the leaderboard every week. The more you play, the more points you can earn. "

    static final String ASK_FOR_NAME = "To get started, tell me your name."

    static final String ASK_TO_START_NEW_GAME = "A new round of Trivia Royale is about to start. Would you like to play?"

    static final String INVALID_CATEGORY_SELECTION = "Sorry, that's not a valid category. "

    static final String STARTING_NEW_GAME = "Okay. Hold on while I find some opponents."

    static String getAvailableCategoryListMessage(long playerId) {
        List<Category> availableCategories = CategoryService.getCategoriesAvailableForPlayer(playerId)
        String categoryListMessage = "You can choose from these categories. "
        for (int i = 0; i < availableCategories.size(); i++) {
            if (i + 1 >= availableCategories.size()) {
                categoryListMessage += "or "
            }
            categoryListMessage += "${availableCategories[i].name}. "
        }
        categoryListMessage
    }

    static String buildQuestionMessage(Question question, int correctAnswerIndex) {

    }
}
