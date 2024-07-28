package dev.galacticmc.hitball.objects.states.impl;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import dev.galacticmc.hitball.HitBallPlugin;
import dev.galacticmc.hitball.objects.LangKey;
import dev.galacticmc.hitball.objects.SpawnLocations;
import dev.galacticmc.hitball.objects.states.GameState;
import dev.galacticmc.hitball.objects.states.StateManager;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class WaitingState implements GameState {

    private SpawnLocations spawnLocations;
    private World world;

    /*
        El mayor bug de la historia es que un BukkitRunnable no se puede cancelar desde dentro?
    */

    @Override
    public void onEnable(HitBallPlugin plugin, StateManager stateManager) {

        this.spawnLocations = stateManager.getSpawnLocations();
        this.world = stateManager.getMiniGameWorld();

        BukkitRunnable waitForPlayers = BukkitRunnableProvider.manageWith(stateManager).waitingState();
        waitForPlayers.runTaskTimer(plugin, 0L, 20L);

        //Can't walk
        world.getPlayers().forEach(player -> {
            player.setWalkSpeed(0.0f);
        });
    }

    @Override
    public void onDisable() {

    }

    // - EVENTS START -

    @Override
    public void playerJoin(Player player) {
        UUID playerUUID = player.getUniqueId();

        // Find an available spawn location
        Optional<Map.Entry<Location, UUID>> availableSeat = spawnLocations.getPlayersSpawns().entrySet()
                .stream()
                .filter(entry -> entry.getValue() == null)
                .findFirst();

        if (availableSeat.isPresent()) {
            // Assign the player's UUID to the available location
            spawnLocations.getPlayersSpawns().replace(availableSeat.get().getKey(), playerUUID);
            // Optionally, teleport the player to the spawn location
            player.teleport(availableSeat.get().getKey());
            world.sendMessage(LangKey.PLAYER_JOIN.formatted("player_name", player.getName()));
            player.setWalkSpeed(0);
        } else {
            // Kick the player if no available seat is found
            player.performCommand("spawn");
            player.sendMessage(LangKey.GAME_FULL.formatted());
        }
    }

    @Override
    public void playerLeave(Player player) {
        if (spawnLocations.getPlayersSpawns().containsValue(player.getUniqueId())) {
            Location key = spawnLocations.getPlayersSpawns().entrySet()
                    .stream()
                    .filter(entry -> player.getUniqueId().equals(entry.getValue()))
                    .map(Map.Entry::getKey)
                    .findFirst().get();
            spawnLocations.getPlayersSpawns().replace(key, null);
        }
        world.sendMessage(LangKey.PLAYER_LEAVE.formatted("player_name", player.getName()));
        player.setWalkSpeed(0.2f);
    }

    @Override
    public void playerMove(PlayerMoveEvent e) {
        if(e.getPlayer().getGameMode().equals(GameMode.CREATIVE)) return;
        e.setCancelled(true);
    }

    @Override
    public void playerInteract(PlayerInteractEvent event) {
        // No hacer nada porque no se procesa ninguna interacción con entidades.
    }

    @Override
    public void fallingBlockDie(EntityRemoveFromWorldEvent event) {
        //Do nothing
    }



    // - EVENTS END -
}
