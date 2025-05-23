package dev.galacticmc.hitball.objects;

import dev.galacticmc.hitball.HitBallPlugin;
import dev.galacticmc.hitball.objects.skills.Skill;
import dev.galacticmc.hitball.objects.states.InGameProperties;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.List;
import java.util.UUID;

public class HitBallPlayer {

    private final HitBallPlugin plugin;
    private final UUID self;

    private List<Skill> skills;
    private Skill selectedSkill;
    public boolean hasSkill(){
        return selectedSkill != null;
    }
    public void updateSkills() {
        this.skills = plugin.getSkillManager().getSkillsForPlayer(getSelf());
        if(this.selectedSkill != null){
            this.selectedSkill = skills.stream().filter(skill -> skill.getPermission().equals(selectedSkill.getPermission())).findFirst().orElse(null);
        }else {
            this.selectedSkill = skills.stream().findFirst().orElse(null);
        }
    }

    public Skill getCurrentSkill(){
        return selectedSkill;
    }
    public void setSelectedSkill(Skill skill){
        this.selectedSkill = skill;
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
    }


    public Player getSelf() {
        return Bukkit.getPlayer(self);
    }

    public List<Skill> getSkills() {
        updateSkills();
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
