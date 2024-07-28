package dev.galacticmc.hitball.objects.skills.impl;

import dev.galacticmc.hitball.HitBallPlugin;
import dev.galacticmc.hitball.objects.LangKey;
import dev.galacticmc.hitball.objects.skills.Skill;
import dev.galacticmc.hitball.objects.states.StateManager;
import dev.galacticmc.hitball.objects.states.impl.PlayingState;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.UUID;

public class ReviveSkill extends Skill {

    public ReviveSkill(HitBallPlugin plugin, StateManager stateManager) {
        super(plugin, stateManager);
        ItemStack icon = new ItemStack(Material.YELLOW_BED);
        icon.lore(Collections.singletonList(Component.text(ChatColor.GOLD + "Revivir")));
        setItemIcon(icon);
    }

    @Override
    public void perfomSkill(UUID self) {
        PlayingState state = (PlayingState)stateManager.getCurrentGameState();
    }

    @Override
    public boolean checksForPlayer(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if(player != null){
            if( player.isInvisible()
                && stateManager.getCurrentGameState() instanceof PlayingState){
                return true;
            }else {
                player.sendMessage(LangKey.REVIVE_CANT_USE.formatted());
            }
        }
        return false;
    }

}
