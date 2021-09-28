package me.gamendecat.hypixelbedwars.games.bedwars.tasks;

import me.gamendecat.hypixelbedwars.games.bedwars.manager.GameManager;
import me.gamendecat.hypixelbedwars.games.bedwars.manager.GameState;
import me.gamendecat.hypixelbedwars.games.bedwars.utility.Colorize;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class GameStartingTask extends BukkitRunnable {

    private GameManager gameManager;

    public GameStartingTask(GameManager gameManager) {
        this.gameManager = gameManager;

    }

    private int timer = 20;

    @Override
    public void run() {
        if(timer <= 0) {
            gameManager.setState(GameState.ACTIVE);
            return;
        }

        if(timer <= 5 || timer == 20) {
            Bukkit.getServer().broadcastMessage(Colorize.color("&aGame starting in " + timer + "seconds..."));
            if(timer  <= 3) {
                for(Player player : Bukkit.getOnlinePlayers()) {
                    player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 1);
                    player.sendTitle("", Colorize.color("&e"  + timer));
                }
            }
        }
        timer--;
    }

    public int getTimeUntilStart() {
        return timer;
    }
}
