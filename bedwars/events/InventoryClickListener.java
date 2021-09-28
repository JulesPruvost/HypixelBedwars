package me.gamendecat.hypixelbedwars.games.bedwars.events;

import me.gamendecat.hypixelbedwars.games.bedwars.manager.GameState;
import me.gamendecat.hypixelbedwars.games.bedwars.gui.GUI;
import me.gamendecat.hypixelbedwars.games.bedwars.manager.GameManager;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryClickListener implements Listener {

    private GameManager gameManager;

    public InventoryClickListener(GameManager gameManager) {
        this.gameManager = gameManager;
    }
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if(gameManager.getState() == GameState.LOBBY && e.getWhoClicked().getGameMode() != GameMode.CREATIVE) {
            e.setCancelled(true);
        }
        if(e.getCurrentItem() == null) return;
        /*String materialName = e.getCurrentItem().getType().name();
        if(materialName.contains("BOOTS") || materialName.contains("LEGGINGS") || materialName.contains("CHESTPLATE") || materialName.contains("HELMET")) {
            e.setCancelled(true);
            return;
        }*/
        if(!e.getCurrentItem().hasItemMeta()) return;
        Player player = (Player) e.getWhoClicked();

        GUI gui = gameManager.getGuiManager().getOpenGUI(player);

        if(gui == null) return;

        e.setCancelled(true);

        GUI newGUI = gui.handleClick(player, e.getCurrentItem(), e.getView());

        e.getView().close();

        if(newGUI != null) {
            gameManager.getGuiManager().setGUI(player, newGUI);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        Player player = (Player) e.getPlayer();

        gameManager.getGuiManager().clear(player);
    }
}
