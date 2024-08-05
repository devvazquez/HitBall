package dev.galacticmc.hitball.objects.states;

import dev.galacticmc.hitball.HitBallPlugin;
import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.CustomEntity;
import dev.lone.itemsadder.api.CustomFurniture;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;

public class BallEntity{

    private final Location initialLocation;
    private final HitBallPlugin plugin;

    private Vector velocity;
    private FallingBlock entity;

    public BallEntity(Location initialLocation, HitBallPlugin plugin){
        this.initialLocation = initialLocation;
        this.plugin = plugin;
        setVelocity(new Vector());
    }

    public void spawn(){
        Location location;
        if(entity == null){
            location = initialLocation;
        }else {
            entity.remove();
            location = getLocation();
        }
        plugin.getThreadSafeMethods().runSafeLambda(()->{
            setEntity(createInitialFallingBlock(location));
            entity.setGravity(false);
            entity.setDropItem(false);
            entity.setCustomName("Ball");
            entity.setInvulnerable(true);
            setLocation(getLocation());
            setVelocity(getVelocity());
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

    public FallingBlock getEntity(){
        if(entity == null){
            spawn();
        }
        return entity;
    }

    private FallingBlock createInitialFallingBlock(Location loc){
        return loc.getWorld().spawnFallingBlock(loc, Material.PACKED_ICE.createBlockData());
    }

}
