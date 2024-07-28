package dev.galacticmc.hitball.objects;

import dev.galacticmc.hitball.HitBallPlugin;
import dev.galacticmc.hitball.objects.states.StateManager;
import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import dev.lone.itemsadder.api.ItemsAdder;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

import java.util.*;

public class WorldManager implements Listener {

    private final HitBallPlugin plugin;
    private final HashMap<World, StateManager> worldPerStateManager;
    private final ConfigurationSection worldsSection;

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
            if (minPlayers > spawnLocations.getPlayersSpawns().size()) {
                plugin.getLogger().severe(String.format("La cantidad mínima de jugadores (%d) en el mundo %s es mayor que la cantidad de ubicaciones de spawn disponibles (%d).", minPlayers, worldName, spawnLocations.getPlayersSpawns().size()));
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

    public World getBestWorld() {
        return worldPerStateManager.keySet().stream()
                .sorted(Comparator.comparingInt(world ->
                        worldPerStateManager.get(world).getMaxPlayers() - world.getPlayers().size()))
                .filter(world -> world.getPlayers().size() < worldPerStateManager.get(world).getMaxPlayers())
                .findFirst()
                .orElse(null);
    }


}
