package me.gamendecat.hypixelbedwars.games.bedwars.gui;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class GUIManager {

    private Map<Player, GUI> playerGUIMap = new HashMap<>();

    public GUI getOpenGUI(Player player) {
        System.out.println(playerGUIMap.get(player));
        return playerGUIMap.get(player);
    }

    public void setGUI(Player player, GUI gui) {
        if(gui == null) {
            player.closeInventory();
            return;
        }

        player.closeInventory();
        playerGUIMap.put(player, gui);
        player.openInventory(gui.getInventory());
    }

    public void clear(Player player) {
        playerGUIMap.put(player, null);
    }


}
