package dev.galacticmc.hitball.objects;

import dev.galacticmc.hitball.HitBallPlugin;
import dev.galacticmc.hitball.objects.states.HitBallPlayer;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.List;

public class ThreadSafeMethods {

    /*
        This class contains some methods reinterpretations that can be run in a secondary thread (thread safe)
    */
    private final HitBallPlugin plugin;

    public ThreadSafeMethods(HitBallPlugin plugin){
        this.plugin = plugin;
    }

    //World::getPlayers() is not thread safe.
    public List<HitBallPlayer> getPlayersInWorld(World world){
        return Bukkit.getOnlinePlayers().stream()
                .filter(player -> player.getWorld() == world) //Is player in world?
                .map(player -> plugin.getWorldManager().getHitBallPlayer(player)) //Map player to HitBallPlayer
                .toList();
    }

    public void runSafeLambda(Runnable lambda){
        Bukkit.getScheduler().runTask(plugin, lambda);
    }

}
