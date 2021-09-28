package me.gamendecat.hypixelbedwars.games.bedwars.tasks;

import me.gamendecat.hypixelbedwars.games.bedwars.manager.GameManager;
import me.gamendecat.hypixelbedwars.games.bedwars.utility.Colorize;
import me.gamendecat.hypixelbedwars.games.bedwars.worlds.Island;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PlayerRespawnTask implements Runnable {

    private Player player;
    private Island playerIsland;
    private GameManager gameManager;
    private String titleText;
    private String subTitleText;
    private PacketPlayOutTitle title;
    private PacketPlayOutTitle subTitle;
    private PacketPlayOutTitle length;

    public PlayerRespawnTask(Player player, GameManager gameManager, Island playerIsland) {
        this.player = player;
        this.gameManager = gameManager;
        this.playerIsland = playerIsland;

        playerIsland.addToAbsolutelyAlive(player);

        length = new PacketPlayOutTitle(10, 70, 20); // <fadeIn>, <duration>, <fadeOut>
    }

    private int tick = 0;

    @Override
    public void run() {
        System.out.println("tick: " + tick);

        if(tick == 5) {
            System.out.println("yeah!!!");
            titleText = Colorize.color("&aRespawned");
            subTitleText = " ";
            title = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + titleText + "\"}"));
            subTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + subTitleText + "\"}"));
            playerIsland.removeFromAbsolutelyAlive(player);
            player.setGameMode(GameMode.SURVIVAL);
            player.teleport(playerIsland.getSpawnLocation());
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(title);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(subTitle);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(length);
            return;
        }

        subTitleText = Colorize.color("&aRespawning in " + (5 - tick) + "...");
        titleText = Colorize.color("&cYOU DIED");

        title = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + titleText + "\"}"));
        subTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + subTitleText + "\"}"));

        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(title);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(subTitle);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(length);


        tick++;
    }
}
