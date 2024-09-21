package dev.galacticmc.hitball.objects;

import dev.galacticmc.hitball.objects.skills.Skill;
import dev.galacticmc.hitball.objects.skills.impl.ReviveSkill;
import dev.galacticmc.hitball.util.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.bukkit.ChatColor;

import java.util.Map;

public enum LangKey {

    PREFIX("prefix"),
    //Waiting state
    PLAYER_JOIN("player_join"),
    PLAYER_LEAVE("player_leave"),
    WAITING_30("waiting_30"),
    WAITING_15("waiting_15"),
    WAITING_5("waiting_5"),
    WAITING_TITLE("waiting_title"),
    GAME_FULL("game_full"),
    GAME_START("game_start"),
    GAME_COUNTDOWN("game_countdown"),
    RESTORE_COUNTDOWN("restore_countdown"),
    SPAWN_TP("spawn_tp"),
    //Playing state
    PLAYER_KILL_CHAT("player_kill_chat"),
    PLAYER_DEATH_CHAT("player_death_chat"),
    PLAYER_KILL_ACTION_BAR("player_kill_action_bar"),
    PLAYER_DEATH_ACTION_BAR("player_death_action_bar"),
    PLAYER_DEATH_ACTION_BAR_NO_KILLER("player_death_action_bar"),
    DEATH_TITLE("death_title"),
    WIN_TITLE("win_title"),
    SHIELD_COOLDOWN("shield_cooldown"),
    PLAYER_LEAVE_STOP("player_leave_stop"),
    SHIELD_ACTIVE("shield_active"),
    //Abilities
    DEFAULT_CANT_USE("default_cant_use"),
    REVIVE_CANT_USE("revive_cant_use");

    public final String key;
    public String value = "N/A";

    LangKey(String key) {
        this.key = key;
    }

    public void setValue(String value){
        this.value = value;
    }

    public Component formatted(){
        if(value.contains("%prefix%")){
            this.value = value.replace("%prefix%", PREFIX.value);
        }
        return Component.text(ChatColor.translateAlternateColorCodes('&', value));
    }

    public Component formatted(String... placeholders){
        Map<String, String> entries = Utils.createMap(placeholders);
        Component base = formatted();
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            String match = entry.getKey();
            match = String.format("%%%s%%", match);
            String replace = entry.getValue();
            base = base.replaceText(TextReplacementConfig.builder().matchLiteral(match).replacement(replace).build());
        }
        return base;
    }

    /**
     *
     * TODO: Change to swtich expression.
     * @param skill
     * @return The corresponent lang key to be used.
     */
    public static LangKey getFailedSkillTranslation(Skill skill) {
        if(skill instanceof ReviveSkill){
            return LangKey.REVIVE_CANT_USE;
        }else {
            return LangKey.DEFAULT_CANT_USE;
        }
    }

}
