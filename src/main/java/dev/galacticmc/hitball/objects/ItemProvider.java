package dev.galacticmc.hitball.objects;

import dev.galacticmc.hitball.HitBallPlugin;
import dev.galacticmc.hitball.objects.skills.Skill;
import dev.galacticmc.hitball.objects.states.HitBallPlayer;

import java.util.List;

public class ItemProvider {

    private final HitBallPlugin hitBallPlugin;

    private ItemProvider(HitBallPlugin plugin) {
        this.hitBallPlugin = plugin;
    }

    public static ItemProvider withPlugin(HitBallPlugin plugin){
        return new ItemProvider(plugin);
    }

    public void setupPlayerInventory(HitBallPlayer player){
        List<Skill> skills = player.getSkills();

    }

}
