/*
 * Copyright (c) 2021. Jules Pruvost
 * All rights reserved
 */

package me.gamendecat.hypixelbedwars.games.bedwars.gui.shop;

import me.gamendecat.hypixelbedwars.games.bedwars.players.PlayerManager;
import me.gamendecat.hypixelbedwars.games.bedwars.utility.ItemBuilder;
import me.gamendecat.hypixelbedwars.games.bedwars.worlds.Island;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.TrapDoor;
import org.bukkit.material.Wool;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum ShopCategory {

    QUICK_BUY, BLOCKS, WEAPONS, ARMOR, TOOLS, RANGED, POTIONS, UTILITY, TRAPS;

    public Material material() {
        switch(this) {
            case QUICK_BUY: return Material.NETHER_STAR;
            case BLOCKS: return new Wool(DyeColor.WHITE).getItemType();
            case WEAPONS: return Material.DIAMOND_SWORD;
            case ARMOR: return Material.DIAMOND_BOOTS;
            case TOOLS: return Material.DIAMOND_PICKAXE;
            case RANGED: return Material.BOW;
            case POTIONS: return Material.BREWING_STAND;
            case UTILITY: return Material.TNT;
            case TRAPS: return new TrapDoor(Material.DARK_OAK_DOOR).getItemType();
        }
        return Material.BED;
    }

    public List<ShopItem> getItems(PlayerManager playerManager, Player player, Island island) {
        switch(this) {
            case QUICK_BUY: return ShopCategory.quickBuyItems(playerManager, player, island);
            case UTILITY: return ShopCategory.utilitiesItems();
            default: return new ArrayList<>();
        }
    }

    public static List<ShopItem> quickBuyItems(PlayerManager playerManager, Player player, Island island) {
        ShopItem pickaxeItem = playerManager.getPickaxeShopItemForPlayer(player);
        ShopItem axeItem = playerManager.getAxeShopItemForPlayer(player);
        return new ArrayList<>(
                Arrays.asList(new ShopItem(
                        "Wool",
                        island.getColor().woolMaterial().toItemStack(),
                        16,
                        4,
                        Material.IRON_INGOT,
                        new String[]{"Quickly bridge across islands!"}
                ), new ShopItem("Stone Sword",
                        new ItemStack(Material.STONE_SWORD),
                        10,
                        Material.IRON_INGOT,
                        new String[]{"A great starter weapon."}
                ), new ShopItem("Permanent Chainmail Armor",
                        new ItemStack(Material.CHAINMAIL_BOOTS),
                        40,
                        Material.IRON_INGOT,
                        new String[]{"Chainmail legging and boots.", "&a&lThese stay with you when you respawn."}
                ), new ShopItem("Permanent Shears",
                        new ItemStack(Material.SHEARS),
                        20,
                        Material.IRON_INGOT,
                        new String[]{"Quickly clean up wool.", "&a&lThis stays with you when you respawn"}
                ), new ShopItem("Bow",
                        new ItemStack(Material.BOW),
                        12,
                        Material.GOLD_INGOT,
                        new String[]{"Shoot down your enemies from afar!"}
                ),new ShopItem("Invisibility Potion (30 seconds)",
                        new ItemBuilder(Material.POTION).addPotionEffect(PotionType.INVISIBILITY).toItemStack(),
                        2,
                        Material.EMERALD,
                        new String[]{"Go invisible for 30 seconds and", "suprise your enemies"}
                ), new ShopItem(
                        "TNT", new ItemStack(Material.TNT),
                        4,
                        Material.GOLD_INGOT,
                        new String[]{"Ignites right as you place it!", "Great for breaking bed defenses"}
                ), new ShopItem("Wood",
                        new ItemStack(Material.WOOD),
                        4,
                        Material.GOLD_INGOT,
                        new String[]{"A nice mid-tier defense block."}
                ),new ShopItem("Iron Sword",
                        new ItemStack(Material.IRON_SWORD),
                        7,
                        Material.GOLD_INGOT,
                        new String[]{"A swordman's weapon, perfect for", "strong defense and attack alike."}
                ), new ShopItem("Permanent Iron Armor",
                        new ItemStack(Material.IRON_BOOTS),
                        12,
                        Material.GOLD_INGOT,
                        new String[]{"Iron leggings and boots", "&a&lThese stay with you when you respawn."}
                ), new ShopItem("Bow (Power I)",
                        new ItemBuilder(Material.BOW).addEnchant(Enchantment.ARROW_DAMAGE, 1).hideEnchantment().toItemStack(),
                        24,
                        Material.GOLD_INGOT,
                        new String[]{"An upgraded version of the default", "Bow, with some additional power."}
                ), new ShopItem("Jump V Potion (45 Seconds)",
                        new ItemBuilder(Material.POTION).addPotionEffect(PotionType.JUMP).toItemStack(),
                        1,
                        Material.EMERALD, new String[]{"Become a rabbit and jump over walls"}
                ), new ShopItem("Ender pearl",
                        new ItemStack(Material.ENDER_PEARL),
                        4,
                        Material.EMERALD,
                        new String[]{"Perhaps the quickest way to invade", "other islands without warning"}
                ), new ShopItem("Endstone",
                        new ItemStack(Material.ENDER_STONE),
                        16,
                        24,
                        Material.IRON_INGOT,
                        new String[]{"A strong high tier defensive block."}
                ), new ShopItem("Permanent Diamond Armor",
                        new ItemStack(Material.DIAMOND_BOOTS),
                        6,
                        Material.EMERALD,
                        new String[]{"Diamond leggings and boots", "&a&lThese stay with you when you respawn"}
                ),new ShopItem("Diamond Sword",
                        new ItemStack(Material.DIAMOND_SWORD),
                        4,
                        Material.EMERALD,
                        new String[]{"A high tier offensive weapon"}
                ), new ShopItem("Arrows",
                        new ItemStack(Material.ARROW),
                        8,
                        2,
                        Material.GOLD_INGOT,
                        new String[]{"Arrows for ranged weapons"}
                ), new ShopItem("Speed Potion (45 seconds)",
                        new ItemBuilder(Material.POTION).addPotionEffect(PotionType.SPEED).toItemStack(),
                        1,
                        Material.EMERALD,
                        new String[]{"**harder, faster, stronger**", "Get speed for 45 seconds"}
                ), new ShopItem("Water Bucket",
                new ItemStack(Material.WATER_BUCKET),
                3, Material.GOLD_INGOT,
                new String[]{"Protect your bed from explosions!"}
                )
            )
        );
    }

    public static List<ShopItem> utilitiesItems() {
        return new ArrayList<>(
                Arrays.asList(new ShopItem("Golden Apple",
                        new ItemStack(Material.GOLDEN_APPLE),
                        4, Material.GOLD_INGOT,
                        new String[]{"Quickly regenerate your health!"}
                ))
        );
    }
}
