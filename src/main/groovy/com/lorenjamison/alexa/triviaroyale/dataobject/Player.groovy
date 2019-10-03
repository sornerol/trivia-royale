package com.lorenjamison.alexa.triviaroyale.dataobject

import com.lorenjamison.alexa.triviaroyale.dataobject.base.PlayerBase


class Player extends PlayerBase {

    PlayerBase playerUpdater

    Player(PlayerBase playerUpdater) {
        this.playerUpdater = playerUpdater
        this.load()
    }

    Player(String alexaId) {
        this.playerUpdater = new PlayerBase()
        playerUpdater.alexaId = alexaId
        this.load()
    }

    Player(long id) {
        this.playerUpdater = new PlayerBase()
        playerUpdater.id = id
        this.load()
    }

    void load() {

    }

    void save() {

    }
}
