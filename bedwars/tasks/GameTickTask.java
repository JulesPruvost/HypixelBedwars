package me.gamendecat.hypixelbedwars.games.bedwars.tasks;

import me.gamendecat.hypixelbedwars.games.bedwars.manager.GameManager;
import me.gamendecat.hypixelbedwars.games.bedwars.manager.GameState;
import me.gamendecat.hypixelbedwars.games.bedwars.utility.Colorize;
import me.gamendecat.hypixelbedwars.games.bedwars.worlds.Island;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class GameTickTask extends BukkitRunnable {

    private GameManager gameManager;

    public GameTickTask(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    private int currentSecond = 0;

    @Override
    public void run() {
        //this.gameManager.getScoreboard().updateScoreboard();
        currentSecond++;

        gameManager.getGameWorld().tick(currentSecond);

        for(Player player : Bukkit.getOnlinePlayers()) {
            if(gameManager.getState() != GameState.ACTIVE) return;
            if(player.getLocation().getBlockY() <= 0) {
                if(player.getGameMode() == GameMode.SURVIVAL) {
                    if(gameManager.getGameWorld().islandForPlayer(player) == null) {
                        return;
                    }

                    gameManager.getPlayerManager().setSpectatorMode(player, true);

                    Island playerIsland = gameManager.getGameWorld().islandForPlayer(player);

                    boolean finalKill = !playerIsland.isBedPlaced();
                    if(!finalKill) {
                        BukkitTask task =
                                Bukkit.getServer().getScheduler().runTaskTimer(
                                        gameManager.getPlugin(),
                                        new PlayerRespawnTask(player, gameManager, playerIsland),
                                        0, 20);
                        Bukkit.getServer().getScheduler().runTaskLater(gameManager.getPlugin(), () -> {
                            task.cancel();
                        },140);
                    }
                    String islandColor = "&" + playerIsland.getColor().getChatColor().getChar();
                    Bukkit.broadcastMessage(Colorize.color(islandColor + player.getDisplayName() + " &7Fell into the void." + (finalKill ? " &3&lFINAL KILL" : "")));
                }else {
                    player.teleport(gameManager.getGameWorld().getLobbyPosition());
                }
            }
        }
    }

    public int getCurrentSecond() {
        return currentSecond;
    }
}
