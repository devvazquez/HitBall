package dev.galacticmc.hitball.objects.skills;

import dev.galacticmc.hitball.HitBallPlugin;
import dev.galacticmc.hitball.objects.skills.impl.ReviveSkill;
import dev.galacticmc.hitball.objects.states.StateManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class SkillManager implements Listener {

    private final HitBallPlugin plugin;
    private final StateManager stateManager;

    public SkillManager(HitBallPlugin hitBallPlugin, StateManager stateManager) {
        this.plugin = hitBallPlugin;
        this.stateManager = stateManager;
    }

    public List<Skill> getSkillsForPlayer(Player player) {
        List<Skill> skills = new ArrayList<>();
        for (PermissionAttachmentInfo perm : player.getEffectivePermissions()) {
            // Example: hitball.revive
            String name = perm.getPermission();
            if (!name.startsWith("hitball.skill.")) continue;
            switch (name.substring(13)) { // Change to 13 to account for the length of "hitball."
                case "revive":
                    skills.add(new ReviveSkill(plugin, stateManager));
                    break;
                // Add more cases here for other skills
            }
        }
        return skills;
    }

}
