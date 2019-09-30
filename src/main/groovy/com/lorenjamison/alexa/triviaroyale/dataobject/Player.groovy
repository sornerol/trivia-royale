package com.lorenjamison.alexa.triviaroyale.dataobject

import com.lorenjamison.alexa.triviaroyale.dataobject.updater.PlayerUpdater


class Player {
    long id
    String alexaId
    String name
    boolean isHousePlayer
    Date lastModified
    PlayerUpdater playerUpdater

    Player(String alexaId) {
        this.playerUpdater = new PlayerUpdater(alexaId)
        this.load()
    }

    void load() {

    }

    void save() {

    }
}
