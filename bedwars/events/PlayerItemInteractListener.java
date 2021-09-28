package me.gamendecat.hypixelbedwars.games.bedwars.events;

import me.gamendecat.hypixelbedwars.games.bedwars.gui.shop.IslandUpgradeGUI;
import me.gamendecat.hypixelbedwars.games.bedwars.gui.shop.ShopCategory;
import me.gamendecat.hypixelbedwars.games.bedwars.manager.GameState;
import me.gamendecat.hypixelbedwars.games.bedwars.gui.shop.ItemShopGUI;
import me.gamendecat.hypixelbedwars.games.bedwars.gui.SetupWizardIslandSelectorGUI;
import me.gamendecat.hypixelbedwars.games.bedwars.manager.GameManager;
import me.gamendecat.hypixelbedwars.games.bedwars.gui.TeamPickerGUI;
import me.gamendecat.hypixelbedwars.games.bedwars.worlds.GameWorld;
import me.gamendecat.hypixelbedwars.games.bedwars.worlds.Island;
import me.gamendecat.hypixelbedwars.games.bedwars.worlds.generators.Generator;
import me.gamendecat.hypixelbedwars.games.bedwars.worlds.generators.GeneratorType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerItemInteractListener implements Listener {

    private GameManager gameManager;

    @EventHandler
    public void onInteractWithShop(PlayerInteractEntityEvent e) {
        String name = e.getRightClicked().getCustomName();
        System.out.println("yes!");
        if(name == null) return;
        name = ChatColor.stripColor(name);
        System.out.println("its run?");
        if(name.toLowerCase().contains("item shop")) {
            System.out.println("?");
            e.setCancelled(true);
            ItemShopGUI gui = new ItemShopGUI(gameManager, e.getPlayer(), ShopCategory.QUICK_BUY);
            gameManager.getGuiManager().setGUI(e.getPlayer(), gui);
        }else if(name.toLowerCase().contains("island upgrades")){
            IslandUpgradeGUI gui = new IslandUpgradeGUI();
            gameManager.getGuiManager().setGUI(e.getPlayer(), gui);
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteractWithItem(PlayerInteractEvent e) {
        if(!e.hasItem()) return;
        if(e.getItem() == null) return;
        if(!e.getItem().hasItemMeta()) return;
        if(e.getItem().getItemMeta().getDisplayName() == null) return;

        Player player = e.getPlayer();

        String itemName = e.getItem().getItemMeta().getDisplayName();
        itemName = ChatColor.stripColor(itemName);

        if(itemName.toLowerCase().contains("select team") &&
                gameManager.getState() == GameState.PRELOBBY ||
                gameManager.getState() == GameState.LOBBY ||
                gameManager.getState() == GameState.STARTING) {
            TeamPickerGUI gui = new TeamPickerGUI(gameManager, player);
            gameManager.getGuiManager().setGUI(player, gui);

            e.setCancelled(true);
        }
    }

    public PlayerItemInteractListener(GameManager gameManager) {
        this.gameManager = gameManager;
    }
    @EventHandler
    public void onInteractWithSetupWizardItem(PlayerInteractEvent e) {
        if(!e.hasItem()) return;
        if(!gameManager.getSetupWizardManager().isInWizard(e.getPlayer())) return;
        if(e.getItem() == null) return;
        if(!e.getItem().hasItemMeta()) return;

        Player player = e.getPlayer();

        String itemName = e.getItem().getItemMeta().getDisplayName();
        itemName = ChatColor.stripColor(itemName);
        Island island  = gameManager.getSetupWizardManager().getIsland(player);

        Location current = player.getLocation();
        Location clicked = null;
        if(e.getClickedBlock() != null) {
            clicked = e.getClickedBlock().getLocation();
        }

        e.setCancelled(true);

        switch(itemName.toLowerCase()) {
            case "set diamond generator":
                Generator diamondGenerator = new Generator(current, GeneratorType.DIAMOND, false);
                gameManager.getConfigurationManager().saveUnownedGenerator(player.getWorld().getName(), diamondGenerator);
                player.sendMessage("Set diamond generator");
                break;
            case "set emerald generator":
                Generator emeraldGenerator = new Generator(current, GeneratorType.EMERALD, false);
                gameManager.getConfigurationManager().saveUnownedGenerator(player.getWorld().getName(), emeraldGenerator);
                player.sendMessage("Set emerald generator");
                break;
            case "change island":
                SetupWizardIslandSelectorGUI gui = new SetupWizardIslandSelectorGUI(gameManager);
                gameManager.getGuiManager().setGUI(player, gui);
                break;
            case "corner stick":
                if(clicked != null) {
                    island.setProtectedCornerOne(clicked);
                }
                break;
            case "second corner stick":
                if(clicked != null) {
                    island.setProtectedCornerTwo(clicked);
                }
                break;
            case "set shop location":
                island.setShopEntityLocation(current);
                break;
            case "set generator location":
                Generator islandGenerator = new Generator(current, GeneratorType.IRON, true);
                island.addGenerator(islandGenerator);
                Generator islandGenerator1 = new Generator(current, GeneratorType.GOLD, true);
                island.addGenerator(islandGenerator1);
                Generator islandGenerator2 = new Generator(current, GeneratorType.EMERALD, true);
                island.addGenerator(islandGenerator2);
                break;
            case "set team upgrade location":
                island.setUpgradeEntityLocation(current);
                break;
            case "set spawn location":
                island.setSpawnLocation(current);
                break;
            case "set bed location":
                if(clicked != null) {
                    island.setBedLocation(clicked);
                }
                break;
            case "set lobby spawn":
                GameWorld gameWorld = gameManager.getSetupWizardManager().getWorld(player);
                gameWorld.setLobbyPosition(current);
                gameManager.getConfigurationManager().saveWorld(gameWorld);
                break;
            case "save island":
                gameManager.getConfigurationManager().saveIsland(island);
                Bukkit.getServer().getScheduler().runTaskLater(gameManager.getPlugin(), () -> gameManager.getSetupWizardManager().worldSetupWizard(player, island.getGameWorld()), 4);
                break;
        }
    }
}
