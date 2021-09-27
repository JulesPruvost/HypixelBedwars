package me.gamendecat.hypixelbedwars.games.bedwars.worlds.generators;

import me.gamendecat.hypixelbedwars.games.bedwars.utility.Colorize;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;

public class Generator {

    private GeneratorType type;
    private final Location location;
    private final boolean isIslandGenerator;
    private GeneratorTier currentTier;

    private ArmorStand armorStand = null;
    private ArmorStand armorStandone = null;

    public Generator(Location location, GeneratorType type, boolean isIslandGenerator) {
        this.isIslandGenerator = isIslandGenerator;
        this.location = location;
        this.type = type;
    }

    private boolean activated = false;
    private int secondsSinceActivation = 0;

    public void spawn() {
        if(type == GeneratorType.DIAMOND && isIslandGenerator) return;
        if(!activated) {
            if(armorStand != null) {
                armorStand.setCustomNameVisible(false);
            }
            return;
        }

        secondsSinceActivation++;

        if(!isIslandGenerator) {
            armorStand.setCustomNameVisible(true);
            armorStand.setCustomName(Colorize.color(getArmorStandName()));
        }

        if(secondsSinceActivation < getActivationTime()) return;

        secondsSinceActivation = 0;
        Material material = null;
        switch(type) {
            case IRON:
                material = Material.IRON_INGOT;
                break;
            case GOLD:
                material = Material.GOLD_INGOT;
                break;
            case DIAMOND:
                material = Material.DIAMOND;
                break;
            case EMERALD:
                material = Material.EMERALD;
                break;
        }

        location.getWorld().dropItem(location, new ItemStack(material));
    }

    public void setActivated(boolean activated) {
        if(activated == this.activated) return;

        this.activated = activated;

        if(isIslandGenerator) {
            return;
        }

        if(!activated && armorStand != null) {
            armorStand.remove();
        }

        this.setType(GeneratorTier.ONE);

        createArmorstand(location.clone().add(0,3,0), "&eTier &c" + currentTier.toString(), false, true);
        FloatingItem item = new FloatingItem(location.clone().add(0,2,0));
        if(type == GeneratorType.DIAMOND) {
            createArmorstand(location.clone().add(0,2.60,0), "&b&lDiamond", false, false);
            createArmorstand(location.clone().add(0,2.20,0), getArmorStandName(), true, false);
            item.spawn(new ItemStack(Material.DIAMOND_BLOCK), true, " ");
        }else if(type == GeneratorType.EMERALD) {
            createArmorstand(location.clone().add(0,2.5, 0), "&a&lEmerald", false, true);
            createArmorstand(location.clone().add(0,2,0), getArmorStandName(), true, false);
            item.spawn(new ItemStack(Material.EMERALD_BLOCK), true, " ");
        }
    }

    private int getActivationTime() {
        switch(type) {
            case IRON:
                return returnActivationTime(5, 3,2);
            case GOLD:
                return returnActivationTime(12, 8, 6);
            case EMERALD:
                if(isIslandGenerator) {
                    return returnActivationTime(30, 20, 15);
                }else {
                    return returnActivationTime(25, 20, 15);
                }
            case DIAMOND:
                return returnActivationTime(20, 15, 10);
        }
        return 20;
    }

    private String getArmorStandName() {
        int timeLeft = (getActivationTime() - secondsSinceActivation);
        if(timeLeft == 0) {
            timeLeft = getActivationTime();
        }
        return Colorize.color("&eSpawns in &c" + timeLeft + "&esecond" + (timeLeft == 1 ? "" : "s"));
    }

    public Location getLocation() {
        return location;
    }

    public GeneratorType getType() {
        return type;
    }

    public int returnActivationTime(int one, int two, int three) {
        if(currentTier == GeneratorTier.ONE) {
            return one;
        }else if(currentTier == GeneratorTier.TWO) {
            return two;
        }else {
            return three;
        }
    }

    public void setType(GeneratorTier tier) {
        this.currentTier = tier;
    }

    public void setTypeOne(GeneratorType type) {
        this.type = type;
    }

    public void createArmorstand(Location location, String name, boolean b, boolean c) {
        if(b){
            armorStand = location.getWorld().spawn(location, ArmorStand.class);
            System.out.println(armorStand + " 1");
            armorStand.setVisible(false);
            armorStand.setCustomNameVisible(true);
            armorStand.setCustomName(Colorize.color(name));
            armorStand.setGravity(false);
        }else {
            if(c) {
                armorStandone = location.getWorld().spawn(location, ArmorStand.class);
                System.out.println(armorStandone + " 2");
                armorStandone.setVisible(false);
                armorStandone.setCustomNameVisible(true);
                armorStandone.setCustomName(Colorize.color(name));
                armorStandone.setGravity(false);
            }else {
                ArmorStand armorStand = location.getWorld().spawn(location, ArmorStand.class);
                System.out.println(armorStand + " 3");
                armorStand.setVisible(false);
                armorStand.setCustomNameVisible(true);
                armorStand.setCustomName(Colorize.color(name));
                armorStand.setGravity(false);
            }
        }
    }

    public ArmorStand getArmorStandone() {
        return armorStandone;
    }
}
