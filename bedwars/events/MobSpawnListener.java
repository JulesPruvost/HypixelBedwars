/*
 * Copyright (c) 2021. Jules Pruvost
 * All rights reserved
 */

package me.gamendecat.hypixelbedwars.games.bedwars.events;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.ItemSpawnEvent;

public class MobSpawnListener implements Listener {

    @EventHandler
    public void onSpawn(CreatureSpawnEvent e) {
            if(e.getEntity().getWorld().getName().equalsIgnoreCase("HypixelBedwarsLobby")) return;
            if(e.getEntity() instanceof ArmorStand) return;
            if(e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL) e.setCancelled(true);
    }

    @EventHandler
    public void onEntity(ItemSpawnEvent e) {
        if(e.getEntity().getName().contains("BED")) {
            e.setCancelled(true);
        }
    }
}
