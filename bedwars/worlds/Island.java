package me.gamendecat.hypixelbedwars.games.bedwars.worlds;

import de.tr7zw.nbtapi.NBTEntity;
import me.gamendecat.hypixelbedwars.games.bedwars.manager.GameManager;
import me.gamendecat.hypixelbedwars.games.bedwars.utility.Colorize;
import me.gamendecat.hypixelbedwars.games.bedwars.utility.ItemBuilder;
import me.gamendecat.hypixelbedwars.games.bedwars.worlds.generators.Generator;
import me.gamendecat.hypixelbedwars.games.bedwars.worlds.generators.GeneratorTier;
import me.gamendecat.hypixelbedwars.games.bedwars.worlds.generators.GeneratorType;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class Island {

    private GameWorld gameWorld;
    private IslandColor color;

    private List<UUID> players = new ArrayList<>();
    private List<UUID> absolutelyAlive = new ArrayList<>();

    private Location protectedCornerOne = null;
    private Location protectedCornerTwo = null;

    private Location bedLocation = null;
    private Location upgradeEntityLocation = null;
    private Location shopEntityLocation = null;
    private HashMap<IslandUpgrade, Integer> upgradeStatusMap = new HashMap<>();

    private Location spawnLocation = null;

    private List<Generator> generatorList = new ArrayList<>();

    public Island(GameWorld gameWorld, IslandColor teamColor) {
        this.gameWorld = gameWorld;
        this.color = teamColor;
        this.upgradeStatusMap.put(IslandUpgrade.SHARP_SWORDS, 0);
        this.upgradeStatusMap.put(IslandUpgrade.PROTECTION, 0);
        this.upgradeStatusMap.put(IslandUpgrade.FASTER_FORGE, 0);
        this.upgradeStatusMap.put(IslandUpgrade.TRAPS, 0);
        this.upgradeStatusMap.put(IslandUpgrade.HASTE, 0);
    }

    public void setGameWorld(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
    }

    public Location getProtectedCornerOne() {
        return protectedCornerOne;
    }

    public void setProtectedCornerOne(Location protectedCornerOne) {
        this.protectedCornerOne = protectedCornerOne;
    }

    public Location getProtectedCornerTwo() {
        return protectedCornerTwo;
    }

    public void setProtectedCornerTwo(Location protectedCornerTwo) {
        this.protectedCornerTwo = protectedCornerTwo;
    }

    public void setUpgradeEntityLocation(Location upgradeEntityLocation) {
        this.upgradeEntityLocation = upgradeEntityLocation;
    }

    public Location getShopEntityLocation() {
        return shopEntityLocation;
    }

    public void setShopEntityLocation(Location shopEntityLocation) {
        this.shopEntityLocation = shopEntityLocation;
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public void setSpawnLocation(Location spawnLocation) {
        this.spawnLocation = spawnLocation;
    }

    public List<Generator> getGenerators() {
        return generatorList;
    }

    public void setGenerators(List<Generator> generators) {
        this.generatorList = generators;
        for(Generator generator : generators) {
            if(generator.getType() != GeneratorType.EMERALD) {
                generator.setActivated(true);
            }
        }
    }

    public void activateEmeraldGenerators() {
        for(Generator generator : getGenerators()) {
            if(generator.getType() == GeneratorType.EMERALD) {
                generator.setActivated(true);
            }
        }
    }

    public Location getBedLocation() {
        return bedLocation;
    }

    public void setBedLocation(Location bedLocation) {
        this.bedLocation = bedLocation;
    }

    public GameWorld getGameWorld() {
        return gameWorld;
    }

    public void addGenerator(Generator generator) {
        this.generatorList.add(generator);
    }

    public boolean isBlockWithingProtectedZone(Block block) {
        Location blockLocation = block.getLocation();

        List<Location> protectedRange = blocksFromTwoPoints(protectedCornerOne, protectedCornerTwo);

        return protectedRange.contains(blockLocation);
    }

    private List<Location> blocksFromTwoPoints(Location loc1, Location loc2) {
        List<Location> blocks = new ArrayList<>();

        int topBlockX = (Math.max(loc1.getBlockX(), loc2.getBlockX()));
        int bottemBlockX = (Math.min(loc1.getBlockX(), loc2.getBlockX()));

        int topBlockY = (Math.max(loc1.getBlockY(), loc2.getBlockY()));
        int bottemBlockY = (Math.min(loc1.getBlockY(), loc2.getBlockY()));

        int topBlockZ = (Math.max(loc1.getBlockZ(), loc2.getBlockZ()));
        int bottemBlockZ = (Math.min(loc1.getBlockZ(), loc2.getBlockZ()));

        for(int x = bottemBlockX; x <= topBlockX; x++) {
            for(int z = bottemBlockZ; z <= topBlockZ; z++) {
                for (int y = bottemBlockY; y <= topBlockY; y++) {
                    Block block = loc1.getWorld().getBlockAt(x, y, z);
                    blocks.add(block.getLocation());
                }
            }
        }

        return blocks;
    }

    public Location getUpgradeEntityLocation() {
        return upgradeEntityLocation;
    }

    public IslandColor getColor() {
        return color;
    }

    public int alivePlayerCount() {
        if(isBedPlaced()) {
            return players.size();
        }

        List<UUID> alive = players.stream().filter(player -> Bukkit.getPlayer(player).getGameMode() != GameMode.SPECTATOR).collect(Collectors.toList());

        int count = alive.size();

        for(UUID absolutelyAlivePlayer : absolutelyAlive) {
            if(alive.stream().noneMatch(player -> player == absolutelyAlivePlayer)) {
                OfflinePlayer player = Bukkit.getOfflinePlayer(absolutelyAlivePlayer);
                if(player.isOnline()) {
                    count += 1;
                }else {
                    absolutelyAlive.remove(absolutelyAlivePlayer);
                }
            }
        }
        return count;
    }

    public void addMember(UUID player) {
        this.players.add(player);
    }

    public void removeMember(UUID player) {
        this.players.remove(player);
    }

    public List<UUID> getPlayers() {
        return players;
    }

    public boolean isMember(Player player) {
        return players.contains(player.getUniqueId());
    }

    public boolean isBedPlaced() {
        Location bedLocation = getBedLocation();

        if(bedLocation.getBlock().getType().name().contains("BED")) {
            return true;
        }

        Location oneExtraZ = bedLocation.clone().add(0, 0, 1);
        if(oneExtraZ.getBlock().getType().name().contains("BED")) {
            return true;
        }

        Location oneLessZ = bedLocation.clone().add(0, 0, -1);
        if(oneLessZ.getBlock().getType().name().contains("BED")) {
            return true;
        }
        Location oneExtraX = bedLocation.clone().add(1, 0, 0);
        if(oneExtraX.getBlock().getType().name().contains("BED")) {
            return true;
        }
        Location oneLessX = bedLocation.clone().add(-1, 0, 0);
        if(oneLessX.getBlock().getType().name().contains("BED")) {
            return true;
        }

        return false;
    }

    public void addToAbsolutelyAlive(Player player) {
        absolutelyAlive.add(player.getUniqueId());
    }

    public void removeFromAbsolutelyAlive(Player player) {
        absolutelyAlive.remove(player.getUniqueId());
    }

    public void spawnShops() {
        Villager itemShopEntity = shopEntityLocation.getWorld().spawn(shopEntityLocation, Villager.class);
        itemShopEntity.setProfession(Villager.Profession.BLACKSMITH);
        itemShopEntity.setCustomNameVisible(true);
        itemShopEntity.setCustomName(Colorize.color("&eItem Shop"));
        new NBTEntity(itemShopEntity).setInteger("NoAI", 1);

        Skeleton upgrades = upgradeEntityLocation.getWorld().spawn(upgradeEntityLocation, Skeleton.class);
        upgrades.setCustomNameVisible(true);
        upgrades.setCustomName(Colorize.color("&bIsland Upgrades"));
        new NBTEntity(upgrades).setInteger("NoAI", 1);
    }

    public void upgrade(IslandUpgrade upgrade, Player player, GameManager gameManager) {
        upgradeStatusMap.put(upgrade, upgradeStatusMap.getOrDefault(upgrade, 0) + 1);
        int newLevel = upgradeStatusMap.get(upgrade);
        for(UUID memberUUID : players) {
            Player member = Bukkit.getPlayer(memberUUID);
            if(member == null)  {
                continue;
            }
            member.sendMessage(Colorize.color("&6" + player.getDisplayName() + " upgraded  " + upgrade.formattedName()));
            gameManager.getPlayerManager().giveTeamArmor(this, player);

            if(upgrade == IslandUpgrade.SHARP_SWORDS) {
                for(ItemStack itemStack : member.getInventory()) {
                    if(itemStack != null && itemStack.getType().name().toLowerCase().contains("sword")) {
                        itemStack.addEnchantment(Enchantment.DAMAGE_ALL, 1);
                    }
                }
            }else if(upgrade == IslandUpgrade.HASTE) {
                int level = getLevelForUpgrade(upgrade);
                member.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, level));
            }else if(upgrade == IslandUpgrade.PROTECTION) {
                int level = getLevelForUpgrade(upgrade);
                PlayerInventory inv = member.getInventory();
                if(inv.getBoots() == null ||
                        inv.getLeggings() == null ||
                        inv.getChestplate() == null ||
                        inv.getHelmet() == null) return;
                inv.setBoots(new ItemBuilder(new ItemStack(inv.getBoots().getType()))
                                .addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, level)
                                .toItemStack());
                inv.setLeggings(new ItemBuilder(new ItemStack(inv.getLeggings().getType()))
                        .addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, level)
                        .toItemStack());
                inv.setChestplate(new ItemBuilder(new ItemStack(inv.getChestplate().getType()))
                        .addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, level)
                        .toItemStack());
                inv.setHelmet(new ItemBuilder(new ItemStack(inv.getHelmet().getType()))
                        .addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, level)
                        .toItemStack());
            }
        }

        if(upgrade == IslandUpgrade.FASTER_FORGE) {
            int level = getLevelForUpgrade(upgrade);
            generatorList.forEach(generator -> {
                if(level == 1) {
                    generator.setType(GeneratorTier.ONE);
                }else if(level == 2) {
                    generator.setType(GeneratorTier.TWO);
                }else if(level == 3) {
                    generator.setType(GeneratorTier.THREE);
                }
            });
        }
    }

    public int getLevelForUpgrade(IslandUpgrade islandUpgrade) {
        return upgradeStatusMap.getOrDefault(islandUpgrade, 0);
    }
}
