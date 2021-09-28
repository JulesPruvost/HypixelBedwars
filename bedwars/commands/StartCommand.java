package me.gamendecat.hypixelbedwars.games.bedwars.commands;

import me.gamendecat.hypixelbedwars.games.bedwars.manager.GameManager;
import me.gamendecat.hypixelbedwars.games.bedwars.manager.GameState;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StartCommand implements CommandExecutor {

    private GameManager gameManager;

    public StartCommand(GameManager gameManager) {
        this.gameManager = gameManager;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(!(sender instanceof Player)) return true;
        if(Bukkit.getOnlinePlayers().size() < 2) {
            sender.sendMessage("§cNot enough players!");
            return true;
        }
        if(sender.hasPermission("bedwars.admin")) {
            if(gameManager.getState() != GameState.LOBBY) return true;
            gameManager.setState(GameState.STARTING);
        }else {
            sender.sendMessage("§cYou do not have permission");
        }
        return true;
    }
}
