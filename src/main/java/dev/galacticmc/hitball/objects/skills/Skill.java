package dev.galacticmc.hitball.objects.skills;

import dev.galacticmc.hitball.HitBallPlugin;
import dev.galacticmc.hitball.objects.skills.impl.ReviveSkill;
import dev.galacticmc.hitball.objects.states.StateManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public abstract class Skill{

    private HitBallPlugin plugin;

    private ItemStack icon;
    public void setItemIcon(ItemStack m){
        this.icon = m;
    }
    public ItemStack getIcon(){
        return icon;
    }

    public Skill(HitBallPlugin plugin) {
        this.plugin = plugin;
    }

    public abstract void perfomSkill(UUID self, StateManager stateManager);
    public abstract boolean checksForPlayer(UUID player, StateManager stateManager);

    public void executeSkill(UUID player, StateManager stateManager){
        if(checksForPlayer(player, stateManager)){
            perfomSkill(player, stateManager);
        }
    }

}
