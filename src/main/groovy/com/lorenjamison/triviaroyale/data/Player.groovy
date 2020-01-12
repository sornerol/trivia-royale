package com.lorenjamison.triviaroyale.data

import com.lorenjamison.triviaroyale.data.base.PlayerBase

class Player extends PlayerBase implements DataObject {

    PlayerBase playerUpdater

    Player(String alexaId) {
        this.playerUpdater = new PlayerBase()
        playerUpdater.alexaId = alexaId
        this.load()
    }

    static Player createNewPlayer(PlayerBase newPlayerBase) {
        Player newPlayer = new Player()
        newPlayer.playerUpdater = newPlayerBase
        newPlayer.save()

        newPlayer
    }

    @Override
    void load() {

    }

    @Override
    void save() {

    }
}
