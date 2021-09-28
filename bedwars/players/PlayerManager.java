/*
 * Copyright (c) 2021-2021. Jules Pruvost
 * All rights reserved
 */

package me.gamendecat.hypixelbedwars.games.bedwars.players;

import me.gamendecat.hypixelbedwars.games.bedwars.gui.shop.ShopItem;
import me.gamendecat.hypixelbedwars.games.bedwars.manager.GameManager;
import me.gamendecat.hypixelbedwars.games.bedwars.utility.ItemBuilder;
import me.gamendecat.hypixelbedwars.games.bedwars.worlds.Island;
import me.gamendecat.hypixelbedwars.games.bedwars.worlds.IslandUpgrade;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.material.Wool;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class PlayerManager {

    private GameManager gameManager;
    private Map<UUID, PickaxeUpgradeLevel> playerToPickaxeLevel = new HashMap<>();
    private Map<UUID, AxeUpgradeLevel> playerToAxeLevel = new HashMap<>();
    private List<UUID> playersWithShears = new ArrayList<>();
    private Map<UUID, ArmorUpgradeLevel> playerArmorMap = new HashMap<>();


    public PlayerManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public void setSpectatorMode(Player player, boolean b) {
        player.teleport(gameManager.getGameWorld().getLobbyPosition());
        player.setGameMode(GameMode.SPECTATOR);
        player.getInventory().clear();

        if(b) {
            gameManager.endGameIfNeeded();
        }
    }

    public void setPlaying(Player player) {
        player.getInventory().clear();

        Island island = gameManager.getGameWorld().islandForPlayer(player);
        if(island == null || !island.isBedPlaced()) {
            setSpectatorMode(player, false);
            return;
        }
        player.setGameMode(GameMode.SURVIVAL);
        player.teleport(island.getSpawnLocation());
        player.getEnderChest().clear();


        giveTeamArmor(island, player);


    }

    public void clearPlayer(Player player) {
        player.getInventory().clear();
        player.setGameMode(GameMode.SURVIVAL);
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
        player.setLevel(0);
        player.setExp(0);
    }

    public void giveTeamArmor(Island island, Player player) {
        int level = island.getLevelForUpgrade(IslandUpgrade.PROTECTION);
        ItemStack itemStack = new ItemStack(Material.LEATHER_HELMET);
        itemStack.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, level);
        LeatherArmorMeta itemMeta = (LeatherArmorMeta) itemStack.getItemMeta();
        itemMeta.spigot().setUnbreakable(true);
        itemMeta.setColor(island.getColor().getColor());
        itemStack.setItemMeta(itemMeta);

        ItemStack itemStack1 = new ItemStack(Material.LEATHER_CHESTPLATE);
        itemStack1.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, level);
        LeatherArmorMeta itemMeta1 = (LeatherArmorMeta) itemStack1.getItemMeta();
        itemMeta1.spigot().setUnbreakable(true);
        itemMeta1.setColor(island.getColor().getColor());
        itemStack1.setItemMeta(itemMeta1);

        ItemStack itemStack2 = new ItemStack(Material.LEATHER_LEGGINGS);
        itemStack2.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, level);
        LeatherArmorMeta itemMeta2 = (LeatherArmorMeta) itemStack2.getItemMeta();
        itemMeta2.spigot().setUnbreakable(true);
        itemStack2.setItemMeta(itemMeta2);

        ItemStack itemStack3 = new ItemStack(Material.LEATHER_BOOTS);
        itemStack3.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, level);
        itemStack3.addUnsafeEnchantment(Enchantment.OXYGEN, 1);
        LeatherArmorMeta itemMeta3 = (LeatherArmorMeta) itemStack3.getItemMeta();
        itemMeta3.spigot().setUnbreakable(true);
        itemMeta3.setColor(island.getColor().getColor());
        itemStack1.setItemMeta(itemMeta3);

        player.getInventory().setHelmet(itemStack);
        player.getInventory().setChestplate(itemStack1);
        player.getInventory().setLeggings(itemStack2);
        player.getInventory().setBoots(itemStack3);
    }

    public void giveAllTeamSelector() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            giveTeamSelector(player);
        }
    }

    public void giveTeamSelector(Player player) {
        player.getInventory().clear();
        Island island = gameManager.getGameWorld().islandForPlayer(player);
        Material material = Material.BARRIER;
        if(island != null) {
            material = island.getColor().woolMaterial().toItemStack().getType();
            player.getInventory().addItem(new ItemBuilder(new ItemStack(material))
                    .setName("&" + island.getColor().getChatColor().getChar() + island.getColor().formattedName() + "&7(Selected)")
                    .toItemStack());
        }else {
            player.getInventory().addItem(new ItemBuilder(new ItemStack(material, 1))
                    .setName("&cNo Color Selected!")
                    .toItemStack());
        }

        player.getInventory().addItem(new ItemBuilder(new Wool(DyeColor.WHITE).toItemStack(1))
                .setName("&aSelect Team &7(Right click)")
                .toItemStack());
    }

    public void givePlayerPermanentItems(Player player) {
        AtomicBoolean hasSword = new AtomicBoolean(false);

        player.getInventory().forEach(itemStack -> {
            if(itemStack == null) return;

            Material type = itemStack.getType();

            if(type == Material.WOOD_AXE ||
            type == Material.STONE_AXE ||
            type == Material.GOLD_AXE ||
            type == Material.IRON_AXE ||
            type == Material.DIAMOND_AXE) {
               player.getInventory().remove(itemStack);
            }

            if(type == Material.WOOD_PICKAXE ||
                    type == Material.STONE_PICKAXE ||
                    type == Material.GOLD_PICKAXE ||
                    type == Material.IRON_PICKAXE ||
                    type == Material.DIAMOND_PICKAXE) {
                player.getInventory().remove(itemStack);
            }

            if(type == Material.SHEARS) {
                player.getInventory().remove(itemStack);
            }

            if(type.name().toLowerCase().contains("sword")) {
                hasSword.set(true);
            }
        });

        if(!hasSword.get()) {
            ItemStack sword = new ItemStack(Material.WOOD_SWORD);
            Island playerIsland = gameManager.getGameWorld().islandForPlayer(player);
            if(playerIsland != null && playerIsland.getLevelForUpgrade(IslandUpgrade.SHARP_SWORDS) == 1) {
                sword.addEnchantment(Enchantment.DAMAGE_ALL, 1);
            }
            player.getInventory().addItem(sword);
        }

        AxeUpgradeLevel axeUpgradeLevel = getAxeLevel(player);
        ItemStack axeItem = axeUpgradeLevel.toItemStack();
        if(axeItem != null) {
            player.getInventory().addItem(axeItem);
        }

        PickaxeUpgradeLevel pickaxeUpgradeLevel = getPickaxeLevel(player);
        ItemStack pickaxeItem = pickaxeUpgradeLevel.toItemStack();
        if(pickaxeItem != null) {
            player.getInventory().addItem(pickaxeItem);
        }

        if(playersWithShears.contains(player.getUniqueId())) {
            player.getInventory().addItem(new ItemStack(Material.SHEARS));
        }
    }

    public void downgradeTools(Player player) {
        AxeUpgradeLevel axeUpgradeLevel = getAxeLevel(player);
        if(axeUpgradeLevel == AxeUpgradeLevel.DIAMOND) {
            //todo: downgrade tools;
        }
    }

    private void setAxeLevel(Player player, AxeUpgradeLevel level) {
        playerToAxeLevel.put(player.getUniqueId(), level);

        givePlayerPermanentItems(player);
    }

    private void setPickaxeLevel(Player player, PickaxeUpgradeLevel level) {
        playerToPickaxeLevel.put(player.getUniqueId(), level);

        givePlayerPermanentItems(player);
    }

    private void setArmorLevel(Player player, ArmorUpgradeLevel level) {
        playerArmorMap.put(player.getUniqueId(), level);

        giveTeamArmor(gameManager.getGameWorld().islandForPlayer(player), player);
    }

    public void setHasPermanentShears(Player player) {
        playersWithShears.add(player.getUniqueId());

        givePlayerPermanentItems(player);
    }

    public ArmorUpgradeLevel getArmorLevel(Player player) {
        return playerArmorMap.getOrDefault(player.getUniqueId(), ArmorUpgradeLevel.LEATHER);
    }

    public PickaxeUpgradeLevel getPickaxeLevel(Player player) {
        return playerToPickaxeLevel.getOrDefault(player.getUniqueId(), PickaxeUpgradeLevel.NONE);
    }

    public AxeUpgradeLevel getAxeLevel(Player player) {
        return playerToAxeLevel.getOrDefault(player.getUniqueId(), AxeUpgradeLevel.NONE);
    }

    public boolean hasPermanentShears(Player player) {
        return playersWithShears.contains(player.getUniqueId());
    }

    public ShopItem getPickaxeShopItemForPlayer(Player player) {
        PickaxeUpgradeLevel level = getPickaxeLevel(player);

        String tierString;
        if(level == PickaxeUpgradeLevel.NONE) {
            tierString = PickaxeUpgradeLevel.WOOD.tierString();
        }else if (level == PickaxeUpgradeLevel.WOOD) {
            tierString = PickaxeUpgradeLevel.IRON.tierString();
        }else if(level == PickaxeUpgradeLevel.IRON) {
            tierString = PickaxeUpgradeLevel.GOLD.tierString();
        }else {
            tierString = PickaxeUpgradeLevel.DIAMOND.tierString();
        }

        String[] description = new String[]{"Tier &e" + tierString, "", "This item is upgradable!", "It will lose 1 tier if you die."};

        switch(level) {
            case NONE:
                return new ShopItem(
                        "Wooden Pickaxe (Efficiency I)",
                        new ItemBuilder(Material.WOOD_PICKAXE).addEnchant(Enchantment.DIG_SPEED, 1).hideEnchantment().toItemStack(),
                        10,
                        Material.IRON_INGOT,
                        description
                );
            case WOOD:
                return new ShopItem(
                        "Iron Pickaxe (Efficiency II)",
                        new ItemBuilder(Material.IRON_PICKAXE).addEnchant(Enchantment.DIG_SPEED, 2).hideEnchantment().toItemStack(),
                        10,
                        Material.IRON_INGOT,
                        description
                );
            case IRON:
                return new ShopItem(
                        "Gold Pickaxe (Efficiency III)",
                        new ItemBuilder(Material.GOLD_PICKAXE).addEnchant(Enchantment.DIG_SPEED, 3).hideEnchantment().toItemStack(),
                        3,
                        Material.GOLD_INGOT,
                        description
                );
            case GOLD:
                return new ShopItem(
                        "Diamond Pickaxe (Efficiency IV)",
                        new ItemBuilder(Material.DIAMOND_PICKAXE).addEnchant(Enchantment.DIG_SPEED, 4).hideEnchantment().toItemStack(),
                        12,
                        Material.GOLD_INGOT,
                        description
                );
            case DIAMOND:
                return new ShopItem(
                        "You have last tier",
                        new ItemBuilder(Material.BARRIER).toItemStack(),
                        0,
                        Material.IRON_INGOT,
                        new String[]{"You already have the last tier " + tierString}
                );
        }

        return null;
    }

    public ShopItem getAxeShopItemForPlayer(Player player) {
        AxeUpgradeLevel level = getAxeLevel(player);

        String tierString;
        if(level == AxeUpgradeLevel.NONE) {
            tierString = AxeUpgradeLevel.WOOD.tierString();
        }else if (level == AxeUpgradeLevel.WOOD) {
            tierString = AxeUpgradeLevel.IRON.tierString();
        }else if(level == AxeUpgradeLevel.IRON) {
            tierString = AxeUpgradeLevel.GOLD.tierString();
        }else {
            tierString = AxeUpgradeLevel.DIAMOND.tierString();
        }

        String[] description = new String[]{"Tier &e" + tierString, "", "This item is upgradable!", "It will lose 1 tier if you die."};

        switch(level) {
            case NONE:
                return new ShopItem(
                        "Wooden Axe (Efficiency I)",
                        new ItemBuilder(Material.WOOD_AXE).addEnchant(Enchantment.DIG_SPEED, 1).hideEnchantment().toItemStack(),
                        10,
                        Material.IRON_INGOT,
                        description
                );
            case WOOD:
                return new ShopItem(
                        "Iron Axe (Efficiency II)",
                        new ItemBuilder(Material.IRON_AXE).addEnchant(Enchantment.DIG_SPEED, 2).hideEnchantment().toItemStack(),
                        10,
                        Material.IRON_INGOT,
                        description
                );
            case IRON:
                return new ShopItem(
                        "Gold Axe (Efficiency III)",
                        new ItemBuilder(Material.GOLD_AXE).addEnchant(Enchantment.DIG_SPEED, 3).hideEnchantment().toItemStack(),
                        3,
                        Material.GOLD_INGOT,
                        description
                );
            case GOLD:
                return new ShopItem(
                        "Diamond Pickaxe (Efficiency IV)",
                        new ItemBuilder(Material.DIAMOND_AXE).addEnchant(Enchantment.DIG_SPEED, 4).hideEnchantment().toItemStack(),
                        12,
                        Material.GOLD_INGOT,
                        description
                );
            case DIAMOND:
                return new ShopItem(
                        "You have last tier",
                        new ItemBuilder(Material.BARRIER).toItemStack(),
                        0,
                        Material.IRON_INGOT,
                        new String[]{"You already have the last tier " + tierString}
                );
        }

        return null;
    }

    public void reset() {
        playersWithShears.clear();
        playerToAxeLevel.clear();
        playerToPickaxeLevel.clear();
        playerArmorMap.clear();
    }
}