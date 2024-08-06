package dev.galacticmc.hitball.objects;

import dev.galacticmc.hitball.HitBallPlugin;
import dev.galacticmc.hitball.objects.skills.Skill;
import dev.galacticmc.hitball.objects.states.InGameProperties;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class HitBallPlayer {

    private final HitBallPlugin plugin;
    private final UUID self;

    private final List<Skill> skills;
    private Skill selectedSkill;
    public boolean hasSkill(){
        return selectedSkill != null;
    }
    public Skill getCurrentSkill(){
        return selectedSkill;
    }

    private String selectedSword;
    public String getSelectedSword(){
        return selectedSword;
    }
    public void setSelectedSword(String swordNameSpace){
        this.selectedSword = swordNameSpace;
    }

    private InGameProperties properties;
    public InGameProperties getProperties() {
        return properties;
    }

    public boolean inGame = false;

    public HitBallPlayer(HitBallPlugin plugin, UUID self, List<Skill> skills) {
        this.plugin = plugin;
        this.self = self;
        this.skills = skills;
        this.selectedSkill = skills.stream().findFirst().orElse(null);
        this.selectedSword = plugin.getDatabase().getEquippedItem(self);
    }

    public Player getSelf() {
        return Bukkit.getPlayer(self);
    }

    public List<Skill> getSkills() {
        return skills;
    }

    public void joinGame(){
        if(inGame) throw new IllegalStateException(String.format("Player is already in-game! (%s)", getSelf().getName()));
        if(properties != null) throw new IllegalStateException(String.format("Player already has in-game properties! (%s)", getSelf().getName()));
        this.inGame = true;
        this.properties = new InGameProperties(this, plugin);
    }

    public void leaveGame(){
        if(!inGame) throw new IllegalStateException(String.format("Player is not in-game! (%s)", getSelf().getName()));
        if(properties == null) throw new IllegalStateException(String.format("Player doesn't has in-game properties! (%s)", getSelf().getName()));
        this.inGame = false;
        this.properties = null;
    }

}
