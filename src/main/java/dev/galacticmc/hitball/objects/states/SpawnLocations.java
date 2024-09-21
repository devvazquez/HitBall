package dev.galacticmc.hitball.objects.states;

import org.bukkit.Location;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @param playersSpawns Thread safe hashmap.
 */
public record SpawnLocations(Location spawnBallLocation, ConcurrentHashMap<Location, UUID> playersSpawns) {

}
