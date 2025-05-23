package dev.galacticmc.hitball.objects.managers;

import dev.galacticmc.hitball.HitBallPlugin;
import dev.galacticmc.hitball.objects.HitBallPlayer;
import dev.galacticmc.hitball.objects.skills.SkillManager;
import dev.galacticmc.hitball.objects.states.SpawnLocations;
import dev.galacticmc.hitball.objects.states.StateManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldLoadEvent;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class WorldManager implements Listener {

    private final HitBallPlugin plugin;
    private final HashMap<World, StateManager> worldPerStateManager;
    private final ConfigurationSection worldsSection;
    //Thread safe variant of ArrayList
    private final CopyOnWriteArrayList<HitBallPlayer> players;

    public CopyOnWriteArrayList<HitBallPlayer> getOnlinePlayers(){
        return players;
    }

    public WorldManager(HitBallPlugin plugin) {
        this.plugin = plugin;
        this.worldPerStateManager = new HashMap<>();

        //Obtener la seccion 'worlds' de la config
        this.worldsSection = plugin.getConfig().getConfigurationSection("worlds");
        //No puede ser null
        if (worldsSection == null || worldsSection.getKeys(false).isEmpty()) {
            plugin.getLogger().severe("No se estableció ningún mundo en la configuración!");
            plugin.getPluginLoader().disablePlugin(plugin);
        }
        this.players = new CopyOnWriteArrayList<>();
    }

    // - STATIC EVENTS -

    @EventHandler
    public void worldLoad(WorldLoadEvent event){
        World world = event.getWorld();
        String worldName = world.getName();
        //Si la seccion contiene el nombre del mundo.
        if(worldsSection.contains(worldName)){
            ConfigurationSection worldConfig = worldsSection.getConfigurationSection(world.getName());

            //El minimo de jugadores para que empieze la partida
            int minPlayers = worldConfig.getInt("min_players");
            if (minPlayers == 0) {
                plugin.getLogger().severe("La configuración del mundo: " + worldName + " no contiene jugadores mínimos, por favor añada: 'min_players' con valor tipo int.");
                return;
            }

            //El numero de jugadores maximos de la partida.
            int maxPlayers = worldConfig.getInt("max_players");
            if (maxPlayers == 0) {
                plugin.getLogger().severe("La configuración del mundo: " + worldName + " no contiene jugadores máximos, por favor añada: 'max_players' con valor tipo int.");
                return;
            }

            //Obtener los varios puntos de spawn.
            SpawnLocations spawnLocations = plugin.getConfigManager().deserializeSpawnLocations(world, worldConfig);
            if (spawnLocations == null) {
                plugin.getLogger().severe(String.format("Hubo un error deserializando las propiedades del mundo: %s", worldName));
                return;
            }

            // Verificar si minPlayers es mayor que la cantidad de ubicaciones disponibles
            if (minPlayers > spawnLocations.playersSpawns().size()) {
                plugin.getLogger().severe(String.format("La cantidad mínima de jugadores (%d) en el mundo %s es mayor que la cantidad de ubicaciones de spawn disponibles (%d).", minPlayers, worldName, spawnLocations.playersSpawns().size()));
                return;
            }

            //Instanciar stateManager...
            StateManager stateManager = new StateManager(plugin, world, spawnLocations, minPlayers, maxPlayers);
            plugin.getServer().getPluginManager().registerEvents(stateManager, plugin);
            worldPerStateManager.put(world, stateManager);
            //Iniciar el primer GameState (WaitingState)
            stateManager.start();
        }
    }

    //Sorted: Fewer players to be full
    //Filtered: non-full worlds;
    private Stream<World> getAbaliableWorldStream(){
        return worldPerStateManager.keySet().stream()
                .sorted(Comparator.comparingInt(world ->
                        worldPerStateManager.get(world).getMaxPlayers() - world.getPlayers().size()))
                .filter(world -> world.getPlayers().size() < worldPerStateManager.get(world).getMaxPlayers());
    }

    public World getBestWorld() {
        return getAbaliableWorldStream()
                .findFirst()
                .orElse(null);
    }

    public World getBestWorld(int maxPlayers){
        return getAbaliableWorldStream()
                .filter(world -> worldPerStateManager.get(world).getMaxPlayers() == maxPlayers)
                .findFirst()
                .orElse(null);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void playerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        if(players.stream().noneMatch(hitBallPlayer -> hitBallPlayer.getSelf() == player)){
            HitBallPlayer newPlayer = new HitBallPlayer(plugin, player.getUniqueId(), plugin.getSkillManager().getSkillsForPlayer(player));
            players.add(newPlayer);
        }
    }

    public HitBallPlayer getHitBallPlayer(UUID player) {
        Optional<HitBallPlayer> hitPlayer = players.stream().filter(hitBallPlayer -> hitBallPlayer.getSelf().getUniqueId() == player).findFirst();
        if(hitPlayer.isPresent()){
            return hitPlayer.get();
        }else{
            throw new RuntimeException("No se pudo encotrar el jugador en la lista.");
        }
    }

    public HitBallPlayer getHitBallPlayer(Player player) {
        return getHitBallPlayer(player.getUniqueId());
    }

    @EventHandler
    public void playerLeave(PlayerQuitEvent event){
        Player player = event.getPlayer();
        if(players.stream().anyMatch(hitBallPlayer -> hitBallPlayer.getSelf() == player)){
            HitBallPlayer hitBallPlayer = getHitBallPlayer(player);
            players.remove(hitBallPlayer);
        }
    }


}
