package dev.galacticmc.hitball.objects;

import dev.galacticmc.hitball.HitBallPlugin;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.List;
import java.util.concurrent.Callable;

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

    /**
     * This method will execute the given Runnable in the Main thread within the next tick
     * @param lambda The lambda to be run.
     * @see Thread
     * @see org.bukkit.scheduler.BukkitTask
     */
    public void runSafeLambda(Runnable lambda){
        Callable<Boolean> call = () -> {
            lambda.run();
            return true;
        };
        var f = Bukkit.getScheduler().callSyncMethod(plugin, call);
    }

}
