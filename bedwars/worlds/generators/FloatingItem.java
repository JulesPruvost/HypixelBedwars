/*
 * Copyright (c) 2021. Jules Pruvost
 * All rights reserved
 */

package me.gamendecat.hypixelbedwars.games.bedwars.worlds.generators;

import com.google.common.collect.Lists;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FloatingItem {

    private static List<FloatingItem> items = new ArrayList<>();
    private Location location, sameLocation;
    private ArmorStand armorStand;
    private boolean floatLoop;
    private List<ArmorStand> texts = new ArrayList<>();

    /**
     * Constructs a new floating item and adds it to the items list
     *
     * @param location The location to spawn item at
     */
    public FloatingItem(Location location) {
        this.location = location;
        this.floatLoop = true;

        items.add(this);
    }

    /**
     * @note This needs to be ran on onEnable in order to update properly
     */
    public static void enable(JavaPlugin plugin) {
        new BukkitRunnable() {

            @Override
            public void run() {
                FloatingItem.getFloatingItems().stream().filter(i -> i.getArmorStand() != null).forEach(i -> i.update());
            }
        }.runTaskTimerAsynchronously(plugin, 0, 1);
    }

    /**
     *
     * @param text Then text of the floating item
     * @param itemStack The itemstack
     * @param big Whether the item should be big or not
     */
    public void spawn(ItemStack itemStack, boolean big, String... text) {
        armorStand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        armorStand.setGravity(false);
        armorStand.setHelmet(itemStack);
        armorStand.setVisible(false);
        armorStand.setSmall(big ? false : true);

        this.sameLocation = armorStand.getLocation();

        addText(this, text);
    }

    /**
     * Updates the floating item
     */
    public void update() {
        Location location = armorStand.getLocation();

        if (!this.floatLoop) {
            location.add(0, 0.01, 0);
            location.setYaw((location.getYaw() + 7.5F));

            armorStand.teleport(location);

            if (armorStand.getLocation().getY() > (0.25 + sameLocation.getY()))
                this.floatLoop = true;
        }
        else {
            location.subtract(0, 0.01, 0);
            location.setYaw((location.getYaw() - 7.5F));

            armorStand.teleport(location);

            if (armorStand.getLocation().getY() < (-0.25 + sameLocation.getY()))
                this.floatLoop = false;
        }
    }

    private void addText(FloatingItem floatingItem, String... text) {
        ArmorStand armorStand = null;
        List<String> lines = Arrays.asList(text);
        lines = Lists.reverse(lines);

        double y = 0.25D;

        for (int i = 0; i < lines.size(); i++) {
            armorStand = (ArmorStand) location.getWorld().spawnEntity(location.clone().add(0, y, 0), EntityType.ARMOR_STAND);
            armorStand.setGravity(false);
            armorStand.setCustomName(lines.get(i).replace('&', '§'));
            armorStand.setCustomNameVisible(true);
            armorStand.setVisible(false);
            y += 0.21D;

            texts.add(armorStand);
        }
    }

    /**
     * Deletes all text that the floating item has
     */
    public void deleteAllText() {
        texts.forEach(t -> t.remove());
    }

    /**
     * Deletes this floating item
     */
    public void delete() {
        deleteAllText();
        if (armorStand != null)
            armorStand.remove();
    }

    /**
     * Resets all floating items
     */
    public void reset() {
        getFloatingItems().remove(this);
    }

    /**
     * Deletes all floating items on the server
     */
    public static void deleteAll() {
        getFloatingItems().forEach(i -> i.delete());
        getFloatingItems().clear();
    }

    /**
     * Gets all registered floating items
     * @return All floating items
     */
    public static List<FloatingItem> getFloatingItems() {
        return items;
    }

    /**
     * Gets all text holders
     * @return All text holders
     */
    public List<ArmorStand> getTexts() {
        return texts;
    }

    /**
     * Gets the location of the floating item
     * @return The location
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Gets the armorstand of the floating item
     * @return The armorstand
     */
    public ArmorStand getArmorStand() {
        return armorStand;
    }
}
