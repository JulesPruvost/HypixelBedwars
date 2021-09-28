/*
 * Copyright (c) 2021. Jules Pruvost
 * All rights reserved
 */

package me.gamendecat.hypixelbedwars.games.bedwars.gui.shop;

import me.gamendecat.hypixelbedwars.games.bedwars.gui.GUI;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class IslandUpgradeGUI implements GUI{
    @Override
    public Inventory getInventory() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public GUI handleClick(Player player, ItemStack itemstack, InventoryView view) {
        return null;
    }

    @Override
    public boolean isInventory(InventoryView view) {
        return false;
    }
}
