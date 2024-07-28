package dev.galacticmc.hitball.commands.impl;

import dev.galacticmc.hitball.HitBallPlugin;
import dev.galacticmc.hitball.commands.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class TeleportCommand extends SubCommand {

    public TeleportCommand(HitBallPlugin plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "teleport";
    }

    @Override
    public List<String> getAliases() {
        return Collections.emptyList();
    }

    @Override
    public String getDescription() {
        return "Teleports the player to the best game.";
    }

    @Override
    public String getSyntax() {
        return "/teleport";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if(!(sender instanceof Player)) return;
        Player player = (Player) sender;
        player.teleport(plugin.getWorldManager().getBestWorld().getSpawnLocation());
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return Collections.emptyList();
    }
}
