package dev.galacticmc.hitball.objects;

import dev.galacticmc.hitball.HitBallPlugin;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
        //Kils for a given player
        if (params.equals("most_kills_name")) {
            return plugin.getDatabase().getPlayerWithMostKills();
        }

        if (params.equals("most_kills_number")) {
            return String.valueOf(plugin.getDatabase().getKills(Bukkit.getOfflinePlayer(plugin.getDatabase().getPlayerWithMostKills()).getUniqueId()));
        }

        if (params.matches("^kills_top_number_\\d+$")) {
            // Obtain the rank number
            int rank = Integer.parseInt(params.substring(17));
            // Return the number of kills from the database for the player at this rank.
            return String.valueOf(plugin.getDatabase().getKillsByRank(rank));
        }

        if (params.matches("^kills_top_name_\\d+$")) {
            // Obtain the rank number
            int rank = Integer.parseInt(params.substring(15));

            // Return the player name from the database for the player at this rank.
            String name = plugin.getDatabase().getNameByRank(rank);
            if(player != null && name.equals(player.getName())){
                name = ChatColor.translateAlternateColorCodes('&', "&b" + name);
            }
            return name;
        }

        if (params.matches("^kills_\\w{3,16}$")) {
            // Obtain the player name
            String playerName = params.substring(6);
            UUID uuid = Objects.requireNonNull(Bukkit.getOfflinePlayer(playerName)).getUniqueId();
            // Return the value from the database.
            return String.valueOf(plugin.getDatabase().getKills(uuid));
        }

        return null;
    }

}
