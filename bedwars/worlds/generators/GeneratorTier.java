/*
 * Copyright (c) 2021. Jules Pruvost
 * All rights reserved
 */

package me.gamendecat.hypixelbedwars.games.bedwars.worlds.generators;

public enum GeneratorTier {
    ONE, TWO, THREE;

    public String toString() {
        switch (this) {
            case ONE:
                return "1";
            case TWO:
                return "2";
            case THREE:
                return "3";
            default:
                return "Error";
        }
    }
}
