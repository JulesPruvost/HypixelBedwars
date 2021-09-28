package me.gamendecat.hypixelbedwars.games.bedwars.worlds;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.material.Wool;

public enum IslandColor {
    RED,
    BLUE,
    GREEN,
    YELLOW,
    AQUA,
    WHITE,
    PINK,
    GRAY;

    public String formattedName() {
        String caps = this.toString();
        char i = caps.toUpperCase().charAt(0);
        return i + caps.substring(1);
    }

    public ChatColor getChatColor() {
        if(this == PINK) {
            return ChatColor.LIGHT_PURPLE;
        }
        return ChatColor.valueOf(this.toString());
    }

    public Color getColor() {
        switch(this) {
            case RED:
                return Color.RED;
            case BLUE:
                return Color.BLUE;
            case GREEN:
                return Color.GREEN;
            case YELLOW:
                return Color.YELLOW;
            case AQUA:
                return Color.AQUA;
            case WHITE:
                return Color.WHITE;
            case PINK:
                return Color.FUCHSIA;
            case GRAY:
                return Color.GRAY;
        }
        return Color.BLACK;
    }

    public Wool woolMaterial() {
        Wool teamWoolMaterial = new Wool(DyeColor.BLACK);
        switch(this) {
            case RED:
                teamWoolMaterial = new Wool(DyeColor.RED);
                break;
            case BLUE:
                teamWoolMaterial = new Wool(DyeColor.BLUE);
                break;
            case GREEN:
                teamWoolMaterial = new Wool(DyeColor.GREEN);
                break;
            case YELLOW:
                teamWoolMaterial = new Wool(DyeColor.YELLOW);
                break;
            case AQUA:
                teamWoolMaterial = new Wool(DyeColor.LIGHT_BLUE);
                break;
            case WHITE:
                teamWoolMaterial = new Wool(DyeColor.WHITE);
                break;
            case PINK:
                teamWoolMaterial = new Wool(DyeColor.PINK);
                break;
            case GRAY:
                teamWoolMaterial = new Wool(DyeColor.GRAY);
                break;
        }
        return teamWoolMaterial;
    }
}
