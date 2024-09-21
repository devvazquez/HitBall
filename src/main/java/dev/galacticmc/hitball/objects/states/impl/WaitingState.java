package dev.galacticmc.hitball.objects.states.impl;

import dev.galacticmc.hitball.HitBallPlugin;
import dev.galacticmc.hitball.objects.LangKey;
import dev.galacticmc.hitball.objects.states.GameState;
import dev.galacticmc.hitball.objects.states.StateManager;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class WaitingState implements GameState {

    private StateManager stateManager;
    private World world;

    /*
        El mayor bug de la historia es que un BukkitRunnable no se puede cancelar desde dentro?
    */

    @Override
    public void onEnable(HitBallPlugin plugin, StateManager stateManager) {
        this.stateManager = stateManager;
        this.world = stateManager.getMiniGameWorld();

        BukkitRunnable waitForPlayers = BukkitRunnableProvider.manageWith(stateManager).waitingState();
        waitForPlayers.runTaskTimer(plugin, 0L, 20L);

    }

    @Override
    public void onDisable() {

    }

    // - EVENTS START -

    @Override
    public void playerJoin(Player player) {
        UUID playerUUID = player.getUniqueId();
        stateManager.addPlayerToSpawns(playerUUID, (location) -> {
            // Optionally, teleport the player to the spawn location
            player.teleport(location);
            world.sendMessage(LangKey.PLAYER_JOIN.formatted("player_name", player.getName()));
            player.setWalkSpeed(0);
        }, () -> {
            // Kick the player if no available seat is found
            player.performCommand("spawn");
            player.sendMessage(LangKey.GAME_FULL.formatted());
        });
    }

    @Override
    public void playerLeave(Player player) {
        stateManager.removePlayerFromSpawns(player.getUniqueId());

        world.sendMessage(LangKey.PLAYER_LEAVE.formatted("player_name", player.getName()));
        player.setWalkSpeed(0.2f);
    }

    @Override
    public void playerMove(PlayerMoveEvent e) {
        if(e.getPlayer().getGameMode().equals(GameMode.CREATIVE)) return;
        Location f = e.getFrom();
        Location t = e.getTo();
        if(f.getX() != t.getX() || f.getY() != t.getY()){
            e.setCancelled(true);
        }
    }

    @Override
    public void playerInteract(PlayerInteractEvent event) {
        // No hacer nada porque no se procesa ninguna interacción con entidades.
    }


    // - EVENTS END -
}
