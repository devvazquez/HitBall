package dev.galacticmc.hitball.objects.skills;

import dev.galacticmc.hitball.HitBallPlugin;
import dev.galacticmc.hitball.objects.HitBallPlayer;
import dev.galacticmc.hitball.objects.LangKey;
import dev.galacticmc.hitball.objects.skills.impl.movement.IMovementSkill;
import dev.galacticmc.hitball.objects.states.StateManager;
import dev.galacticmc.hitball.objects.states.impl.PlayingState;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import net.kyori.adventure.text.TextComponent;

public abstract class Skill{

    private HitBallPlugin plugin;
    private final String permission;
    public String getPermission() {
        return permission;
    }

    private final ItemStack icon;
    public ItemStack getIcon(){
        return icon.asQuantity(usages);
    }

    private final String name;
    public String getName(){
        return name;
    }

    private final TextComponent description;
    public TextComponent getDescription(){
        return description;
    }

    private int usages;
    public int getUsages(){
        return usages;
    }

    public Skill(HitBallPlugin plugin, String perm, String name, TextComponent desc, Material icon, int usages) {
        this.plugin = plugin;
        this.permission = perm;
        this.name = name;
        this.description = desc;
        this.icon = new ItemStack(icon);
        this.usages = usages;
        assert usages > 0;
    }

    public abstract void perfomSkill(HitBallPlayer self, StateManager stateManager);
    public abstract boolean checksForPlayer(HitBallPlayer player, StateManager stateManager);

    public void executeSkill(HitBallPlayer player, StateManager stateManager){

        if(checksForPlayer(player, stateManager)){
            if(getUsages() == 0){
                player.getSelf().sendMessage(LangKey.PREFIX.formatted().append(Component.text(" No le quedan mas usos a la habilidad.")));
                return;
            }

            //A movement skill has already been called
            if(this instanceof IMovementSkill && stateManager.getCurrentGameState() instanceof PlayingState playingState){
                if(playingState.isSkillAlreadyModifyingPlayerMovement()){
                    player.getSelf().sendMessage(LangKey.PREFIX.formatted().append(Component.text(" Alguien ya ha usado una habilidad que modifica el movimiento de los jugadores!")));
                    return;
                }
            }

            this.usages--;
            player.getProperties().addSkillItem();
            player.getSelf().setCooldown(icon.getType(), 20 * 5);

            perfomSkill(player, stateManager);
        }else {
            player.getSelf().sendMessage(LangKey.getFailedSkillTranslation(this).formatted());
        }
    }


}
