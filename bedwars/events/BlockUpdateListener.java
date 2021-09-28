package me.gamendecat.hypixelbedwars.games.bedwars.events;

import me.gamendecat.hypixelbedwars.games.bedwars.manager.GameManager;
import me.gamendecat.hypixelbedwars.games.bedwars.manager.GameState;
import me.gamendecat.hypixelbedwars.games.bedwars.utility.Colorize;
import me.gamendecat.hypixelbedwars.games.bedwars.worlds.BlockManager;
import me.gamendecat.hypixelbedwars.games.bedwars.worlds.Island;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ItemSpawnEvent;

import java.util.UUID;

public class BlockUpdateListener implements Listener {

    private GameManager gameManager;
    private BlockManager blockManager;

    public BlockUpdateListener(GameManager gameManager) {
        this.gameManager = gameManager;
        this.blockManager = new BlockManager();
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        System.out.println("test1");
        if(e.getPlayer().getGameMode() == GameMode.CREATIVE) return;
        System.out.println("test2");
        if(gameManager.getState() != GameState.ACTIVE && gameManager.getState() != GameState.WON)  {
            e.setCancelled(true);
            return;
        }
        System.out.println("test3");

        Player player = e.getPlayer();

        Material type = e.getBlock().getType();
        if(type.toString().contains("BED")) {
            System.out.println("test4");
            Location location = e.getBlock().getLocation();
            Island island = gameManager.getGameWorld().getIslandForBed(location);
            if(island != null && !island.isMember(player)) {
                System.out.println("test5");
                for(UUID member : island.getPlayers()) {
                    System.out.println("test6");
                    Player member1 = Bukkit.getPlayer(member);
                    member1.sendTitle(Colorize.color("&c&lBED BROKEN!"), "");
                    member1.playSound(member1.getLocation(), Sound.WITHER_DEATH, 1, 1);
                }

                location.getWorld().strikeLightningEffect(location);

                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);

                Bukkit.broadcastMessage(Colorize.color("&c" + island.getColor().formattedName() + "'s bed was broken!"));

                System.out.println(island.alivePlayerCount());
                System.out.println(island.getColor().formattedName());
            }else {
                e.setCancelled(true);
            }


            return;
        }

        for(Island island : gameManager.getGameWorld().getIslands()) {
            if(island.isBlockWithingProtectedZone(e.getBlock())) {
                player.sendMessage(Colorize.color("&cYou can't break blocks here."));
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDrop(ItemSpawnEvent e){
        if(e.getEntity().getType().name().contains("BED")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        if(e.getPlayer().getGameMode() == GameMode.CREATIVE) return;
        if(gameManager.getState() != GameState.ACTIVE && gameManager.getState() != GameState.WON)  {
            e.setCancelled(true);
            return;
        }

        if(e.getBlock().getType().name().contains("BED")) {
            e.setCancelled(true);
            e.getPlayer().sendMessage("&c&lNO!");
            return;
        }

        if(e.getBlock().getLocation().getBlockY() > 110) {
            e.setCancelled(true);
            return;
        }

        for(Island island : gameManager.getGameWorld().getIslands()) {
            if(island.isBlockWithingProtectedZone(e.getBlock())) {
                e.getPlayer().sendMessage(Colorize.color("&cYou can't place blocks here."));
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBreakBlock(BlockBreakEvent e) {
        if(!blockManager.isPlacedbyPlayer(e.getBlock())) {
            if(e.getBlock().getType().name().contains("BED")) {
                e.getBlock().getDrops().clear();
                return;
            }
            e.setCancelled(true);
        }
    }
}
