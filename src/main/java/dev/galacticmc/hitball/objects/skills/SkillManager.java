package dev.galacticmc.hitball.objects.skills;

import dev.galacticmc.hitball.HitBallPlugin;
import dev.galacticmc.hitball.objects.skills.impl.InvisibleSkill;
import dev.galacticmc.hitball.objects.skills.impl.ReviveSkill;
import dev.galacticmc.hitball.objects.skills.impl.movement.LockPlayersSkill;
import dev.galacticmc.hitball.objects.skills.impl.movement.SlowDownSkill;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class SkillManager implements Listener {

    public final ReviveSkill REVIVE_SKILL;
    public final SlowDownSkill SLOW_DOWN_SKILL;
    public final LockPlayersSkill LOCK_PLAYERS_SKILL;
    public final InvisibleSkill INVISIBLE_SKILL;
    public final List<Skill> STATIC_SKILLS = new ArrayList<>();

    public SkillManager(HitBallPlugin hitBallPlugin) {
        REVIVE_SKILL = new ReviveSkill(hitBallPlugin, "revive");
        STATIC_SKILLS.add(REVIVE_SKILL);
        SLOW_DOWN_SKILL = new SlowDownSkill(hitBallPlugin, "lockplayers");
        STATIC_SKILLS.add(SLOW_DOWN_SKILL);
        LOCK_PLAYERS_SKILL = new LockPlayersSkill(hitBallPlugin, "slowdown");
        STATIC_SKILLS.add(LOCK_PLAYERS_SKILL);
        INVISIBLE_SKILL = new InvisibleSkill(hitBallPlugin, "invisible");
        STATIC_SKILLS.add(INVISIBLE_SKILL);
    }

    public List<Skill> getSkillsForPlayer(Player player) {
        List<Skill> skills = new ArrayList<>();
        STATIC_SKILLS.forEach(skill -> {
            if(player.isPermissionSet("hitball." + skill.getPermission())) {
                skills.add(skill);
            }
        });
        return skills;
    }

}
