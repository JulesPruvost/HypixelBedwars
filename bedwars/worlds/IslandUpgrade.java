/*
 * Copyright (c) 2021. Jules Pruvost
 * All rights reserved
 */

package me.gamendecat.hypixelbedwars.games.bedwars.worlds;

public enum IslandUpgrade {
    SHARP_SWORDS, HASTE, FASTER_FORGE, PROTECTION, TRAPS;

    public String formattedName() {
        switch(this) {
            case SHARP_SWORDS:
                return "&b&lSharpness";
            case HASTE:
                return "&e&lHaste";
            case PROTECTION:
                return "&f&lProtection";
            case TRAPS:
                return "&4&lTraps";
            case FASTER_FORGE:
                return "&9&lThe forge";
        }
        return "Something weird!";
    }


}
