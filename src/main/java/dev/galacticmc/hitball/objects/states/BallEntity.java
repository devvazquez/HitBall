package dev.galacticmc.hitball.objects.states;

import dev.galacticmc.hitball.HitBallPlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.FallingBlock;
import org.bukkit.util.Vector;

public class BallEntity{

    private final Location initialLocation;
    private final HitBallPlugin plugin;

    private Vector velocity;
    private FallingBlock entity;
    private Runnable callBack;
    public void whenAlive(Runnable callBack){
        this.callBack = callBack;
    }

    public BallEntity(Location initialLocation, HitBallPlugin plugin){
        this.initialLocation = initialLocation;
        this.plugin = plugin;
        setVelocity(new Vector());
    }

    public void spawn(boolean revive){
        Location location;
        boolean runCallback;
        if(revive){
            runCallback = false;
            location = initialLocation;
        }else if(entity == null){
            location = initialLocation;
            runCallback = true;
        }else {
            runCallback = false;
            //Set the location so that it is never null.
            location = getLocation();
        }

        plugin.getThreadSafeMethods().runSafeLambda(() -> {
            setEntity(createInitialFallingBlock(location));
            entity.setGravity(false);
            entity.setDropItem(false);
            entity.setCustomName("Ball");
            entity.setCustomNameVisible(false);
            entity.setInvulnerable(true);
            setLocation(getLocation());
            setVelocity(getVelocity());
            if(!runCallback) return;
            this.callBack.run();
        });
    }

    public void setVelocity(Vector velocity) {
        this.velocity = velocity;
        if(entity != null){
            this.entity.setVelocity(velocity);
        }
    }

    public Vector getVelocity() {
        return velocity;
    }

    public void setLocation(Location location){
        if(entity != null){
            this.entity.getLocation(location);
        }
    }

    public Location getLocation() {
        return getEntity().getLocation();
    }

    public void setEntity(FallingBlock e){
        this.entity = e;
    }

    public FallingBlock getEntity() {
        return entity;
    }

    private FallingBlock createInitialFallingBlock(Location loc){
        return loc.getWorld().spawnFallingBlock(loc, Material.PACKED_ICE.createBlockData());
    }

}
