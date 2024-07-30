package dev.galacticmc.hitball.objects.states;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import dev.galacticmc.hitball.HitBallPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public interface GameState extends Listener {

    void onEnable(HitBallPlugin plugin, StateManager stateManager);
    void onDisable();
    void playerJoin(Player player);
    void playerLeave(Player player);
    void playerMove(PlayerMoveEvent event);
    void playerInteract(PlayerInteractEvent event);
}
