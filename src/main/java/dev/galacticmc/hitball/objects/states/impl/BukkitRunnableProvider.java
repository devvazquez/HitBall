package dev.galacticmc.hitball.objects.states.impl;

import dev.galacticmc.hitball.objects.LangKey;
import dev.galacticmc.hitball.objects.states.StateManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;

public class BukkitRunnableProvider {

    private final StateManager stateManager;

    public BukkitRunnableProvider(StateManager stateManager) {
        this.stateManager = stateManager;
    }

    public static BukkitRunnableProvider manageWith(StateManager stateManager){
        return new BukkitRunnableProvider(stateManager);
    }

    public BukkitRunnable waitingState(){
        World world = this.stateManager.getMiniGameWorld();
        int minPlayers = this.stateManager.getMinPlayers();
        int maxPlayers = this.stateManager.getMaxPlayers();
        return new BukkitRunnable() {
            int segundos = 30;

            @Override
            public void run() {
                //Unos mensages para orientar a la gente.
                if (segundos == 30) {
                    world.sendMessage(LangKey.WAITING_30.formatted());
                    world.getPlayers().forEach(player -> {
                        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1.0f, 1.0f);
                    });
                } else if (segundos == 15) {
                    world.sendMessage(LangKey.WAITING_15.formatted());
                    world.getPlayers().forEach(player -> {
                        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1.0f, 1.0f);
                    });
                } else if (segundos <= 5 && segundos > 0) {
                    world.sendMessage(LangKey.WAITING_5.formatted("segundos", String.valueOf(segundos)));
                    world.getPlayers().forEach(player -> {
                        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1.0f, 1.0f);
                    });
                }

                //Mostrar el titulo
                //String.format("%d/%d", world.getPlayers().size(), minPlayers)
                world.getPlayers().forEach(player -> {
                    player.showTitle(Title.title(
                            LangKey.WAITING_TITLE.formatted().color(TextColor.fromCSSHexString("#FF9700")),
                            Component.text(ChatColor.translateAlternateColorCodes('&',
                                    String.format("&a%d &f&l|&r %d &f&l|&r &c%d", minPlayers, world.getPlayers().size(), maxPlayers))),
                            Title.Times.of(
                                    Duration.ZERO,
                                    Duration.ofSeconds(2),
                                    Duration.ofSeconds(1)
                            )
                    ));
                });

                if (segundos <= 0) {
                    if (world.getPlayers().size() >= minPlayers) {
                        //Cancel this task
                        cancel();
                        stateManager.nextGameState(new CountDownState());
                    } else {
                        world.sendMessage(LangKey.RESTORE_COUNTDOWN.formatted());
                        segundos = 30;
                    }
                } else if (world.getPlayers().size() >= stateManager.getMaxPlayers()) {
                    cancel();
                    stateManager.nextGameState(new CountDownState());
                }

                segundos--;
            }
        };
    }

    public BukkitRunnable countDownState(){
        World world = stateManager.getMiniGameWorld();
        return new BukkitRunnable() {
            int segundos = 10;
            String colorSegundos = "#34F251";

            @Override
            public void run() {
                if (segundos <= 0) {
                    //Iniciar el juego
                    world.getPlayers().forEach(player -> {
                        player.clearTitle();
                        player.showTitle(Title.title(LangKey.GAME_START.formatted().color(TextColor.fromCSSHexString("#FFE800")),
                                Component.empty()));
                        player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 1.0f);
                    });
                    cancel();
                    stateManager.nextGameState(new PlayingState());
                } else if (segundos <= 3) {
                    //Ultimos tres segundos
                    colorSegundos = "#FF0000";
                    world.getPlayers().forEach(player -> {
                        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1.0f, 1.0f);
                        player.showTitle(Title.title(LangKey.GAME_COUNTDOWN.formatted().color(TextColor.fromCSSHexString("#34F2D8")),
                                Component.text(segundos).color(TextColor.fromCSSHexString(colorSegundos))));
                    });
                } else if (segundos <= 10) {
                    //Primeros 7 segundos.
                    world.getPlayers().forEach(player -> {
                        player.showTitle(Title.title(LangKey.GAME_COUNTDOWN.formatted().color(TextColor.fromCSSHexString("#34F2D8")),
                                Component.text(segundos).color(TextColor.fromCSSHexString(colorSegundos))));
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
                    });
                }
                segundos--;
            }
        };
    }

}
