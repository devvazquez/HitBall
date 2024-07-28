package dev.galacticmc.hitball.commands.impl;

import dev.galacticmc.hitball.HitBallPlugin;
import dev.galacticmc.hitball.commands.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class ReloadSubCommand extends SubCommand {

    public ReloadSubCommand(HitBallPlugin plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("rl");
    }

    @Override
    public String getDescription() {
        return "Recarga el plugin.";
    }

    @Override
    public String getSyntax() {
        return "/hitball reload";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        plugin.reloadConfig();
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return Collections.emptyList();
    }
}
