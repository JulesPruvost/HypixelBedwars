package me.gamendecat.hypixelbedwars.games.bedwars.commands;

import me.gamendecat.hypixelbedwars.games.bedwars.manager.GameManager;
import me.gamendecat.hypixelbedwars.games.bedwars.worlds.GameWorld;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetupWizardCommand implements CommandExecutor {

    private GameManager gameManager;

    public SetupWizardCommand(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(!(sender instanceof Player)) return true;

        Player player = (Player) sender;

        if(!player.hasPermission("bedwars.admin")) {
            player.sendMessage("§cYou don'(t have permission to run this command.");
            return true;
        }
        if(args.length < 1) {
            player.sendMessage("/setup <map name>");
            return true;
        }

        String mapName = args[0];

        if(mapName.equalsIgnoreCase("exit")) {
            gameManager.getSetupWizardManager().removeFromWizard(player);
            return true;
        }
        player.sendMessage("Loading world, one moment...");

        GameWorld world = new GameWorld(mapName);

        world.loadWorld(gameManager, true, () -> gameManager.getSetupWizardManager().activateSetupWizard(player, world));
        return true;
    }
}
