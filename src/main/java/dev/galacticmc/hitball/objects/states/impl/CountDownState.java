package dev.galacticmc.hitball.objects.states.impl;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import dev.galacticmc.hitball.HitBallPlugin;
import dev.galacticmc.hitball.objects.LangKey;
import dev.galacticmc.hitball.objects.SpawnLocations;
import dev.galacticmc.hitball.objects.states.GameState;
import dev.galacticmc.hitball.objects.states.StateManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class CountDownState implements GameState {

    private HitBallPlugin plugin;
    private StateManager stateManager;
    private World world;

    @Override
    public void onEnable(HitBallPlugin plugin, StateManager stateManager) {

        this.plugin = plugin;
        this.stateManager = stateManager;
        SpawnLocations spawnLocations = stateManager.getSpawnLocations();
        this.world = stateManager.getMiniGameWorld();

        //Iniciar el juego
        //Ultimos tres segundos
        //Primeros 7 segundos.
        BukkitRunnable countDown = BukkitRunnableProvider.manageWith(stateManager).countDownState();
        countDown.runTaskTimer(plugin, 0L, 20L);
    }

    @Override
    public void onDisable() {
        stateManager.getPlayers().forEach(player -> {
            player.getSelf().setWalkSpeed(0.2f);
        });
    }

    @Override
    public void playerJoin(Player player) {
        // Kick the player if no available seat is found
        player.performCommand("spawn");
        player.sendMessage(LangKey.GAME_FULL.formatted());
    }

    @Override
    public void playerLeave(Player player) {
        //Can't leave the game
        world.sendMessage(LangKey.RESTORE_COUNTDOWN.formatted());
        stateManager.nextGameState(new WaitingState());
        player.setWalkSpeed(0.2f);
    }

    @Override
    public void playerMove(PlayerMoveEvent event) {
        if(event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) return;
        event.setCancelled(true);
    }

    @Override
    public void playerInteract(PlayerInteractEvent event) {

    }

    @Override
    public void fallingBlockDie(EntityRemoveFromWorldEvent event) {

    }
}
