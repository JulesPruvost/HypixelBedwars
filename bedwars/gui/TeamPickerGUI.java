package me.gamendecat.hypixelbedwars.games.bedwars.gui;

import dev.jcsoftware.jscoreboards.JScoreboardTeam;
import me.gamendecat.hypixelbedwars.games.bedwars.manager.GameManager;
import me.gamendecat.hypixelbedwars.games.bedwars.utility.Colorize;
import me.gamendecat.hypixelbedwars.games.bedwars.utility.ItemBuilder;
import me.gamendecat.hypixelbedwars.games.bedwars.worlds.Island;
import me.gamendecat.hypixelbedwars.games.bedwars.worlds.IslandColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class TeamPickerGUI implements GUI{

    private final Inventory inventory;
    private final GameManager gameManager;

    public TeamPickerGUI(GameManager gameManager, Player player) {
        inventory = Bukkit.createInventory(null, 27, getName());

        this.gameManager = gameManager;

        for(Island island : gameManager.getGameWorld().getIslands()) {
            ItemBuilder itemBuilder = new ItemBuilder(island.getColor().woolMaterial().toItemStack().getType()
                    , 1)
                    .setName(island.getColor().getChatColor() + island.getColor().formattedName())
                    .addLoreLine(island.isMember(player) ? "&aSelected" : "&cNot Selected")
                    .addLoreLine("&a" + island.getPlayers().size() + "/" + gameManager.getGameWorld().getMaxTeamSize() + "players");

            if(island.isMember(player)) {
                itemBuilder.addUnsafeEnchantment(Enchantment.THORNS, 1);
            }

            inventory.addItem(itemBuilder.toItemStack());
        }

        inventory.addItem(
                new ItemBuilder(Material.BARRIER).setName("&aExit").toItemStack()
        );
    }
    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public String getName() {
        return "§aSelect Island";
    }

    @Override
    public GUI handleClick(Player player, ItemStack itemstack, InventoryView view) {
        if(itemstack.getType() == Material.BARRIER) {
            return null;
        }
        IslandColor clickedColor = null;

        String itemName = ChatColor.stripColor(itemstack.getItemMeta().getDisplayName());
        for(IslandColor color : IslandColor.values()) {
            if(itemName.equalsIgnoreCase(color.formattedName())) {
                clickedColor = color;
                break;
            }
        }

        Optional<Island> playerIsland = gameManager.getGameWorld().getIslands().stream().filter(island -> island.isMember(player)).findFirst();
        playerIsland.ifPresent(island -> {
            island.removeMember(player.getUniqueId());
            JScoreboardTeam team = gameManager.getScoreboard().findTeam(island.getColor().formattedName()).get();
            if(team != null) {
                team.removePlayer(player);
            }
            gameManager.getScoreboard().updateScoreboard();
            });

        IslandColor finalClickedColor = clickedColor;
        Optional<Island> selectedIsland = gameManager.getGameWorld().getIslands().stream().filter(island -> island.getColor() == finalClickedColor).findFirst();

        if(selectedIsland.isPresent()) {
            Island island = selectedIsland.get();
            if(island.getPlayers().size() != gameManager.getGameWorld().getMaxTeamSize()) {
                island.addMember(player.getUniqueId());
                JScoreboardTeam team = gameManager.getScoreboard().findTeam(island.getColor().formattedName()).get();
                if(team != null) {
                    team.addPlayer(player);
                }
                gameManager.getScoreboard().updateScoreboard();
            }else {
                player.sendMessage(Colorize.color("&cThat island is full!"));
            }
        }

        gameManager.getPlayerManager().giveTeamSelector(player);
        return null;
    }

    @Override
    public boolean isInventory(InventoryView view) {
        return view.getTitle().equals(getName());
    }


}

