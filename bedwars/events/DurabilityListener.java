/*
 * Copyright (c) 2021. Jules Pruvost
 * All rights reserved
 */

package me.gamendecat.hypixelbedwars.games.bedwars.events;

import me.gamendecat.hypixelbedwars.games.bedwars.manager.GameManager;
import me.gamendecat.hypixelbedwars.games.bedwars.manager.GameState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;

public class DurabilityListener implements Listener {

    private GameManager gameManager;

    public DurabilityListener(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @EventHandler
    public void onDurabilityLose(PlayerItemDamageEvent e) {
        if(gameManager.getState() == null) return;
        if(gameManager.getState() != GameState.ACTIVE) return;
        e.setCancelled(true);
    }
}
