/*
 * Copyright (c) 2021-2021. Jules Pruvost
 * All rights reserved
 */

package me.gamendecat.hypixelbedwars.games.bedwars.gui.shop;

import me.gamendecat.hypixelbedwars.games.bedwars.gui.GUI;
import me.gamendecat.hypixelbedwars.games.bedwars.manager.GameManager;
import me.gamendecat.hypixelbedwars.games.bedwars.utility.ItemBuilder;
import me.gamendecat.hypixelbedwars.games.bedwars.worlds.Island;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ItemShopGUI implements GUI {

    private GameManager gameManager;
    private Inventory inventory;
    private ShopCategory shopCategory;
    private List<ShopItem> itemsList;

    public ItemShopGUI(GameManager gameManager, Player player, ShopCategory shopCategory) {
        this.gameManager = gameManager;
        this.shopCategory = shopCategory;
        this.itemsList = shopCategory.getItems(gameManager.getPlayerManager(), player, gameManager.getGameWorld().islandForPlayer(player));

        Island island = gameManager.getGameWorld().islandForPlayer(player);

        inventory = Bukkit.createInventory(null, 54, getName());

        inventory.addItem(new ItemBuilder(Material.NETHER_STAR,1).setName("&bQuick Buy").toItemStack());
        inventory.addItem(new ItemBuilder(Material.HARD_CLAY,1).setName("&aBlocks").setLore("&eClick to view!").toItemStack());
        inventory.addItem(new ItemBuilder(Material.GOLD_SWORD,1).setName("&aMelee").setLore("&eClick to view!").toItemStack());
        inventory.addItem(new ItemBuilder(Material.CHAINMAIL_BOOTS,1).setName("&aArmor").setLore("&eClick to view!").toItemStack());
        inventory.addItem(new ItemBuilder(Material.STONE_PICKAXE,1).setName("&aTools").setLore("&eClick to view!").toItemStack());
        inventory.addItem(new ItemBuilder(Material.BOW,1).setName("&aRanged").setLore("&eClick to view!").toItemStack());
        inventory.addItem(new ItemBuilder(Material.BREWING_STAND,1).setName("&aPotions").setLore("&eClick to view!").toItemStack());
        inventory.addItem(new ItemBuilder(Material.BARRIER, 1).setName("&cOOOOPSIIE!!!!").setLore("&cAnother oopsie xD").toItemStack());
        inventory.addItem(new ItemBuilder(Material.BARRIER,1).setName("&cOopsie!").setLore("&cAnother oopsie").toItemStack());
        inventory.addItem(new ItemBuilder(Material.BARRIER, 1).setName("&cPOGCHAMP!!!!").setLore("&cAnother oopsie xD").toItemStack());

        inventory.setItem(9, new ItemBuilder(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5)).setName("&aPage Selected!").toItemStack());

        ItemStack itemstack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
        for (int i = 10; i < 18; i++) {
            inventory.setItem(i, itemstack);
        }

        /*inventory.setItem( 19 , new ItemBuilder(island.getColor().woolMaterial().toItemStack())
                        .setName("&eWool")
                        .setLore(Arrays.asList("&7Cost: &f4 Iron", " ", "&7Great for bridging across",  "islands! You get your own teams", "colored wool!"))
                        .toItemStack());
        inventory.setItem(20, new ItemBuilder(new ItemStack(Material.HARD_CLAY))
                        .setName("&eHardened Clay")
                        .setLore(Arrays.asList("&7Cost: &f12 Iron", " ", "&7Basic block to defend your bed."))
                        .toItemStack());
        inventory.setItem(21, new ItemBuilder(new ItemStack(Material.GLASS))
                        .setName("&eBlast-Proof Glass")
                        .setLore(Arrays.asList("&7Cost: &f12 Iron", " ", "&7Immune to explosions."))
                        .toItemStack());
        inventory.setItem(22, new ItemBuilder(new ItemStack(Material.ENDER_STONE))
                        .setName("&eEnd stone")
                        .setLore(Arrays.asList("&7Cost: &f24 Iron", " ", "&7Solid block to defend your bed."))
                        .toItemStack());
        inventory.setItem(23, new ItemBuilder(new ItemStack(Material.LADDER))
                        .setName("&eLadder")
                        .setLore(Arrays.asList("&7Cost: &f4 Iron", " ", "&7Useful to save cats stuck in", "&7trees"))
                        .toItemStack());
        inventory.setItem(24, new ItemBuilder(new ItemStack(Material.OBSIDIAN))
                        .setName("&eObsidian")
                        .setLore(Arrays.asList("&7Cost: &24 Emeralds", " ", "&7Extreme Protection for your bed."))
                        .toItemStack());
        inventory.setItem(25, new ItemBuilder(new ItemStack(Material.TNT))
                        .setName("&eTNT")
                        .setLore(Arrays.asList("&7Cost: &64 Gold", " ", "Instantly ignites, appropriate", "to explode things!"))
                        .toItemStack());
        inventory.setItem(28, new ItemBuilder(new ItemStack(Material.STONE_SWORD))
                        .setName("&eStone Sword")
                        .setLore(Arrays.asList("&7Cost: &f10 Iron", " "))
                        .toItemStack());
        inventory.setItem(29, new ItemBuilder(new ItemStack(Material.IRON_SWORD))
                        .setName("&eIron Sword")
                        .setLore(Arrays.asList("&7Cost: &67 Gold", " "))
                        .toItemStack());
         */

        for (ShopItem shopItem : shopCategory.getItems(gameManager.getPlayerManager(), player, island)) {
            inventory.addItem(shopItem.toItemStack());
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public String getName() {
        return "Item Shop";
    }

    @Override
    public GUI handleClick(Player player, ItemStack itemstack, InventoryView view) {
        Optional<ShopItem> item = itemsList.stream().filter(shopItem -> Objects.equals(shopItem.toItemStack().getItemMeta().getDisplayName(), itemstack.getItemMeta().getDisplayName())).findFirst();
        if(!item.isPresent()) return null;
        int a = 0;
        for(ItemStack itemStack : player.getInventory()) {
            if(itemStack == null) continue;
            if(itemStack.getType() == item.get().getMaterialBoughtwith()) {
                int i = item.get().getPrice();
                if(itemStack.getAmount() >= i) {
                    if(item.get().giveItemStack(player)) {
                        System.out.println("");
                        itemStack.setAmount(itemStack.getAmount() - i);
                        player.getInventory().setItem(a, itemStack);
                    }
                }else {
                    player.sendMessage("§cNot enough resources.");
                }
            }
            a++;
        }
        return null;
    }

    @Override
    public boolean isInventory(InventoryView view) {
        return view.getTitle().equals(getName());
    }
}
