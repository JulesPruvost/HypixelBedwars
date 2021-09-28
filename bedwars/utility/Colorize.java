package me.gamendecat.hypixelbedwars.games.bedwars.utility;

import org.bukkit.ChatColor;

public class Colorize {


    public static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
