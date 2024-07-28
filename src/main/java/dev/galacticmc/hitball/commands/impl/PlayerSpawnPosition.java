package dev.galacticmc.hitball.commands.impl;

import dev.galacticmc.hitball.HitBallPlugin;
import dev.galacticmc.hitball.commands.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class PlayerSpawnPosition extends SubCommand {

    public PlayerSpawnPosition(HitBallPlugin plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "playerSpawn";
    }

    @Override
    public List<String> getAliases() {
        return Collections.emptyList();
    }

    @Override
    public String getDescription() {
        return "Añade tu posición como spawn en este mundo.";
    }

    @Override
    public String getSyntax() {
        return "/playerSpawn";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) return;

        Player player = (Player) sender;
        //Commandos que utilizan world config.
        String worldName = player.getWorld().getName();
        ConfigurationSection worldsSection = plugin.getConfig().getConfigurationSection("worlds");
        if (worldsSection == null || !worldsSection.contains(worldName)) {
            player.sendMessage("No se encontró configuración para el mundo actual.");
            return;
        }
        ConfigurationSection worldConfig = worldsSection.getConfigurationSection(worldName);
        if (worldConfig == null) {
            player.sendMessage("Configuración faltante para el mundo actual en la configuración.");
            return;
        }
        plugin.getConfigManager().addPlayerSpawnLocation(player.getLocation(), worldConfig);
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return Collections.emptyList();
    }
}
