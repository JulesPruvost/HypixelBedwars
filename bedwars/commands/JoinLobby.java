/*
 * Copyright (c) 2021. Jules Pruvost
 * All rights reserved
 */

package me.gamendecat.hypixelbedwars.games.bedwars.commands;

import me.gamendecat.hypixelbedwars.games.bedwars.manager.GameManager;
import me.gamendecat.hypixelbedwars.games.bedwars.manager.GameState;
import me.gamendecat.hypixelbedwars.games.bedwars.utility.Colorize;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JoinLobby implements CommandExecutor {

    private GameManager gameManager;

    public JoinLobby(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        if(!player.hasPermission("bedwars.admin")) {
            player.sendMessage(Colorize.color("&cNo permission!"));
        }
        if(gameManager.getState() == GameState.LOBBY) {
            player.teleport(gameManager.getGameWorld().getLobbyPosition());
            gameManager.getPlayerManager().giveTeamSelector(player);
            return true;
        }
        if(gameManager.getState() != GameState.PRELOBBY) return true;

        if(args.length < 1) {
            player.sendMessage("/join <map name>");
            return true;
        }

        String string = args[0];
        World world = Bukkit.getWorld(string);
        if(world == null) {
            player.sendMessage(Colorize.color("&cThis world is invalid"));
            return true;
        }

        this.gameManager.getConfigurationManager().loadWorld(this.gameManager.getConfigurationManager().randomMapName(), (gameWorld) -> {
            gameManager.setGameWorld(gameWorld);
            gameManager.setState(GameState.LOBBY);
            Bukkit.broadcastMessage(Colorize.color("&7(&c!&7) &e" + player.getDisplayName() + " &7just joined bedwars!"));
            //for(Island island : gameWorld.getIslands()) {
            //char islandColorChar = island.getColor().getChatColor().getChar();
            //this.scoreboard.createTeam(island.getColor().formattedName(),
            //    "&" + island.getColor().getChatColor().getChar() + "&" + islandColorChar + "&l" + island.getColor().formattedName().charAt(0) + "&r&" + island);
            //}
        });


        return true;
    }
}
