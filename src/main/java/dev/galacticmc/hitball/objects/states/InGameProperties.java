package dev.galacticmc.hitball.objects.states;

import dev.galacticmc.hitball.HitBallPlugin;
import dev.galacticmc.hitball.objects.LangKey;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class InGameProperties {

    private final HitBallPlugin plugin;

    private boolean dead = false;
    private boolean shielded = false;
    private BossBar bossBar;

    private static final long SHIELD_DURATION = 3000; // 3 seconds
    private long DEACTIVATE_SHIELD = 0;

    public InGameProperties(HitBallPlayer player) {
        this.plugin = player.getPlugin();
        this.bossBar = Bukkit.createBossBar(LangKey.SHIELD_ACTIVE.value, BarColor.BLUE, BarStyle.SOLID);
    }

    public boolean isAlive() {
        return !dead;
    }

    public void kill() {
        this.dead = true;
        removePlayerHeadHelmet();
        enableFakeSpectatorMode();
    }

    public void reset() {
        // Reset player state
        this.dead = false;
        removePlayerHeadHelmet();
        disableFakeSpectatorMode();
    }

    public boolean isShieldActive() {
        return shielded;
    }

    public void activateShield() {
        this.shielded = true;
        addPlayerHeadHelmet();
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
        bossBar.removePlayer(getSelf());
        bossBar.setVisible(false);
        removePlayerHeadHelmet();
    }

    // Add a player head helmet with a custom texture value
    private void addPlayerHeadHelmet() {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD); // Create a new ItemStack of the Player Head type.
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta(); // Get the created item's ItemMeta and cast it to SkullMeta so we can access the skull properties
        skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer("Triumphus")); // Set the skull's owner so it will adapt the skin of the provided username (case sensitive).
        skull.setItemMeta(skullMeta); // Apply the modified meta to the initial created item
        getSelf().getInventory().setHelmet(skull);
    }

    private void removePlayerHeadHelmet() {
        getSelf().getInventory().setHelmet(new ItemStack(Material.AIR));
    }

    public void showShieldBossBar() {
        bossBar.addPlayer(getSelf());
        bossBar.setVisible(true);
    }

    public void hideShieldBossBar() {
        bossBar.removePlayer(getSelf());
        bossBar.setVisible(false);
    }

    public void enableFakeSpectatorMode() {
        // Make player invisible
        getSelf().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, false, false));

        // Allow flying and set fly speed
        getSelf().setAllowFlight(true);
        getSelf().setFlying(true);
        getSelf().setFlySpeed(0.1f);

        // Disable damage
        getSelf().setInvulnerable(true);

        // Hide the player from all other players
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.hidePlayer(plugin, getSelf());
        }
    }

    public void disableFakeSpectatorMode() {
        // Remove invisibility
        getSelf().removePotionEffect(PotionEffectType.INVISIBILITY);

        // Disable flying
        getSelf().setAllowFlight(false);
        getSelf().setFlying(false);

        // Enable damage
        getSelf().setInvulnerable(false);

        // Show the player to all other players
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.showPlayer(plugin, getSelf());
        }
    }

}
