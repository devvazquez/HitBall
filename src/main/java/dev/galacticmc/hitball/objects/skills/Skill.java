package dev.galacticmc.hitball.objects.skills;

import dev.galacticmc.hitball.HitBallPlugin;
import dev.galacticmc.hitball.objects.skills.impl.ReviveSkill;
import dev.galacticmc.hitball.objects.states.StateManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public abstract class Skill{

    public HitBallPlugin plugin;
    public StateManager stateManager;
    public ItemStack icon;

    public void setItemIcon(ItemStack m){
        this.icon = m;
    }
    public ItemStack getIcon(){
        return icon;
    }

    public Skill(HitBallPlugin plugin, StateManager stateManager) {
        this.plugin = plugin;
        this.stateManager = stateManager;
    }

    public abstract void perfomSkill(UUID self);
    public abstract boolean checksForPlayer(UUID player);

    public void executeSkill(UUID player){
        if(checksForPlayer(player)){
            perfomSkill(player);
        }
    }

}
