package dev.galacticmc.hitball.objects;

import dev.galacticmc.hitball.HitBallPlugin;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

public class HitBallExpansion extends PlaceholderExpansion {

    private HitBallPlugin plugin;
    public HitBallExpansion(HitBallPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "hitball";
    }

    @Override
    public @NotNull String getAuthor() {
        return "nebulusdev";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if(player == null) return "";
        UUID uuid;
        switch (params){
            case "most_kills_name":
                uuid = UUID.fromString(plugin.getDatabase().getPlayerWithMostKills());
                return Objects.requireNonNull(Bukkit.getOfflinePlayer(uuid)).getName();
            case "most_kills_number":
                return String.valueOf(plugin.getDatabase().getKills(UUID.fromString(plugin.getDatabase().getPlayerWithMostKills())));
            default:
                // Check if the params match the "kills_<playerName>" pattern
                if (params.matches("^kills_\\w{3,16}$")) {
                    //Obtener el nombre del jugador
                    String playerName = params.substring(6);
                    uuid = Objects.requireNonNull(Bukkit.getOfflinePlayer(playerName)).getUniqueId();
                    //Devolver el valor de la base de datos.
                    return String.valueOf(plugin.getDatabase().getKills(uuid));
                }
                return "";
        }
    }

}
