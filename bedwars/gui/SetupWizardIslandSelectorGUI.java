package me.gamendecat.hypixelbedwars.games.bedwars.gui;

import me.gamendecat.hypixelbedwars.games.bedwars.utility.ItemBuilder;
import me.gamendecat.hypixelbedwars.games.bedwars.manager.GameManager;
import me.gamendecat.hypixelbedwars.games.bedwars.worlds.IslandColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class SetupWizardIslandSelectorGUI implements GUI{

    private Inventory inventory;

    private GameManager gameManager;

    public SetupWizardIslandSelectorGUI(GameManager gameManager) {
        inventory = Bukkit.createInventory(null, 27, getName());

        this.gameManager = gameManager;

        for(IslandColor color : IslandColor.values()) {
            inventory.addItem(
                    new ItemBuilder(color.woolMaterial().toItemStack()).setName(color.getChatColor() + color.formattedName()).toItemStack()
            );
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
        if(!gameManager.getSetupWizardManager().isInWizard(player)) {
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
        if(clickedColor != null) {
            gameManager.getSetupWizardManager().teamSetupWizard(player, clickedColor);
        } else {
            gameManager.getSetupWizardManager().worldSetupWizard(player, gameManager.getSetupWizardManager().getWorld(player));
        }

        return null;
    }

    @Override
    public boolean isInventory(InventoryView view) {
        return view.getTitle().equals(getName());
    }
}
