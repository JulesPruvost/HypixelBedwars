/*
 * Copyright (c) 2021. Jules Pruvost
 * All rights reserved
 */

package me.gamendecat.hypixelbedwars.games.bedwars.players;

import me.gamendecat.hypixelbedwars.games.bedwars.utility.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public enum AxeUpgradeLevel {

    NONE, WOOD, IRON, GOLD, DIAMOND;

    public String tierString() {
        switch(this) {
            case WOOD:
                return "1";
            case IRON:
                return "2";
            case GOLD:
                return "3";
            case DIAMOND:
                return "4";
        }
        return "How did you do this?";
    }

    public ItemStack toItemStack() {
        switch(this) {
            case NONE:
                return null;
            case WOOD:
                return new ItemBuilder(Material.WOOD_AXE).addEnchant(Enchantment.DIG_SPEED, 1).hideEnchantment().toItemStack();
            case IRON:
                return new ItemBuilder(Material.IRON_AXE).addEnchant(Enchantment.DIG_SPEED, 2).hideEnchantment().toItemStack();
            case GOLD:
                return new ItemBuilder(Material.GOLD_AXE).addEnchant(Enchantment.DIG_SPEED, 3).hideEnchantment().toItemStack();
            case DIAMOND:
                return new ItemBuilder(Material.DIAMOND_AXE).addEnchant(Enchantment.DIG_SPEED, 4).hideEnchantment().toItemStack();
            default:
                return new ItemBuilder(Material.BARRIER).setName("&cError").toItemStack();
        }
    }
}
