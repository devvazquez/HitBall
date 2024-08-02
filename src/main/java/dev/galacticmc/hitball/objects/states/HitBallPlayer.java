package dev.galacticmc.hitball.objects.states;

import dev.galacticmc.hitball.HitBallPlugin;
import dev.galacticmc.hitball.objects.LangKey;
import dev.galacticmc.hitball.objects.skills.Skill;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.UUID;

public class HitBallPlayer {

    private final HitBallPlugin plugin;
    private final UUID self;
    private final List<Skill> skills;
    private final Skill selectedSkill;
    public boolean hasSkill(){
        return selectedSkill != null;
    }
    public Skill getCurrentSkill(){
        return selectedSkill;
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
