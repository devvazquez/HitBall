package dev.galacticmc.hitball.commands.impl;

import dev.galacticmc.hitball.HitBallPlugin;
import dev.galacticmc.hitball.commands.SubCommand;
import dev.galacticmc.hitball.objects.HitBallPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.xenondevs.invui.window.Window;

import java.util.List;

public class SwordsCommand extends SubCommand {

    public SwordsCommand(HitBallPlugin plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "espadas";
    }

    @Override
    public List<String> getAliases() {
        return List.of();
    }

    @Override
    public String getDescription() {
        return "Abre un menu para ver tus espadas y selecionarlas.";
    }

    @Override
    public String getSyntax() {
        return "/espadas";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if(!(sender instanceof Player player)) return;
        HitBallPlayer hitBallPlayer = plugin.getWorldManager().getHitBallPlayer(player);
        Window swordsWindow = plugin.getGuiProvider().provideSwordsWindow(hitBallPlayer);
        swordsWindow.open();
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return List.of();
    }
}
