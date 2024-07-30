package dev.galacticmc.hitball.objects.managers;

import dev.galacticmc.hitball.HitBallPlugin;
import dev.galacticmc.hitball.objects.states.SpawnLocations;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

public class ConfigManager {

    private HitBallPlugin plugin;

    public ConfigManager(HitBallPlugin hitBallPlugin) {
        this.plugin = hitBallPlugin;
    }

    public void setBallSpawnLocation(Location location, ConfigurationSection worldConfig) {
        List<Integer> locList = Arrays.asList(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        worldConfig.set("ball_spawn", locList);
        plugin.saveConfig();
    }

    public void addPlayerSpawnLocation(Location location, ConfigurationSection worldConfig) {
        List<Integer> locList = Arrays.asList(location.getBlockX(), location.getBlockY(), location.getBlockZ(), Math.round(location.getYaw()), Math.round(location.getPitch()));
        List<List<Integer>> positions = (List<List<Integer>>) worldConfig.getList("player_spawns", new ArrayList<>());
        positions.add(locList);
        worldConfig.set("player_spawns", positions);
        plugin.saveConfig();
    }

    public SpawnLocations deserializeSpawnLocations(World world, ConfigurationSection configurationSection) {

        //Obtain world name.
        String worldName = world.getName();
        List<Integer> ballSpawnLocList = configurationSection.getIntegerList("ball_spawn");
        if (ballSpawnLocList.size() != 3) {
            plugin.getLogger().severe(String.format("'ball_spawn', in %s, should only be 3 integers long but was: ", worldName) + ballSpawnLocList.size());
            return null;
        }
        Location ballSpawnLoc = new Location(world, ballSpawnLocList.get(0), ballSpawnLocList.get(1), ballSpawnLocList.get(2));

        List<List<Integer>> playerSpawns = (List<List<Integer>>) configurationSection.getList("player_spawns");
        if (playerSpawns == null) {
            plugin.getLogger().severe(String.format("'player_spawns', in %s, should be a list of locations but was: ", world.getName()) + playerSpawns);
            return null;
        }

        HashMap<Location, UUID> tempHashMap = new HashMap<>();
        for (List<Integer> location : playerSpawns) {
            if (location.size() != 5) {
                plugin.getLogger().severe(String.format("Location list: %s, in 'player_spawns' inside config.yml should only be 5 integers long but was: %s", Arrays.toString(location.toArray()), location.size()));
                return null;
            }
            Location tempLoc = new Location(world, location.get(0), location.get(1), location.get(2), location.get(3), location.get(4));
            tempLoc = tempLoc.toCenterLocation();
            tempLoc.setY(location.get(1));
            tempHashMap.put(tempLoc, null);
        }
        assert tempHashMap.isEmpty();
        return new SpawnLocations(ballSpawnLoc, tempHashMap);
    }

}
