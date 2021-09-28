package me.gamendecat.hypixelbedwars.games.bedwars.config;

import io.netty.util.internal.ThreadLocalRandom;
import me.gamendecat.hypixelbedwars.games.bedwars.manager.GameManager;
import me.gamendecat.hypixelbedwars.games.bedwars.worlds.IslandColor;
import me.gamendecat.hypixelbedwars.games.bedwars.worlds.GameWorld;
import me.gamendecat.hypixelbedwars.games.bedwars.worlds.Island;
import me.gamendecat.hypixelbedwars.games.bedwars.worlds.generators.Generator;
import me.gamendecat.hypixelbedwars.games.bedwars.worlds.generators.GeneratorType;
import org.apache.commons.lang3.EnumUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ConfigurationManager {

    private GameManager gameManager;

    private ConfigurationSection mapsConfiguration;

    public ConfigurationManager(GameManager gameManager) {
        this.gameManager = gameManager;
        FileConfiguration fileConfiguration = gameManager.getPlugin().getConfig();

        if(!fileConfiguration.isConfigurationSection("maps")) {
            mapsConfiguration = fileConfiguration.createSection("maps");
        } else {
            mapsConfiguration = fileConfiguration.getConfigurationSection("maps");
        }

        gameManager.getPlugin().saveConfig();
    }

    public String randomMapName() {
        String[] mapNames = mapsConfiguration.getKeys(false).toArray(new String[]{});
        //return mapNames[ThreadLocalRandom.current().nextInt(mapNames.length)];
        //currently just testing.
        return "Lighthouse";
    }

    //loading

    public void loadWorld(String mapName, Consumer<GameWorld> consumer) {
        GameWorld gameWorld = new GameWorld(mapName);
        gameWorld.loadWorld(gameManager, true, () -> {
            ConfigurationSection mapSection = getMapSection(mapName);
            for(String key : mapSection.getKeys(false)) {
                //video 1 4:39:00
                if(EnumUtils.isValidEnum(IslandColor.class, key)) {
                    Island island = loadIsland(gameWorld, mapSection.getConfigurationSection(key));
                    gameWorld.addIsland(island);
                }
            }

            if(mapSection.isConfigurationSection("generators")) {
                ConfigurationSection mapGenerators = mapSection.getConfigurationSection("generators");
                gameWorld.setGenerators(loadGenerators(gameWorld, mapGenerators, false));
            }


            gameWorld.setLobbyPosition(locationFrom(gameWorld.getWorld(), mapSection.getConfigurationSection("lobbySpawn")));

            consumer.accept(gameWorld);
        });

    }

    public Island loadIsland(GameWorld world, ConfigurationSection section) {
        IslandColor color = IslandColor.valueOf(section.getName());
        Island island = new Island(world, color);

        try {
            island.setBedLocation(getSomething(world.getWorld(), section.getConfigurationSection("bed")));
            island.setSpawnLocation(locationFrom(world.getWorld(), section.getConfigurationSection("spawn")));
            island.setUpgradeEntityLocation(locationFrom(world.getWorld(), section.getConfigurationSection("upgradeEntity")));
            island.setShopEntityLocation(locationFrom(world.getWorld(), section.getConfigurationSection("shopEntity")));
            island.setProtectedCornerOne(getSomething(world.getWorld(), section.getConfigurationSection("protectedCornerOne")));
            island.setProtectedCornerTwo(getSomething(world.getWorld(), section.getConfigurationSection("protectedCornerTwo")));
            island.setGenerators(loadGenerators(world, section.getConfigurationSection("generators"), true));
        }catch(Exception ex) {
            Bukkit.getServer().getLogger().severe("Invalid " + color.formattedName() + "island in " + world.getConfigName());
            ex.printStackTrace();
        }

        return island;
    }

    public List<Generator> loadGenerators(GameWorld world, ConfigurationSection section, boolean forIsland) {
        List<Generator> generators = section.getKeys(false).stream().map((key) -> {
            ConfigurationSection generatorSection = section.getConfigurationSection(key);
            Location location = locationFrom(world.getWorld(), generatorSection.getConfigurationSection("location"));
            String typeString = generatorSection.getString("type");

            if(!EnumUtils.isValidEnum(GeneratorType.class, String.valueOf(typeString))) {
                return null;
            }

            GeneratorType type = GeneratorType.valueOf(typeString);
            return new Generator(location, type, forIsland);
        }).collect(Collectors.toList());

        return generators;
    }

    //saving

    public void saveWorld(GameWorld world) {
        ConfigurationSection mapSection = getMapSection(world.getConfigName());
        ConfigurationSection lobbySection;
        if(mapSection.isConfigurationSection("lobbySpawn")) {
            lobbySection = mapSection.getConfigurationSection("lobbySpawn");
        }else {
            lobbySection = mapSection.createSection("lobbySpawn");
        }
        writeLocation(world.getLobbyPosition(), lobbySection);

        gameManager.getPlugin().saveConfig();
    }

    public ConfigurationSection getMapSection(String mapName) {
        if(!mapsConfiguration.isConfigurationSection(mapName)) {
            mapsConfiguration.createSection(mapName);
        }

        return mapsConfiguration.getConfigurationSection(mapName);
    }

    public void saveUnownedGenerator(String worldConfigName, Generator generator) {
        ConfigurationSection mapSection = getMapSection(worldConfigName);
        ConfigurationSection generatorSection;
        if(mapSection.isConfigurationSection("generators")) {
            generatorSection = mapSection.getConfigurationSection("generators");
        }else{
            generatorSection = mapSection.createSection("generators");
        }

        ConfigurationSection section = generatorSection.createSection(String.valueOf(UUID.randomUUID().toString()));
        section.set("type", generator.getType());
        writeLocation(generator.getLocation(), section.createSection("location"));

        gameManager.getPlugin().saveConfig();
    }

    public void saveIsland(Island island) {
        ConfigurationSection mapSection = getMapSection(island.getGameWorld().getConfigName());

        ConfigurationSection colorSection;
        if(mapSection.isConfigurationSection(island.getColor().toString())) {
            colorSection = mapSection.getConfigurationSection(island.getColor().toString());
        }else{
            colorSection = mapSection.createSection(island.getColor().toString());
        }

        Map<String, Location> locationsToWrite = new HashMap<>();
        locationsToWrite.put("upgradeEntity", island.getUpgradeEntityLocation());
        locationsToWrite.put("bed", island.getBedLocation());
        locationsToWrite.put("shopEntity", island.getShopEntityLocation());
        locationsToWrite.put("spawn", island.getSpawnLocation());
        locationsToWrite.put("protectedCornerOne", island.getProtectedCornerOne());
        locationsToWrite.put("protectedCornerTwo", island.getProtectedCornerTwo());



        for(Map.Entry<String, Location> entry : locationsToWrite.entrySet()) {
            ConfigurationSection section;
            if(!colorSection.isConfigurationSection(entry.getKey())) {
                    section = colorSection.createSection(entry.getKey());
            }else {
                section = colorSection.getConfigurationSection(entry.getKey());
            }
            if(entry.getValue() != null) {
                writeLocation(entry.getValue(), section);
            }
        }

        colorSection.set("generators", null);
        ConfigurationSection generatorSection;
        if(colorSection.isConfigurationSection("generators")) {
            generatorSection = colorSection.getConfigurationSection("generators");
        }else {
            generatorSection = colorSection.createSection("generators");
        }


        for(Generator generator : island.getGenerators()) {
            ConfigurationSection section = generatorSection.createSection(String.valueOf(UUID.randomUUID().toString()));
            section.set("type", String.valueOf(generator.getType()));
            writeLocation(generator.getLocation(), section.createSection("location"));
        }
        gameManager.getPlugin().saveConfig();
    }

    public void writeLocation(Location location,ConfigurationSection section) {
        section.set("x", location.getX());
        section.set("y", location.getY());
        section.set("z", location.getZ());
        section.set("yaw", location.getYaw());
        section.set("pitch", location.getPitch());
    }

    public Location locationFrom(World world, ConfigurationSection section) {
        float yaw = (float) section.getDouble("yaw");
        float pitch = (float) section.getDouble("pitch");
        Location location = new Location(world, section.getInt("x"), section.getInt("y"), section.getInt("z"), yaw, pitch);
        return location;
    }

    public Location getSomething(World world, ConfigurationSection section) {
        Location location = new Location(world, section.getInt("x"), section.getInt("y"), section.getInt("z"));
        return location;
    }
}
