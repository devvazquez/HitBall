package dev.galacticmc.hitball.objects.states;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import dev.galacticmc.hitball.HitBallPlugin;
import dev.galacticmc.hitball.objects.HitBallPlayer;
import dev.galacticmc.hitball.objects.states.impl.WaitingState;
import dev.galacticmc.hitball.util.Utils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class StateManager implements Listener {

    private final HitBallPlugin plugin;
    public ArrayList<HitBallPlayer> getPlayers(){
        return new ArrayList<>(plugin.getThreadSafeMethods().getPlayersInWorld(miniGameWorld));
    }
    public  ArrayList<HitBallPlayer> getPlayersFiltered(Predicate<HitBallPlayer> filter){
        return getPlayers().stream()
                .filter(filter)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private GameState currentGameState;
    public GameState getCurrentGameState() {
        return currentGameState;
    }

    private final World miniGameWorld;
    public World getMiniGameWorld() {
        return miniGameWorld;
    }

    private final SpawnLocations spawnLocations;
    public SpawnLocations getSpawnLocations() {
        return spawnLocations;
    }

    private final int minPlayers;
    public int getMinPlayers() {
        return minPlayers;
    }

    private final int maxPlayers;
    public int getMaxPlayers() {
        return maxPlayers;
    }

    public StateManager(HitBallPlugin plugin, World world, SpawnLocations spawnLocations, int minPlayers, int maxPlayers) {
        this.plugin = plugin;
        this.miniGameWorld = world;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.spawnLocations = spawnLocations;
    }

    public void start(){
        nextGameState(new WaitingState());
    }

    public void nextGameState(GameState state) {
        if (currentGameState != null) {
            plugin.getLogger().info("Next game state '%s' for world: %s".formatted(currentGameState.getClass().getCanonicalName(), miniGameWorld.getName()));
            HandlerList.unregisterAll(currentGameState);
            currentGameState.onDisable();
            currentGameState = null;
        }else {
            plugin.getLogger().info("First game state '%s' for world: %s".formatted(state.getClass().getCanonicalName(), miniGameWorld.getName()));
        }
        this.currentGameState = state;
        plugin.getServer().getPluginManager().registerEvents(state, plugin);
        state.onEnable(plugin, this);
    }

    public void addPlayerToSpawns(UUID playerUUID, Consumer<Location> ifSo, Runnable ifNot){
        // Find an available spawn location
        Optional<Map.Entry<Location, UUID>> availableSeat = spawnLocations.playersSpawns().entrySet()
                .stream()
                .filter(entry -> entry.getValue() == Utils.DUMMY_UUID)
                .findFirst();

        if (availableSeat.isPresent()) {
            // Assign the player's UUID to the available location
            spawnLocations.playersSpawns().replace(availableSeat.get().getKey(), playerUUID);
            ifSo.accept(availableSeat.get().getKey());
        } else {
            ifNot.run();
        }
    }

    public void removePlayerFromSpawns(UUID playerUUID){
        if (spawnLocations.playersSpawns().containsValue(playerUUID)) {
            Location key = spawnLocations.playersSpawns().entrySet()
                    .stream()
                    .filter(entry -> playerUUID.equals(entry.getValue()))
                    .map(Map.Entry::getKey)
                    .findFirst().get();
            spawnLocations.playersSpawns().replace(key, Utils.DUMMY_UUID);
        }
    }

    public void shutdown() {
        HandlerList.unregisterAll(currentGameState);
        if (currentGameState != null) {
            currentGameState.onDisable();
        }
    }

    //EVENTS

    @EventHandler //No se puede romper bloques si no se esta en creativo
    public void blockBreakEvent(BlockBreakEvent event){
        if(event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void playerJump(PlayerJumpEvent event){
        if(event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) return;
        if(event.getPlayer().getWorld() != miniGameWorld) return;
        if(currentGameState instanceof WaitingState){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void playerItemDropEvent(PlayerDropItemEvent event){
        if(event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) return;
        event.setCancelled(true);
    }

    // - EVENT HOOKS -

    @EventHandler // playerJoin y playerLeave en GameState.java
    public void playerChangeWorld(PlayerChangedWorldEvent event){
        Player player = event.getPlayer();
        if(player.getWorld() == miniGameWorld){
            currentGameState.playerJoin(player);
        } else if (event.getFrom() == miniGameWorld) {
            currentGameState.playerLeave(event.getPlayer());
        }
    }

    @EventHandler //playerMove en GameState.java
    public void playerMove(PlayerMoveEvent event){
        if(event.getPlayer().getWorld() != miniGameWorld) return;
        currentGameState.playerMove(event);
    }

    @EventHandler //playerInteract en GameState.java
    public void playerInteract(PlayerInteractEvent event) {
        if(event.getPlayer().getWorld() != miniGameWorld) return;
        currentGameState.playerInteract(event);
    }

    // - EVENT HOOKS -

    @EventHandler
    public void openInventory(InventoryOpenEvent event){
        if(event.getPlayer().getWorld() != miniGameWorld) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void inventoryDrag(InventoryDragEvent event){
        if(event.getWhoClicked().getWorld() != miniGameWorld) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void fallingBlockSolidify(EntityBlockFormEvent event){
        if(event.getEntity().getWorld() != miniGameWorld) return;
        event.setCancelled(true);
    }
}
