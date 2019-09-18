package com.lorenjamison.alexa.triviaroyale.dataobject

import com.lorenjamison.alexa.triviaroyale.dataobject.updater.PlayerUpdater


class Player {
    String id
    String name
    boolean isHousePlayer
    Date lastModified
    PlayerUpdater playerUpdater

    Player(String id) {
        this.playerUpdater = new PlayerUpdater(id)
        this.load()
    }

    void load() {

    }

    void save() {

    }
}
