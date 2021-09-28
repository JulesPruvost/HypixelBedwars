/*
 * Copyright (c) 2021. Jules Pruvost
 * All rights reserved
 */

package me.gamendecat.hypixelbedwars.games.bedwars.events;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class ArmorStandDamageEvent implements Listener {

    @EventHandler
    public void onDamageArmorStand(EntityDamageEvent e) {
        Entity entity = e.getEntity();
        if(entity instanceof ArmorStand) {
            e.setCancelled(true);
        }
    }
}
