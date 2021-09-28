package me.gamendecat.hypixelbedwars.games.bedwars.events;

import me.gamendecat.hypixelbedwars.games.bedwars.manager.GameManager;
import me.gamendecat.hypixelbedwars.games.bedwars.manager.GameState;
import me.gamendecat.hypixelbedwars.games.bedwars.tasks.PlayerRespawnTask;
import me.gamendecat.hypixelbedwars.games.bedwars.utility.Colorize;
import me.gamendecat.hypixelbedwars.games.bedwars.worlds.Island;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.UUID;

public class PlayerDeathEventListener implements Listener {

    private GameManager gameManager;
    private HashMap<UUID, EntityDamageEvent.DamageCause> lastDamagecauseMap = new HashMap<>();
    private HashMap<UUID, String> lastDamageMap = new HashMap<>();

    public PlayerDeathEventListener(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if(gameManager.getState() != GameState.ACTIVE) {
            e.setCancelled(true);
            return;
        }

        if(e.getEntity() instanceof Villager || e.getEntity() instanceof Skeleton) {
            e.setCancelled(true);
            return;
        }

        if(!(e.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) e.getEntity();
        if(player.getHealth() >= 0.0D) {
            return;
        }

        System.out.println();

        respawnTask(player, e.getFinalDamage());
    }

    @EventHandler
    public void onDamageByOther(EntityDamageByEntityEvent e) {
        if(!(e.getEntity() instanceof Player || !(e.getDamager() instanceof Player))) {
            return;
        }

        if(gameManager.getState() != GameState.ACTIVE) {
            return;
        }

        Player player = (Player) e.getEntity();
        Player damager = (Player) e.getDamager();

        Island damagerIsland = gameManager.getGameWorld().islandForPlayer(damager);
        Island playerIsland = gameManager.getGameWorld().islandForPlayer(player);


        if(damagerIsland == playerIsland) {
            e.setCancelled(true);
            return;
        }

        //if(damagerIsland != null) {
          //  String damagerColor = "&" + damagerIsland.getColor().getChatColor().getChar();
            //lastDamageMap.put(player.getUniqueId(), damagerColor + damager.getDisplayName() + "&f");
        //}

        if(gameManager.getState() != GameState.ACTIVE) {
            e.setCancelled(true);
            return;
        }

        if(e.getEntity() instanceof Villager || e.getEntity() instanceof Skeleton) {
            e.setCancelled(true);
            return;
        }

        respawnTask(player, e.getFinalDamage());
    }

    public void respawnTask(Player player, double finalDamage) {

        if((player.getHealth() - finalDamage) > 0.5) {
            return;
        }

        Island playerIsland = gameManager.getGameWorld().islandForPlayer(player);

        for(ItemStack itemStack : player.getInventory()) {
            if(itemStack == null) continue;
            if(itemStack.getItemMeta() == null) continue;
            if(itemStack.getType().name() == null) continue;
            if(itemStack.getType() == Material.DIAMOND || itemStack.getType() == Material.IRON_INGOT || itemStack.getType() == Material.GOLD_INGOT || itemStack.getType() == Material.EMERALD || itemStack.getType().name().contains("WOOL")){
                player.getLocation().getWorld().dropItemNaturally(player.getLocation(), itemStack);
            }
        }

        player.getInventory().clear();
        player.setBedSpawnLocation(playerIsland.getSpawnLocation(), true);

        Bukkit.getServer().getScheduler().runTaskLater(gameManager.getPlugin(), () -> {
            player.setHealth(player.getMaxHealth());
            player.playSound(player.getLocation(), Sound.PIG_DEATH, 1, 1);

            boolean finalKill = !playerIsland.isBedPlaced();

            String islandColor = "&" + playerIsland.getColor().getChatColor().getChar();
            EntityDamageEvent.DamageCause lastDamageCause = lastDamagecauseMap.getOrDefault(player.getUniqueId(), EntityDamageEvent.DamageCause.VOID);
            switch(lastDamageCause) {
                case FALL:
                    Bukkit.broadcastMessage(Colorize.color(islandColor + player.getDisplayName() + " &7Hit the ground too hard." + (finalKill ? " &3&lFINAL KILL" : "")));
                    break;
                case VOID:
                case SUICIDE:
                    Bukkit.broadcastMessage(Colorize.color(islandColor + player.getDisplayName() + " &7Fell into the void." + (finalKill ? " &3&lFINAL KILL" : "")));
                    break;
                case ENTITY_ATTACK:
                    Bukkit.broadcastMessage(Colorize.color(islandColor + player.getDisplayName() + " &7was killed by " + lastDamageMap.getOrDefault(player.getUniqueId(), "&can act of God") + "&7!" + (finalKill ? " &3&lFINAL KILL" : "")));
                    break;
                default:
                    Bukkit.broadcastMessage(Colorize.color(islandColor + player.getDisplayName() + " &7Died suddenly." + (finalKill ? " &3&lFINAL KILL" : "")));
                    break;
            }

            lastDamageMap.remove(player.getUniqueId());
            lastDamagecauseMap.remove(player.getUniqueId());

            if(playerIsland.isBedPlaced()) {
                gameManager.getPlayerManager().setSpectatorMode(player, false);

                BukkitTask task = Bukkit.getServer().getScheduler().runTaskTimer(gameManager.getPlugin(), new PlayerRespawnTask(player, gameManager, playerIsland),0, 20);
                Bukkit.getServer().getScheduler().runTaskLater(gameManager.getPlugin(), task::cancel,20 * 6);
            } else {
                player.getWorld().strikeLightningEffect(player.getLocation());
                player.sendTitle(Colorize.color("&cYOU DIED"), "");
                if(!gameManager.getGameWorld().getActiveIslands().contains(playerIsland)) {
                    Bukkit.broadcastMessage("");
                    Bukkit.broadcastMessage(Colorize.color("&f&lTEAM ELIMINATED > &r&" +
                            playerIsland.getColor().getChatColor().getChar() +
                            playerIsland.getColor().formattedName()) +
                            " &c has been elimanted");
                }
                Bukkit.broadcastMessage(" ");
                for(Player all : Bukkit.getOnlinePlayers()) {
                    all.playSound(all.getLocation(), Sound.WITHER_DEATH, 1, 1);
                }
            }
            gameManager.getPlayerManager().setSpectatorMode(player, true);
        }, 2);
    }

    @EventHandler
    public void onDie(PlayerDeathEvent e) {
        e.setDeathMessage(null);
    }

}

