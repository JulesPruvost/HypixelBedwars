package me.gamendecat.hypixelbedwars.games.bedwars.setup;

import me.gamendecat.hypixelbedwars.games.bedwars.utility.ItemBuilder;
import me.gamendecat.hypixelbedwars.games.bedwars.manager.GameManager;
import me.gamendecat.hypixelbedwars.games.bedwars.worlds.IslandColor;
import me.gamendecat.hypixelbedwars.games.bedwars.worlds.GameWorld;
import me.gamendecat.hypixelbedwars.games.bedwars.worlds.Island;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class SetupWizardManager {

    public Map<Player, Island> playerToIslandMap = new HashMap<>();
    public Map<Player, GameWorld> playerToGameWorldMap= new HashMap<>();

    private GameManager gameManager;

    public SetupWizardManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public boolean isInWizard(Player player) {
        return playerToGameWorldMap.containsKey(player);
    }

    public void activateSetupWizard(Player player, GameWorld world) {
        player.getInventory().clear();
        player.teleport(new Location(world.getWorld(), 0, 69, 0));
        player.setGameMode(GameMode.CREATIVE);

        worldSetupWizard(player, world);
    }

    public void worldSetupWizard(Player player, GameWorld world) {
        player.getInventory().clear();

        player.getInventory().addItem(
                new ItemBuilder(Material.DIAMOND).setName("&aset Diamond Generator").toItemStack()
        );

        player.getInventory().addItem(
                new ItemBuilder(Material.EMERALD).setName("&aset Emerald Generator").toItemStack()
        );

        player.getInventory().addItem(
                new ItemBuilder(Material.BLAZE_POWDER).setName("&aset Lobby Spawn").toItemStack()
        );

        player.getInventory().addItem(
                new ItemBuilder(Material.STICK).setName("&aChange Island").toItemStack()
        );

        player.setGameMode(GameMode.CREATIVE);
        playerToGameWorldMap.put(player, world);
    }

    public void teamSetupWizard(Player player, IslandColor teamcolor) {
        player.getInventory().clear();
        player.getInventory().addItem(
                new ItemBuilder(Material.STICK).setName("&aCorner Stick").toItemStack()
        );
        player.getInventory().addItem(
                new ItemBuilder(Material.BLAZE_ROD).setName("&aSecond Corner Stick").toItemStack()
        );
        player.getInventory().addItem(
                new ItemBuilder(Material.EGG).setName("&aSet Shop Location").toItemStack()
        );
        player.getInventory().addItem(
                new ItemBuilder(Material.IRON_INGOT).setName("&aSet Generator Location").toItemStack()
        );
        player.getInventory().addItem(
                new ItemBuilder(Material.DIAMOND_SWORD).setName("&aSet Team Upgrade Location").toItemStack()
        );
        player.getInventory().addItem(
                new ItemBuilder(Material.BOWL).setName("&aSet Spawn Location").toItemStack()
        );
        player.getInventory().addItem(
                new ItemBuilder(Material.MAGMA_CREAM).setName("&aSet Bed Location").toItemStack()
        );
        player.getInventory().addItem(
                new ItemBuilder(teamcolor.woolMaterial().toItemStack()).setName("&aChange Island").toItemStack()
        );
        player.getInventory().addItem(
                new ItemBuilder(Material.RED_MUSHROOM).setName("&aSave Island").toItemStack()
        );

        Island island = new Island(getWorld(player), teamcolor);
        playerToIslandMap.put(player, island);
    }

    public GameWorld getWorld(Player player) {
        return playerToGameWorldMap.get(player);
    }

    public Island getIsland(Player player) {
        return playerToIslandMap.get(player);
    }

    public void removeFromWizard(Player player) {
        player.teleport(new Location(Bukkit.getWorld("world"),-39, 71, 0, -90, 3));
    }
}
