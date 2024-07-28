package dev.galacticmc.hitball.commands;

import dev.galacticmc.hitball.HitBallPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class SubCommand {

    public HitBallPlugin plugin;

    public SubCommand(HitBallPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * @return The name of the subcommand
     */
    public abstract String getName();

    /**
     * @return The aliases that can be used for this command. Can be null
     */
    public abstract List<String> getAliases();

    /**
     * @return A description of what the subcommand does to be displayed
     */
    public abstract String getDescription();

    /**
     * @return An example of how to use the subcommand
     */
    public abstract String getSyntax();

    /**
     * @param sender The thing that ran the command
     * @param args   The args passed into the command when run
     */
    public abstract void perform(CommandSender sender, String[] args);

    /**
     * @param player The player who ran the command
     * @param args   The args passed into the command when run
     * @return A list of arguments to be suggested for autocomplete
     */
    public abstract List<String> getSubcommandArguments(Player player, String[] args);

}