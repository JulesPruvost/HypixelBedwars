/*
 * Copyright (c) 2021. Jules Pruvost
 * All rights reserved
 */

package me.gamendecat.hypixelbedwars.games.bedwars.gui.shop;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ShopItem {

    private String name;
    private ItemStack itemStack;
    private int amount;
    private int price;
    private Material materialBoughtwith;
    private String[] description;

    public ShopItem(String name, ItemStack itemStack, int amount, int price, Material materialBoughtWith, String[] description) {
        this.name = name;
        this.itemStack = itemStack;
        this.amount = amount;
        this.price = price;
        this.materialBoughtwith = materialBoughtWith;
        this.description = description;
    }

    public ShopItem(String name, ItemStack itemStack, int price, Material materialBoughtWith, String[] description) {
        this.name = name;
        this.itemStack = itemStack;
        this.amount = 1;
        this.price = price;
        this.materialBoughtwith = materialBoughtWith;
        this.description = description;
    }

    public ItemStack toItemStack() {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(name);
        List<String> strings = new ArrayList<>();
        strings.add(" ");
        for(String string : description) {
            strings.add("§7" + string);
        }
        strings.add(" ");
        strings.add("§7" + price + " " +  materialBoughtwith.name());

        itemMeta.setLore(strings);
        itemStack.setItemMeta(itemMeta);
        itemStack.setAmount(amount);
        return itemStack;
    }

    public boolean giveItemStack(Player player) {
        int i = player.getInventory().firstEmpty();
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(null);
        itemMeta.setLore(null);
        itemStack.setItemMeta(itemMeta);
        if(i != -1) {
            player.getInventory().setItem(i, itemStack);
            return true;
        }
        return false;
    }

    public String getName() {
        return name;
    }

    public int getAmount() {
        return amount;
    }

    public int getPrice() {
        return price;
    }

    public Material getMaterialBoughtwith() {
        return materialBoughtwith;
    }

    public String[] getDescription() {
        return description;
    }
}
