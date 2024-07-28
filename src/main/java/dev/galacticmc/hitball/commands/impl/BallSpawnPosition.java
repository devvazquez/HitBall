package dev.galacticmc.hitball.commands.impl;

import dev.galacticmc.hitball.HitBallPlugin;
import dev.galacticmc.hitball.commands.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class BallSpawnPosition extends SubCommand {

    public BallSpawnPosition(HitBallPlugin plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "ballSpawn";
    }

    @Override
    public List<String> getAliases() {
        return Collections.emptyList();
    }

    @Override
    public String getDescription() {
        return "Establece la posicion de spawn de la bola.";
    }

    @Override
    public String getSyntax() {
        return "/ballSpawn";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if(!(sender instanceof Player)) return;
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
        plugin.getConfigManager().setBallSpawnLocation(player.getLocation(), worldConfig);
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return Collections.emptyList();
    }
}
