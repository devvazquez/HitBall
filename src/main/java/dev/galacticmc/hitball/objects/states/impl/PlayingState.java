package dev.galacticmc.hitball.objects.states.impl;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import dev.galacticmc.hitball.HitBallPlugin;
import dev.galacticmc.hitball.objects.HitBallPlayer;
import dev.galacticmc.hitball.objects.LangKey;
import dev.galacticmc.hitball.objects.states.BallEntity;
import dev.galacticmc.hitball.objects.states.GameState;
import dev.galacticmc.hitball.objects.states.SpawnLocations;
import dev.galacticmc.hitball.objects.states.StateManager;
import dev.galacticmc.hitball.util.Utils;
import dev.lone.itemsadder.api.CustomPlayer;
import dev.lone.itemsadder.api.CustomStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PlayingState implements GameState {

    private HitBallPlugin plugin;
    private StateManager stateManager;
    private World world;
    private SpawnLocations spawnLocations;
    private BukkitRunnable updateVelocityTask, checkCollisionsTask;
    private HitBallPlayer targeting;
    private HitBallPlayer lastTargeting;
    private BallEntity ball;
    private HashMap<UUID, Boolean> canRunSkill;

    private boolean running = false;
    private boolean skillAlreadyModifyingPlayerMovement = false;
    public boolean isSkillAlreadyModifyingPlayerMovement() {
        return skillAlreadyModifyingPlayerMovement;
    }

    // Game constants
    private double SPEED;
    private double MAX_SPEED;
    private double STEP;

    @Override
    public void onEnable(HitBallPlugin plugin, StateManager stateManager) {
        this.plugin = plugin;
        this.stateManager = stateManager;
        this.world = stateManager.getMiniGameWorld();
        this.spawnLocations = stateManager.getSpawnLocations();
        this.canRunSkill = new HashMap<>();
        stateManager.getPlayers().forEach(player -> {
            canRunSkill.put(player.getSelf().getUniqueId(), false);
        });

        this.ball = new BallEntity(spawnLocations.spawnBallLocation(), plugin);

        this.SPEED = 0.15D;
        this.MAX_SPEED = 1.5D;
        this.STEP = 0.06D;

        //Set all player to be in-game
        this.stateManager.getPlayers().forEach(HitBallPlayer::joinGame);
        this.targeting = getRandomOnlinePlayer();

        this.checkCollisionsTask = new BukkitRunnable() {
            @Override
            public void run() {
                if(running){
                    // Check player collisions
                    checkCollisions();
                }else {
                    cancel();
                }
            }
        };

        // Get locations
        // Calculate velocity
        // Check player collisions
        this.updateVelocityTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (running) {
                    // Get locations
                    Location ballLocation = ball.getLocation();
                    Location playerLocation = targeting.getSelf().getEyeLocation().add(0, -0.5, 0);

                    // Calculate velocity
                    Vector direction = playerLocation.toVector().subtract(ballLocation.toVector()).normalize();
                    direction.multiply(SPEED);

                    //Update it
                    ball.setVelocity(direction);
                }else {
                    cancel();
                }
            }
        };
        // Start the game
        this.running = true;

        //Run this code when the entity spawns.
        /*  If done just after BallEntity::spawn() line, it would be executed
         *  before since spawn() is executed safely using ThreadSafeMethods::runSafeLambda()
         *  which is executed in the next tick.
         */
        ball.whenAlive(() -> {
            //Set glowing the entity only for targeting.
            try {
                plugin.getGlowingEntities().setGlowing(ball.getEntity(), targeting.getSelf(), ChatColor.RED);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
            //Run the main task.
            updateVelocityTask.runTaskTimerAsynchronously(plugin, 0L, 0L);
            checkCollisionsTask.runTaskTimerAsynchronously(plugin, 0L, 0L);
        });

        //Spawn the ball
        ball.spawn(false);
    }

    private HitBallPlayer getRandomOnlinePlayer()   {
        List<HitBallPlayer> alive = stateManager.getPlayersFiltered(
                player -> player.getProperties().isAlive());
        plugin.getLogger().info("Obtained alive player list: " + alive.toString());
        int randomIndex = Utils.RANDOM.nextInt(alive.size());
        return alive.get(randomIndex);
    }

    private HitBallPlayer getRandomOnlinePlayer(HitBallPlayer notToBe) {
        List<HitBallPlayer> alive = stateManager.getPlayersFiltered(player ->
                player.getProperties().isAlive()
                && !player.equals(notToBe));
        int randomIndex = Utils.RANDOM.nextInt(alive.size());
        return alive.get(randomIndex);
    }

    private void endGame() {
        //Can't be called two times
        if(!running) return;

        try{
            updateVelocityTask.cancel();
            checkCollisionsTask.cancel();
        }catch (Exception e){
            plugin.getLogger().warning("Playing state tasks were already stopped.");
        }
        this.running = false;

        if(ball != null && targeting != null){
            try {
                plugin.getGlowingEntities().unsetGlowing(ball.getEntity(), targeting.getSelf());
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }

        //Thread-safe entity removal
        plugin.getThreadSafeMethods().runSafeLambda(() -> {
            ball.getEntity().remove();
        });

        stateManager.getPlayers().forEach(player -> {
            player.getProperties().reset();
        });

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            stateManager.getPlayers().forEach(player -> {
                stateManager.removePlayerFromSpawns(player.getSelf().getUniqueId());
                player.leaveGame();
                player.getSelf().sendMessage(LangKey.SPAWN_TP.formatted());
                player.getSelf().teleport(Bukkit.getWorld("hitballspawn").getSpawnLocation());
            });
            plugin.getThreadSafeMethods().runSafeLambda(() -> {
                stateManager.start();
            });
        }, 100L);
    }

    @Override
    public void onDisable() {
        // Do nothing
    }

    public void switchPlayer() {
        lastTargeting = targeting;
        if (targeting != null) {
            targeting = getRandomOnlinePlayer(targeting);
            if(targeting == null){
                plugin.getLogger().warning("Hubo un error seleccionando al proximo target.");
                endGame();
            }
            //Change the glow...
            try {
                plugin.getGlowingEntities().unsetGlowing(ball.getEntity(), lastTargeting.getSelf());
                plugin.getGlowingEntities().setGlowing(ball.getEntity(), targeting.getSelf(), ChatColor.RED);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void checkCollisions() {
        Location ballLocation = ball.getLocation();
        //Distance to real player head.
        double distance = ballLocation.distance(targeting.getSelf().getEyeLocation().subtract(0, 0.5, 0));
        boolean active = targeting.getProperties().isShieldActive();
        // If the ball is about to touch the player.
        if (!active && distance < 0.65) {
            handlePlayerElimination(); // Player is eliminated if they are the target and the ball collides with them.
        }else if (active && distance < 2) {
            bounceBall(); // Ball bounces off if it collides with a player's active shield.
        }
    }

    private void handlePlayerElimination() {
        HitBallPlayer killedPlayerHB = targeting;
        HitBallPlayer killerPlayerHB = lastTargeting;
        killedPlayerHB.getProperties().kill();

        Player killedPlayer = targeting.getSelf();
        boolean lastPlayer = stateManager.getPlayersFiltered(
                player -> player.getProperties().isAlive())
                .size() == 1;

        if (killerPlayerHB != null) {
            Player killerPlayer = lastTargeting.getSelf();
            if (killerPlayer.equals(killedPlayer)) {
                endGame();
            }
            handleKillerPlayer(killerPlayer, killedPlayer, lastPlayer);
            world.sendMessage(LangKey.PLAYER_KILL_CHAT.formatted(
                    "killer_player", killerPlayer.getName(),
                    "killed_player", killedPlayer.getName()));
        } else {
            handleNoKillerPlayer(killedPlayer, lastPlayer);
            world.sendMessage(LangKey.PLAYER_DEATH_CHAT.formatted());
        }

        killedPlayer.showTitle(Title.title(LangKey.DEATH_TITLE.formatted(), Component.empty()));
        killedPlayer.playSound(killedPlayer.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0f, 1.0f);

        //El jugador muere
        if (!lastPlayer) {
            switchPlayer();
            this.SPEED = 0.15D;
            ball.spawn(true);
        } else {
            endGame();
        }
    }

    private void handleKillerPlayer(Player killerPlayer, Player killedPlayer, boolean lastPlayer) {
        //Añadir la kill a la base de datos.
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.getDatabase().addKills(killerPlayer.getUniqueId(), 1));
        canRunSkill.replace(killerPlayer.getUniqueId(), true);
        if (lastPlayer) {
            announceWinner(killerPlayer);
            spawnFirework(killerPlayer);
        } else {
            killerPlayer.sendActionBar(LangKey.PLAYER_KILL_ACTION_BAR.formatted("killed_player", killedPlayer.getName()));
            killerPlayer.playSound(killerPlayer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        }
        killedPlayer.sendActionBar(LangKey.PLAYER_DEATH_ACTION_BAR.formatted("killer_player", killerPlayer.getName()));
    }

    private void handleNoKillerPlayer(Player killedPlayer, boolean lastPlayer) {
        killedPlayer.sendActionBar(LangKey.PLAYER_DEATH_ACTION_BAR_NO_KILLER.formatted());
        if (lastPlayer) {
            announceLastSurvivor(killedPlayer);
        }
    }

    private void announceWinner(Player player) {
        //Añadir la win y loss a la base de datos
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            stateManager.getPlayers().forEach(p -> {
                if (p.getSelf() == player) {
                    plugin.getDatabase().addWins(player.getUniqueId(), 1);
                }else {
                    plugin.getDatabase().addLosses(player.getUniqueId(), 1);
                }
                plugin.getDatabase().addGamesPlayed(player.getUniqueId(), 1);
            });
        });
        player.showTitle(Title.title(LangKey.WIN_TITLE.formatted(), Component.empty()));
        player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_TWINKLE, 1.0f, 1.0f);
    }

    private void announceLastSurvivor(Player player) {
        announceWinner(player);
        spawnFirework(player);
    }

    private void spawnFirework(Player player) {
        //Thread safe entity spawn
        plugin.getThreadSafeMethods().runSafeLambda(() -> {
            Firework firework = (Firework) player.getWorld().spawnEntity(player.getLocation().add(0, 5, 0), EntityType.FIREWORK);
            FireworkMeta meta = firework.getFireworkMeta();
            FireworkEffect effect = FireworkEffect.builder()
                    .with(FireworkEffect.Type.STAR)
                    .withColor(org.bukkit.Color.AQUA)
                    .withFade(org.bukkit.Color.YELLOW)
                    .withTrail()
                    .withFlicker()
                    .build();
            meta.addEffect(effect);
            meta.setPower(0);
            firework.setFireworkMeta(meta);
            Bukkit.getScheduler().runTaskLater(plugin, firework::detonate, 19L);
        });

    }

    @Override
    public void playerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        HitBallPlayer hitBallPlayer = plugin.getWorldManager().getHitBallPlayer(player);
        //Sword click
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK){
            //If player interact in range within the ball just bounce it and don't wait for the next tick.
            ItemStack inMainHand = player.getInventory().getItemInMainHand();
            if(CustomStack.byItemStack(inMainHand) == null) return;
            if (!hitBallPlayer.getProperties().isShieldActive()) {
                hitBallPlayer.getProperties().activateShield();
            }
        }
        //Skill click
        else if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK){
            if(!hitBallPlayer.hasSkill()) return;
            if(!player.getInventory().getItemInOffHand().equals(hitBallPlayer.getCurrentSkill().getIcon())) return;
            if(canRunSkill.get(player.getUniqueId())){
                event.setCancelled(true);
                hitBallPlayer.getCurrentSkill().executeSkill(hitBallPlayer, stateManager);
            }else {
                player.sendMessage(ChatColor.RED + "Elimina primero a alguien para utilizar la habilidad!");
            }
        }

    }

    @Override
    public void playerJoin(Player player) {
        player.performCommand("spawn");
        player.sendMessage(LangKey.GAME_FULL.formatted());
    }

    @Override
    public void playerLeave(Player player) {
        // Remove the player's spawn location
        stateManager.removePlayerFromSpawns(player.getUniqueId());

        if(running){
            // Stop game leave message
            world.sendMessage(LangKey.PLAYER_LEAVE_STOP.formatted("player_name", player.getName()));
            endGame();
        }
    }

    @Override
    public void playerMove(PlayerMoveEvent event) {
        // Do nothing
    }

    @EventHandler
    public void fallingBlockDie(EntityRemoveFromWorldEvent event) {
        if (event.getEntity() == ball.getEntity() && running) {
            ball.spawn(false);
        }
    }

    private void bounceBall() {
        targeting.getProperties().deactivateShield();
        switchPlayer();
        if(SPEED > MAX_SPEED) return;
        this.SPEED += STEP;
    }

    /*
        - SKILLS -
     */

    public void revivePlayer(HitBallPlayer player){
        if(!running) return;
        player.getProperties().reset();
        //player.teleport(stateManager.getSpawnLocations().getPlayersSpawns());
        switchPlayer();
        this.SPEED = 0.15D;
        ball.spawn(true);
    }

    public void slowDownGame(HitBallPlayer notToBeAffected) {
        double preSpeed = this.SPEED;

        doubleSyncDelayedMovementModification(
            () -> {
                for(HitBallPlayer player : stateManager.getPlayers()){
                    if(player.equals(notToBeAffected)) continue;
                    player.getSelf().setWalkSpeed(0.1f);
                }
                this.SPEED = 0.15D;
            }, () -> {
                for(HitBallPlayer player : stateManager.getPlayers()){
                    if(player.equals(notToBeAffected)) continue;
                    player.getSelf().setWalkSpeed(0.2f);
                }
                this.SPEED = preSpeed;
                skillAlreadyModifyingPlayerMovement = false;
            }, 20 * 5);

    }

    public void lockPlayers(HitBallPlayer notToLock) {
        doubleSyncDelayedMovementModification(
            () -> {
                for(HitBallPlayer player : stateManager.getPlayers()){
                    if(player.equals(notToLock)) continue;
                    player.getSelf().setWalkSpeed(0f);
                    player.getSelf().addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 1000000, 250));
                }
            },
            () -> {
                for(HitBallPlayer player : stateManager.getPlayers()){
                    if(player.equals(notToLock)) continue;
                    player.getSelf().setWalkSpeed(0.2f);
                    player.getSelf().removePotionEffect(PotionEffectType.JUMP);
                }
            }, 20);
    }

    //Movement skills method
    private void doubleSyncDelayedMovementModification(Runnable instant, Runnable delayed, int delayedTicks){
        Runnable moodifiedInstant = () -> {
            skillAlreadyModifyingPlayerMovement = true;
            instant.run();
        };
        plugin.getThreadSafeMethods().runSafeLambda(moodifiedInstant);
        Runnable modifiedDelayed = () -> {
            skillAlreadyModifyingPlayerMovement = false;
            delayed.run();
        };
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, modifiedDelayed, delayedTicks);
    }

    public void turnInvisiblePlayer(HitBallPlayer player) {
        player.getSelf().setInvisible(true);
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> player.getSelf().setInvisible(false), 20 * 7);
    }
}
