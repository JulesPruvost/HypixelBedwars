/*
 * Copyright (c) 2021. Jules Pruvost
 * All rights reserved
 */

package me.gamendecat.hypixelbedwars.games.bedwars.worlds;

import me.gamendecat.hypixelbedwars.games.bedwars.manager.GameManager;
import me.gamendecat.hypixelbedwars.games.bedwars.manager.GameState;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.HashSet;
import java.util.Set;

public class BlockManager {

    private final Set<Block> placedByPlayer = new HashSet<>();

    public void setPlaced(Block block) {
        placedByPlayer.add(block);
    }

    public boolean isPlacedbyPlayer(Block block) {
        return placedByPlayer.contains(block);
    }

    public void reset() {
        placedByPlayer.clear();
    }
}
