package com.triviaroyale.util

import com.triviaroyale.data.GameState
import com.triviaroyale.data.util.GameStateStatus
import spock.lang.Specification

class GameStateHelperTest extends Specification {

    public static final String PLAYER_ID = 'GameStateHelperUseSecondChance'
    def 'UseSecondChance'() {
        setup:
        GameState gameState = new GameState()
        gameState.with {
            playerId = PLAYER_ID
            status = GameStateStatus.GAME_OVER
            secondChanceUsed = false
            playersHealth = [:]
        }

        when:
        gameState = GameStateHelper.useSecondChance(gameState)

        then:
        gameState.status == GameStateStatus.ACTIVE
        gameState.secondChanceUsed
        gameState.playersHealth[PLAYER_ID] == Constants.STARTING_HEALTH
    }

}
