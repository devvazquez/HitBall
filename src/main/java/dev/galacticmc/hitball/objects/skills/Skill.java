package dev.galacticmc.hitball.objects.skills;

import dev.galacticmc.hitball.HitBallPlugin;
import dev.galacticmc.hitball.objects.HitBallPlayer;
import dev.galacticmc.hitball.objects.states.StateManager;
import org.bukkit.inventory.ItemStack;

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

    public abstract void perfomSkill(HitBallPlayer self, StateManager stateManager);
    public abstract boolean checksForPlayer(HitBallPlayer player, StateManager stateManager);

    public void executeSkill(HitBallPlayer player, StateManager stateManager){
        if(checksForPlayer(player, stateManager)){
            perfomSkill(player, stateManager);
        }
    }

}
