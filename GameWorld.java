package me.gamendecat.hypixelbedwars.games.bedwars.worlds;

import me.gamendecat.hypixelbedwars.games.bedwars.manager.GameManager;
import me.gamendecat.hypixelbedwars.games.bedwars.manager.GameState;
import me.gamendecat.hypixelbedwars.games.bedwars.utility.Colorize;
import me.gamendecat.hypixelbedwars.games.bedwars.worlds.generators.Generator;
import me.gamendecat.hypixelbedwars.games.bedwars.worlds.generators.GeneratorTier;
import me.gamendecat.hypixelbedwars.games.bedwars.worlds.generators.GeneratorType;
import net.minecraft.server.v1_8_R3.RegionFileCache;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.lang.String;

public class GameWorld {

    private String name;
    private World world;
    private int maxTeamSize = 1;
    private GameManager gameManager;

    private File destinationWorldFolder;

    private Location lobbyPosition;

    private GeneratorTier diamondTier = GeneratorTier.ONE;
    private GeneratorTier emeraldTier = GeneratorTier.ONE;

    private List<Island> islands = new ArrayList<>();
    private List<Generator> generators = new ArrayList<>();

    public GameWorld(String name) {
        this.name = name;
    }

    public void loadWorld(GameManager gameManager, boolean loadIntoPlaying, Runnable runnable) {
        this.gameManager = gameManager;
        File sourceWorldFolder = null;
        try {
            sourceWorldFolder = new File( gameManager.getPlugin().getDataFolder().getCanonicalPath()
                    + File.separator + ".." + File.separator + ".." + File.separator +  name);
        } catch (IOException e) {
            e.printStackTrace();
        }

        destinationWorldFolder = new File(sourceWorldFolder.getPath() + (loadIntoPlaying ? "_playing" : ""));

        System.out.println("SourceWorld: " + sourceWorldFolder.getName());
        System.out.println("destinationWorldFolder: " + destinationWorldFolder.getName());

        copyFolder(sourceWorldFolder, destinationWorldFolder);

        WorldCreator creator = new WorldCreator(name + (loadIntoPlaying ? "_playing" : ""));
        world = creator.createWorld();
        world.setAutoSave(false);
        world.setGameRuleValue("keepInventory", "true");
        world.setGameRuleValue("doFireTick", "false");
        world.setGameRuleValue("mobGriefing", "false");
        world.setDifficulty(Difficulty.NORMAL);

        runnable.run();
    }// 2:53:26

    private static void copyFolder(File source, File target){
        try {
            ArrayList<String> ignore = new ArrayList<>(Arrays.asList("uid.dat", "session.lock"));
            if(!ignore.contains(source.getName())) {
                if(source.isDirectory()) {
                    if(!target.exists())
                        if (!target.mkdirs())
                            throw new IOException("Couldn't create world directory!");
                    String files[] = source.list();
                    for (String file : files) {
                        File srcFile = new File(source, file);
                        File destFile = new File(target, file);
                        copyFolder(srcFile, destFile);
                    }
                } else {
                    InputStream in = new FileInputStream(source);
                    OutputStream out = new FileOutputStream(target);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = in.read(buffer)) > 0)
                        out.write(buffer, 0, length);
                    in.close();
                    out.close();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void resetWorld() {
        if(world == null) return;

        String worldName = world.getName();
        for(Chunk chunk : world.getLoadedChunks()) {
            chunk.unload();
        }
        Bukkit.unloadWorld(world, false);
        if(Bukkit.getWorld(world.getName()) != null) {
            System.out.println("Not unloaded! Wtf?");
        }

        RegionFileCache.a();

        if(deleteDir(destinationWorldFolder)) {
            System.out.println("Deleted: " + worldName);
        }else {
            System.out.println("Couldn't delete " + worldName);
        }

        gameManager.setState(GameState.PRELOBBY);
    }

    /*private boolean delete(File toDelete) {
        File[] allContents = toDelete.listFiles();

        if(allContents != null) {
            for (File file : allContents) {
                delete(file);
            }
        }

        return toDelete.delete();
    }
     */

    public String getConfigName() {
        return name;
    };

    public World getWorld() {
        return world;
    };

    public Location getLobbyPosition() {
        return lobbyPosition;
    }

    public List<Generator> generatorList() {
        return generators;
    }

    public void addIsland(Island island) {
        islands.add(island);
    }

    public Island getIslandForBed(Location location) {
        Optional<Island> islandOptional =  islands.stream().filter((island) -> {
            if(island.getBedLocation().equals(location)) {
                return true;
            }

            Location oneExtraZ = location.clone().add(0, 0, 1);
            Location oneLessZ = location.clone().add(0, 0, -1);
            Location oneExtraX = location.clone().add(1, 0, 0);
            Location oneLessX = location.clone().add(-1, 0, 0);
            Location[] check = new Location[] {
                    oneExtraZ, oneLessZ, oneExtraX, oneLessX
            };

            for(Location toCheck : check) {
                if(toCheck.equals(island.getBedLocation()) && toCheck.getBlock().getType().name().contains("BED")) {
                    return true;
                }
            }

            return false;
        }).findFirst();

        return islandOptional.orElse(null);
    }

    public List<Generator> getGenerators() {
        return generators;
    }

    public List<Island> getActiveIslands() {
        return islands.stream().filter(island -> (island.isBedPlaced() && island.alivePlayerCount() != 0) || (!island.isBedPlaced() && island.alivePlayerCount() != 0)).collect(Collectors.toList());
    }

    public List<Island> getIslands() {
        return islands;
    }

    public void setLobbyPosition(Location lobbyPosition) {
        this.lobbyPosition = lobbyPosition;
    }

    public Island islandForPlayer(Player player) {
        return islands.stream().filter(island -> island.isMember(player)).findFirst().orElse(null);
    }

    public int getMaxTeamSize() {
        return maxTeamSize;
    }

    public void tick(int currentSecond) {
        double i = secondsToMinutes(currentSecond);
        if(i == 5.0) {
            Bukkit.broadcastMessage(Colorize.color("&bDiamond generators have been upgraded to Tier II"));
            diamondTier = GeneratorTier.TWO;
            for(Generator generator : getGenerators()) {
                if(generator.getType() == GeneratorType.DIAMOND) {
                    generator.getArmorStandone().setCustomNameVisible(true);
                    generator.getArmorStandone().setCustomName(Colorize.color("&eTier &cII"));
                }
            }
        }else if(i == 10.0) {
            Bukkit.broadcastMessage(Colorize.color("&aEmerald Generators have been upgraded to Tier II"));
            emeraldTier = GeneratorTier.TWO;
            for(Generator generator : getGenerators()) {
                if(generator.getType() == GeneratorType.EMERALD) {
                    generator.getArmorStandone().setCustomNameVisible(true);
                    generator.getArmorStandone().setCustomName(Colorize.color("&eTier &cII"));
                }
            }
        }else if(i == 15.0) {
            Bukkit.broadcastMessage(Colorize.color("&bDiamond generators have been upgraded to Tier III"));
            diamondTier = GeneratorTier.THREE;
            for(Generator generator : getGenerators()) {
                if(generator.getType() == GeneratorType.DIAMOND) {
                    generator.getArmorStandone().setCustomNameVisible(true);
                    generator.getArmorStandone().setCustomName(Colorize.color("&eTier &cIII"));
                }
            }
        }else if(i == 20.0) {
            Bukkit.broadcastMessage(Colorize.color("&aEmerald generators have been upgraded to Tier III"));
            emeraldTier = GeneratorTier.THREE; // 1:34:54
            for(Generator generator : getGenerators()) {
                if(generator.getType() == GeneratorType.EMERALD) {
                    generator.getArmorStandone().setCustomNameVisible(true);
                    generator.getArmorStandone().setCustomName(Colorize.color("&eTier &cIII"));
                }
            }
        }

        for(Island island : getIslands()) {
            for(Generator generator : island.getGenerators()) {
                generator.spawn();
            }
        }

        for(Generator generator : getGenerators()) {
            if(generator.getType() == GeneratorType.DIAMOND) {
                generator.setType(diamondTier);
            }else if(generator.getType() == GeneratorType.EMERALD) {
                generator.setType(emeraldTier);
            }
            generator.setActivated(true);
            generator.spawn();
        }
    }

    public void setGenerators(List<Generator> generators) {
        this.generators = generators;
    }

    private double secondsToMinutes(int seconds) {
        return seconds / 60.0;
    }

    public String nextTierString(int currentSecond) {
        double i = secondsToMinutes(currentSecond);
        String line = " ";
        if(i < 5.0) {
            line = getMinsSec(currentSecond);
        }else if(i == 10.0) {
            line = getMinsSec((currentSecond - 300));
        }else if(i == 15.0) {
            line = getMinsSec((currentSecond - 600));
        }else if(i == 20.0) {
            line = getMinsSec((currentSecond - 900));
        }
        return line;
    }

    public String getDiaOrEmerald(int currentSecond) {
        double i = secondsToMinutes(currentSecond);
        if(i < 5.0) {
            return "Diamond II in:";
        }else if(i == 10.0) {
            return "Emerald II in:";
        }else if(i == 15.0) {
            return "Diamond III in:";
        }else if(i == 20.0) {
            return "Emerald III in:";
        }
        return "Error";
    }

    public String getMinsSec(int seconds) {
        int fiveMin = 300;
        int mins = seconds / 60;
        int remainder = seconds - mins * 60;
        int secs = remainder;
        return "&f" + mins + ":" + secs;
    }

    public boolean deleteWorld(File file) {
        if (file.isDirectory()) {
            String[] string = file.list();
            for (int i = 0; i < string.length; i++) {
                File fileDebug = new File(file, string[i]);
                deleteWorld(fileDebug);
                System.out.println("Deleted: " + fileDebug.getName());
            }
        }

        return file.delete();
    }

    public boolean deleteDir(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                deleteDir(f);
            }
        }
        return file.delete();
    }
}
