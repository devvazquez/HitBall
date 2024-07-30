package dev.galacticmc.hitball.objects.managers;

import dev.galacticmc.hitball.HitBallPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import static dev.galacticmc.hitball.objects.LangKey.*;

public class LanguageManager {

    private HitBallPlugin plugin;
    
    public LanguageManager(HitBallPlugin plugin) {
        this.plugin = plugin;
        loadTranslations();
    }

    private void loadTranslations() {
        File langFile = new File(plugin.getDataFolder(), "lang_es.yml");
        if (!langFile.exists()) {
            plugin.saveResource("lang_es.yml", false);
        }

        FileConfiguration langConfig = YamlConfiguration.loadConfiguration(langFile);

        //Playing State
        PREFIX.setValue(langConfig.getString(PREFIX.key, "&d&lGalactic&5&lMC &8&l»"));
        PLAYER_JOIN.setValue(langConfig.getString(PLAYER_JOIN.key, "\"&f[&a+&f\"]&b %player_name%&a se ha unido a la partida!\""));
        PLAYER_LEAVE.setValue(langConfig.getString(PLAYER_LEAVE.key, "El jugador %player_name% ha abandonado la partida!"));
        WAITING_30.setValue(langConfig.getString(WAITING_30.key, "%prefix% &a¡Esperando a más jugadores &e(30s)!"));
        WAITING_15.setValue(langConfig.getString(WAITING_15.key,"%prefix% &a¡Esperando a más jugadores &e(15s)!"));
        WAITING_5.setValue(langConfig.getString(WAITING_5.key, "%prefix% &a¡Esperando a más jugadores &e%segundos%"));
        WAITING_TITLE.setValue(langConfig.getString(WAITING_TITLE.key, "Esperando a los jugadores..."));
        GAME_START.setValue(langConfig.getString(GAME_START.key, "¡El juego ha comenzado!"));
        GAME_COUNTDOWN.setValue(langConfig.getString(GAME_COUNTDOWN.key, "El juego empieza en"));
        RESTORE_COUNTDOWN.setValue(langConfig.getString(RESTORE_COUNTDOWN.key, "%prefix% &c¡Faltan jugadores para alcanzar el mínimo!, RESTAURANDO LA CUENTA ATRÁS...!"));
        GAME_FULL.setValue(langConfig.getString(GAME_FULL.key, "%prefix% &c¡Lo siento! La sala esta llena!"));
        SPAWN_TP.setValue(langConfig.getString(SPAWN_TP.key, "%prefix% &a¡Enviándote al spawn!"));
        //Waiting State
        PLAYER_KILL_CHAT.setValue(langConfig.getString(PLAYER_KILL_CHAT.key, "%aEl jugador &b%killer%&a ha matado a &c%killed%"));
        PLAYER_DEATH_CHAT.setValue(langConfig.getString(PLAYER_DEATH_CHAT.key, "&aEl jugador &b%killed%&a ha muerto"));
        PLAYER_KILL_ACTION_BAR.setValue(langConfig.getString(PLAYER_KILL_ACTION_BAR.key, "&a¡Has matado a %killed_player%!"));
        PLAYER_DEATH_ACTION_BAR.setValue(langConfig.getString(PLAYER_DEATH_ACTION_BAR.key, "&c¡Has sido eliminado por %killer_name%!"));
        PLAYER_DEATH_ACTION_BAR_NO_KILLER.setValue(langConfig.getString(PLAYER_DEATH_ACTION_BAR_NO_KILLER.key, "&c¡Has sido eliminado!"));
        DEATH_TITLE.setValue(langConfig.getString(DEATH_TITLE.key, "&c¡HAS MUERTO!"));
        WIN_TITLE.setValue(langConfig.getString(WIN_TITLE.key, "&a¡HAS GANADO!"));
        SHIELD_COOLDOWN.setValue(langConfig.getString(SHIELD_COOLDOWN.key, "&c¡El escudo se está enfriando!"));
        PLAYER_LEAVE_STOP.setValue(langConfig.getString(PLAYER_LEAVE_STOP.key, "%prefix%&b %player_name% &c¡Ha abandonado la partida y no es posible continuar!"));
        SHIELD_ACTIVE.setValue(langConfig.getString(SHIELD_ACTIVE.key, "Escudo Activo"));
        //Abilities
        REVIVE_CANT_USE.setValue(langConfig.getString(REVIVE_CANT_USE.key, "&c¡Solo puedes utilizar la habilidad cuando estés muerto!"));


    }

}
