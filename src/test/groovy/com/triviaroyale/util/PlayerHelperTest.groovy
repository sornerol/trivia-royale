package com.triviaroyale.util

import com.triviaroyale.data.Player
import org.junit.Ignore
import spock.lang.Specification

@Ignore
class PlayerHelperTest extends Specification {

    def 'ConsumeSecondChance'() {
        setup:
        Player player = new Player()
        player.with {
            secondChancesPurchased = 3
            secondChancesConsumed = 2
        }

        when:
        player = PlayerHelper.consumeSecondChance(player)

        then:
        player.secondChancesConsumed == 3
    }

}
