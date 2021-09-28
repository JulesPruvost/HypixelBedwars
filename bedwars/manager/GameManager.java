package me.gamendecat.hypixelbedwars.games.bedwars.manager;

import dev.jcsoftware.jscoreboards.JPerPlayerScoreboard;
import me.gamendecat.hypixelbedwars.HypixelBedwars;
import me.gamendecat.hypixelbedwars.games.bedwars.config.ConfigurationManager;
import me.gamendecat.hypixelbedwars.games.bedwars.gui.GUIManager;
import me.gamendecat.hypixelbedwars.games.bedwars.players.PlayerManager;
import me.gamendecat.hypixelbedwars.games.bedwars.setup.SetupWizardManager;
import me.gamendecat.hypixelbedwars.games.bedwars.tasks.GameStartingTask;
import me.gamendecat.hypixelbedwars.games.bedwars.tasks.GameTickTask;
import me.gamendecat.hypixelbedwars.games.bedwars.utility.Colorize;
import me.gamendecat.hypixelbedwars.games.bedwars.worlds.GameWorld;
import me.gamendecat.hypixelbedwars.games.bedwars.worlds.Island;
import me.gamendecat.hypixelbedwars.games.bedwars.worlds.generators.FloatingItem;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class GameManager {

    private HypixelBedwars plugin;
    private JPerPlayerScoreboard scoreboard = null;

    private PlayerManager playerManager;
    private SetupWizardManager setupWizardManager;
    private ConfigurationManager configurationManager;
    private GUIManager guiManager;

    private GameStartingTask gameStartingTask;
    private GameTickTask gameTickTask;

    private World lobbyWorld;
    private Location lobbyWorldSpawnLocation;

    private GameWorld gameWorld;

    private String chosenNextMapName;

    private GameState state = GameState.PRELOBBY;

    public GameManager(HypixelBedwars plugin) {
        this.plugin = plugin;

        this.playerManager = new PlayerManager(this);
        this.configurationManager = new ConfigurationManager(this);
        this.setupWizardManager = new SetupWizardManager(this);
        this.guiManager = new GUIManager();

        //this.scoreboard = makeScoreboard();

        lobbyWorld = Bukkit.getWorld("World");
        lobbyWorld.setAutoSave(true);
        lobbyWorld.setDifficulty(Difficulty.NORMAL);
        lobbyWorld.getEntities().stream().filter(entity -> !(entity instanceof ArmorStand)).forEach(Entity::remove);

        lobbyWorldSpawnLocation = new Location(lobbyWorld, -39, 71, 0, -90, 3);

        setState(GameState.PRELOBBY);
    }

    public void setState(GameState state) {
        this.state = state;

        switch(state) {
            case PRELOBBY:
                this.gameWorld = null;
                break;
            case LOBBY:
                for(Player player : Bukkit.getOnlinePlayers()) {
                    player.teleport(gameWorld.getLobbyPosition());
                }

                playerManager.giveAllTeamSelector();
                break;
            case STARTING:
                for(Player p : Bukkit.getOnlinePlayers()) {
                    p.teleport(gameWorld.getLobbyPosition());
                }

                this.gameStartingTask = new GameStartingTask(this);
                this.gameStartingTask.runTaskTimer(plugin, 0, 20);
                break;
            case ACTIVE:
                if(this.gameStartingTask != null) {
                    this.gameStartingTask.cancel();
                }
                this.gameStartingTask = null;

                this.gameTickTask = new GameTickTask(this);
                this.gameTickTask.runTaskTimer(plugin, 0, 20);

                FloatingItem.enable(plugin);

                for(Player player : Bukkit.getOnlinePlayers()) {
                    Island island = gameWorld.islandForPlayer(player);
                    String islandColor;
                    if(island == null) {
                        Optional<Island> optionalIsland = gameWorld.getIslands().stream().filter(found -> found.getPlayers().size() < gameWorld.getMaxTeamSize()).findFirst();
                        if(!optionalIsland.isPresent()) {
                            player.kickPlayer("Not enough islands.");
                            continue;
                        }
                        optionalIsland.get().addMember(player.getUniqueId());

                        islandColor = "§" + optionalIsland.get().getColor().getChatColor().getChar();

                        player.setPlayerListName(islandColor + optionalIsland.get().getColor().formattedName().charAt(0) + " " + player.getName());

                        playerManager.setPlaying(player);

                        continue;
                    }

                    islandColor = "§" + island.getColor().getChatColor().getChar();

                    player.setPlayerListName(islandColor + island.getColor().formattedName().charAt(0) + " " + player.getName());

                    playerManager.setPlaying(player);
                }

                for(Island island : getGameWorld().getIslands()) {
                    island.spawnShops();
                }

               break;
            case WON:
                for(Player player : Bukkit.getOnlinePlayers()) {
                    player.setPlayerListName(player.getName());
                }
                Optional<Island> finalIsland = getGameWorld().getActiveIslands().stream().findFirst();
                if (!finalIsland.isPresent()) {
                    Bukkit.broadcastMessage(Colorize.color("&cNo winner found, ending game..."));
                }else {
                    Island island = finalIsland.get();
                    Bukkit.broadcastMessage(Colorize.color("&6The game is over! " + island.getColor().formattedName() + " has won!"));
                }
                if(this.gameTickTask != null) {
                    this.gameTickTask.cancel();
                    this.gameTickTask = null;
                }

                Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> setState(GameState.RESET), 20 * 8);
                break;
            case RESET:
                for(Player player : Bukkit.getOnlinePlayers()) {
                    player.teleport(lobbyWorldSpawnLocation);
                    playerManager.clearPlayer(player);
                }

                chosenNextMapName = null;

                Bukkit.getServer().getScheduler().runTaskLater(
                        plugin,
                        () -> {
                            gameWorld.resetWorld();

                        },
                60);
                break;
        }

        //todo: handle changes
    }

    public GameState getState() {
        return state;
    }

    public JPerPlayerScoreboard getScoreboard() {
        return scoreboard;
    }

    public HypixelBedwars getPlugin() {
        return this.plugin;
    }

    public ConfigurationManager getConfigurationManager() {
        return configurationManager;
    }


    public GUIManager getGuiManager() {
        return guiManager;
    }

    public SetupWizardManager getSetupWizardManager() {
        return setupWizardManager;
    }

    public GameWorld getGameWorld() {
        return gameWorld;
    }

    public void endGameIfNeeded() {
        if(state != GameState.ACTIVE) return;
        if(getGameWorld().getActiveIslands().size() > 1) {
            return; // no need to end
        }

        setState(GameState.WON);
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public void refreshScoreboard(Team team) {
        team.setCanSeeFriendlyInvisibles(true);
    }

    public void setGameWorld(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
    }

    public GameStartingTask getGameStartingTask() {
        return gameStartingTask;
    }

    public GameTickTask getGameTickTask() {
        return gameTickTask;
    }
}
