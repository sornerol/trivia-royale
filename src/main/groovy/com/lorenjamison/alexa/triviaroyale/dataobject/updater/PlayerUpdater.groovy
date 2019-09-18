package com.lorenjamison.alexa.triviaroyale.dataobject.updater

class PlayerUpdater {
    String id
    String name
    boolean isHousePlayer
    Date lastModified

    PlayerUpdater(String id) {
        this.id = id
    }
}
