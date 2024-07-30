package dev.galacticmc.hitball.objects.states;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import dev.galacticmc.hitball.HitBallPlugin;
import dev.galacticmc.hitball.objects.skills.SkillManager;
import dev.galacticmc.hitball.objects.states.impl.WaitingState;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;

import java.util.ArrayList;
import java.util.Optional;

public class StateManager implements Listener {

    private final HitBallPlugin plugin;

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

    private final SkillManager skillManager;

    private final ArrayList<HitBallPlayer> players;
    public ArrayList<HitBallPlayer> getPlayers() { return players; }

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
        this.skillManager = new SkillManager(plugin, this);
        this.players = new ArrayList<>();
    }

    public void start(){
        nextGameState(new WaitingState());
    }

    public void nextGameState(GameState state) {
        if (currentGameState != null) {
            HandlerList.unregisterAll(currentGameState);
            currentGameState.onDisable();
        }
        this.currentGameState = state;
        plugin.getServer().getPluginManager().registerEvents(currentGameState, plugin);
        currentGameState.onEnable(plugin, this);
    }

    public void shutdown() {
        HandlerList.unregisterAll(currentGameState);
        if (currentGameState != null) {
            currentGameState.onDisable();
        }
    }

    public HitBallPlayer getHitBallPlayer(Player player) {
        Optional<HitBallPlayer> hitPlayer = players.stream().filter(hitBallPlayer -> hitBallPlayer.getSelf() == player).findFirst();
        if(hitPlayer.isPresent()){
            return hitPlayer.get();
        }else{
            throw new RuntimeException("No se pudo encotrar el jugador en la lista.");
        }
    }

    //EVENTS

    @EventHandler //No se puede romper bloques si no se esta en creativo
    public void blockBreakEvent(BlockBreakEvent event){
        if(event.getPlayer().getGameMode() != GameMode.CREATIVE)
            event.setCancelled(true);
    }

    @EventHandler
    public void playerJump(PlayerJumpEvent event){
        if(event.getPlayer().getWorld() != miniGameWorld) return;
        if(currentGameState instanceof WaitingState){
            if(event.getPlayer().getGameMode() != GameMode.CREATIVE){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        if(players.stream().noneMatch(hitBallPlayer -> hitBallPlayer.getSelf() == player)){
            HitBallPlayer newPlayer = new HitBallPlayer(plugin, player.getUniqueId(), skillManager.getSkillsForPlayer(player));
            players.add(newPlayer);
        }
    }

    @EventHandler
    public void playerLeave(PlayerQuitEvent event){
        Player player = event.getPlayer();
        if(players.stream().anyMatch(hitBallPlayer -> hitBallPlayer.getSelf() == player)){
            players.remove(getHitBallPlayer(player));
        }
    }

    // - EVENT HOOKS -

    @EventHandler // playerJoin y playerLeave en GameState.java
    public void playerChangeWorld(PlayerChangedWorldEvent event){
        Player player = event.getPlayer();
        if(event.getPlayer().getWorld() == miniGameWorld){
            getHitBallPlayer(player).joinGame();
            currentGameState.playerJoin(event.getPlayer());
        } else if (event.getFrom() == miniGameWorld) {
            getHitBallPlayer(player).leaveGame();
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
