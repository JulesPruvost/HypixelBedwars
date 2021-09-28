package me.gamendecat.hypixelbedwars.games.bedwars.events;

import me.gamendecat.hypixelbedwars.games.bedwars.manager.GameManager;
import me.gamendecat.hypixelbedwars.games.bedwars.manager.GameState;
import me.gamendecat.hypixelbedwars.games.bedwars.utility.Colorize;
import me.gamendecat.hypixelbedwars.games.bedwars.worlds.GameWorld;
import me.gamendecat.hypixelbedwars.games.bedwars.worlds.Island;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerLoginEventListener implements Listener {

    private GameManager gameManager;

    public PlayerLoginEventListener(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        e.setJoinMessage(Colorize.color("&8[&2+&8] " + e.getPlayer().getDisplayName()));

        //gameManager.getScoreboard().addPlayer(e.getPlayer());

        if("aa".equals("aa")) {
            return;
        }

        if(gameManager.getState() == GameState.ACTIVE) {
            GameWorld world = gameManager.getGameWorld();
            Island playerIsland = world.islandForPlayer(e.getPlayer());

            if(playerIsland != null) {
                if(playerIsland.isBedPlaced()) {
                    e.getPlayer().teleport(playerIsland.getSpawnLocation());
                }
            }
        }else if(gameManager.getState() == GameState.LOBBY) {
            e.getPlayer().getEnderChest().clear();
            e.getPlayer().teleport(gameManager.getGameWorld().getLobbyPosition());
            e.getPlayer().setGameMode(GameMode.SURVIVAL);
            gameManager.getPlayerManager().giveTeamSelector(e.getPlayer());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        e.setQuitMessage(Colorize.color("&8[&c-&8] " + e.getPlayer().getDisplayName()) );

        //gameManager.getScoreboard().removePlayer(e.getPlayer());
    }
}
