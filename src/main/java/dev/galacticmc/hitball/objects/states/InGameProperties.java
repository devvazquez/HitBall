package dev.galacticmc.hitball.objects.states;

import dev.galacticmc.hitball.HitBallPlugin;
import dev.galacticmc.hitball.objects.LangKey;
import dev.galacticmc.hitball.objects.swords.Sword;
import dev.lone.itemsadder.api.CustomStack;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class InGameProperties {

    private final HitBallPlugin plugin;
    private final HitBallPlayer player;

    private boolean dead = false;
    private boolean shielded = false;
    private final BossBar bossBar;
    private final ItemStack chestPlate;

    private static final long SHIELD_DURATION = 3000; // 3 seconds
    private long DEACTIVATE_SHIELD = 0;

    public InGameProperties(HitBallPlayer player, HitBallPlugin plugin) {
        this.player = player;
        this.plugin = plugin;
        this.bossBar = Bukkit.createBossBar(LangKey.SHIELD_ACTIVE.value, BarColor.BLUE, BarStyle.SOLID);
        this.chestPlate =  new ItemStack(Material.LEATHER_CHESTPLATE);

        LeatherArmorMeta meta = (LeatherArmorMeta) chestPlate.getItemMeta();
        meta.setColor(Color.WHITE);
        meta.displayName(Component.text("Escudo"));
        chestPlate.setItemMeta(meta);

        ItemStack sword = CustomStack.getInstance("the_sword_of_the_storm").getItemStack();
        player.getSelf().getInventory().setItem(4, sword);
        if(player.hasSkill()){
            player.getSelf().getInventory().setItemInOffHand(player.getCurrentSkill().getIcon());
        }
    }

    public boolean isAlive() {
        return !dead;
    }

    public void kill() {
        this.dead = true;
        devisualizeShield();
        enableFakeSpectatorMode();
    }

    public void reset() {
        // Reset player state
        this.dead = false;
        devisualizeShield();
        disableFakeSpectatorMode();
    }

    public boolean isShieldActive() {
        return shielded;
    }

    public void activateShield() {
        this.shielded = true;
        visualizeShield();
        showShieldBossBar();
        this.DEACTIVATE_SHIELD = System.currentTimeMillis() + SHIELD_DURATION;
        new BukkitRunnable() {
            @Override
            public void run() {
                if(!shielded){
                    //No cooldown
                    cancel();
                    hideShieldBossBar();
                    DEACTIVATE_SHIELD = 0;
                }
                long remainingTime = DEACTIVATE_SHIELD - System.currentTimeMillis();
                if (remainingTime > 0) {
                    bossBar.setProgress(Math.max((double) remainingTime / SHIELD_DURATION, 0.0D));
                } else {
                    //Shield didn't get used in 3s.
                    cancel();
                    hideShieldBossBar();
                    DEACTIVATE_SHIELD = 0;
                }
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }

    public void deactivateShield() {
        this.shielded = false;
        bossBar.removePlayer(player.getSelf());
        bossBar.setVisible(false);
        devisualizeShield();
    }

    // Add a player head helmet with a custom texture value
    private void visualizeShield() {
        player.getSelf().getInventory().setChestplate(chestPlate);
    }

    private void devisualizeShield() {
        player.getSelf().getInventory().setChestplate(null);
    }

    public void showShieldBossBar() {
        bossBar.addPlayer(player.getSelf());
        bossBar.setVisible(true);
    }

    public void hideShieldBossBar() {
        bossBar.removePlayer(player.getSelf());
        bossBar.setVisible(false);
    }

    public void enableFakeSpectatorMode() {
        // Make player invisible
        player.getSelf().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, false, false));

        // Allow flying and set fly speed
        player.getSelf().setAllowFlight(true);
        player.getSelf().setFlying(true);
        player.getSelf().setFlySpeed(0.1f);

        // Disable damage
        player.getSelf().setInvulnerable(true);

        // Hide the player from all other players
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.hidePlayer(plugin, player.getSelf());
        }
    }

    public void disableFakeSpectatorMode() {
        // Remove invisibility
        player.getSelf().removePotionEffect(PotionEffectType.INVISIBILITY);

        // Disable flying
        player.getSelf().setAllowFlight(false);
        player.getSelf().setFlying(false);

        // Enable damage
        player.getSelf().setInvulnerable(false);

        // Show the player to all other players
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.showPlayer(plugin, player.getSelf());
        }
    }

}
