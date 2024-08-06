package dev.galacticmc.hitball.objects.skills.impl;

import dev.galacticmc.hitball.HitBallPlugin;
import dev.galacticmc.hitball.objects.LangKey;
import dev.galacticmc.hitball.objects.skills.Skill;
import dev.galacticmc.hitball.objects.HitBallPlayer;
import dev.galacticmc.hitball.objects.states.StateManager;
import dev.galacticmc.hitball.objects.states.impl.PlayingState;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;

public class ReviveSkill extends Skill {

    public ReviveSkill(HitBallPlugin plugin) {
        super(plugin);
        ItemStack icon = new ItemStack(Material.YELLOW_BED);
        icon.lore(Collections.singletonList(Component.text(ChatColor.GOLD + "Revivir")));
        setItemIcon(icon);
    }

    @Override
    public void perfomSkill(HitBallPlayer player, StateManager stateManager) {
        PlayingState state = (PlayingState)stateManager.getCurrentGameState();
        state.revivePlayer(player);
    }

    @Override
    public boolean checksForPlayer(HitBallPlayer player, StateManager stateManager) {
        if( player.inGame && !player.getProperties().isAlive()
            && stateManager.getCurrentGameState() instanceof PlayingState){
            return true;
        }else {
            player.getSelf().sendMessage(LangKey.REVIVE_CANT_USE.formatted());
        }
        return false;
    }

}
