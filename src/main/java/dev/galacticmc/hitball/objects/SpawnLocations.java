package dev.galacticmc.hitball.objects;

import org.bukkit.Location;

import java.util.HashMap;
import java.util.UUID;

public class SpawnLocations {

    private final Location spawnBallLocation;
    private final HashMap<Location, UUID> playersSpawns;

    public Location getSpawnBallLocation() {
        return spawnBallLocation;
    }
    public HashMap<Location, UUID> getPlayersSpawns() {
        return playersSpawns;
    }

    public SpawnLocations(Location spawnBallLocation, HashMap<Location, UUID> playersSpawns) {
        this.spawnBallLocation = spawnBallLocation;
        this.playersSpawns = playersSpawns;
    }

}
