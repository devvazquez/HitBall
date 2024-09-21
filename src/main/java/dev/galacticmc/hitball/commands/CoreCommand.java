package dev.galacticmc.hitball.commands;

import dev.galacticmc.hitball.HitBallPlugin;
import dev.galacticmc.hitball.util.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CoreCommand extends Command {

    private final ArrayList<SubCommand> subcommands;
    private final HitBallPlugin plugin;

    public CoreCommand(HitBallPlugin plugin, String name, String description, String usageMessage, List<String> aliases, ArrayList<SubCommand> subCommands) {
        super(name, description, usageMessage, aliases);
        this.subcommands = subCommands;
        this.plugin = plugin;
    }

    public ArrayList<SubCommand> getSubCommands() {
        return subcommands;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, String[] args) {

        if (args.length > 0) {
            for (int i = 0; i < getSubCommands().size(); i++) {
                SubCommand subCommand = getSubCommands().get(i);
                if (args[0].equalsIgnoreCase(subCommand.getName()) || (subCommand.getAliases() != null && subCommand.getAliases().contains(args[0]))) {
                    getSubCommands().get(i).perform(sender, Utils.removeElements(args, subCommand.getName()));
                }
            }
        } else {
            sendHelp(sender);
        }

        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("\n");
        sender.sendMessage("    HitBall version: " + plugin.getDescription().getVersion());
        sender.sendMessage("    Made with ♥ by nebulus4dev");
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, String[] args) throws IllegalArgumentException {
        if (args.length == 1) { //prank <subcommand> <args>
            ArrayList<String> subcommandsArguments = new ArrayList<>();

            //Does the subcommand autocomplete
            for (int i = 0; i < getSubCommands().size(); i++) {
                subcommandsArguments.add(getSubCommands().get(i).getName());
            }
            return subcommandsArguments;
        } else if (args.length >= 2) {
            for (int i = 0; i < getSubCommands().size(); i++) {
                if (args[0].equalsIgnoreCase(getSubCommands().get(i).getName())) {
                    List<String> subCommandArgs = getSubCommands().get(i).getSubcommandArguments(
                            (Player) sender, args
                    );

                    //getSubcommandArguments will have returned null if no implementation was provided.
                    if (subCommandArgs != null)
                        return subCommandArgs;

                    return Collections.emptyList();
                }
            }
        }

        return Collections.emptyList();
    }

}
