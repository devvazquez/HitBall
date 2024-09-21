package dev.galacticmc.hitball;

import com.google.common.collect.Lists;
import dev.galacticmc.hitball.commands.CoreCommand;
import dev.galacticmc.hitball.commands.SubCommand;
import dev.galacticmc.hitball.commands.impl.*;
import dev.galacticmc.hitball.objects.Database;
import dev.galacticmc.hitball.objects.GlowingEntities;
import dev.galacticmc.hitball.objects.HitBallExpansion;
import dev.galacticmc.hitball.objects.ThreadSafeMethods;
import dev.galacticmc.hitball.objects.gui.GuiProvider;
import dev.galacticmc.hitball.objects.managers.ConfigManager;
import dev.galacticmc.hitball.objects.managers.LanguageManager;
import dev.galacticmc.hitball.objects.managers.WorldManager;
import dev.galacticmc.hitball.objects.skills.SkillManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;

public final class HitBallPlugin extends JavaPlugin {

    private SkillManager skillManager;
    public SkillManager getSkillManager() {
        return skillManager;
    }

    private WorldManager worldManager;
    public WorldManager getWorldManager() {
        return worldManager;
    }

    private ConfigManager configManager;
    public ConfigManager getConfigManager() {
        return configManager;
    }

    private Database database;
    public Database getDatabase() {
        return database;
    }

    private ThreadSafeMethods threadSafeMethods;
    public ThreadSafeMethods getThreadSafeMethods(){
        return threadSafeMethods;
    }

    private GlowingEntities glowingEntities;
    public GlowingEntities getGlowingEntities() {
        return glowingEntities;
    }

    private GuiProvider guiProvider;
    public GuiProvider getGuiProvider(){
        return guiProvider;
    }

    /*
        TODO:
            - Add translation keys
            - Add new skills (works with permissions.)
     */

    @Override
    public void onEnable() {
        // Save the 'config.yml' file.
        saveDefaultConfig();

        new LanguageManager(this);

        // Initialize managers
        this.skillManager = new SkillManager(this);
        this.worldManager = new WorldManager(this);
        this.configManager = new ConfigManager(this);
        getServer().getPluginManager().registerEvents(worldManager, this);
        this.database = new Database(this);
        this.threadSafeMethods = new ThreadSafeMethods(this);
        this.glowingEntities = new GlowingEntities(this);
        this.guiProvider = new GuiProvider(this);

        // Add commands
        try {
            addCommand(
                    GenerateCrateRewardCommand.class,
                    PlayerSpawnPositionCommand.class,
                    BallSpawnPositionCommand.class,
                    ReloadSubCommand.class,
                    FindGameCommand.class,
                    SkillsCommand.class,
                    StatsCommand.class
            );
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        //Check for PAPI plugin.
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            this.getLogger().fine("HitBall will provide variables for PAPI.");
            // Register the PAPI extension
            new HitBallExpansion(this).register();
        } else {
            this.getLogger().warning("HitBall won't provide PAPI variables as it does not exist.");
        }

        //Check for ItemsAdder plugin
        if(Bukkit.getPluginManager().getPlugin("ItemsAdder") != null){
            this.getLogger().fine("HitBall has recognized ItemsAdder and will work with it.");
        }else {
            this.getLogger().severe("ItemsAdder plugin was nor found under pl name: 'ItemsAdder', as a crucial part of HitBall, it'll be disabling...");
            throw new UnsupportedOperationException("ItemsAdder plugin was not found!");
        }

    }

    @SafeVarargs
    private void addCommand(Class<? extends SubCommand>... subcommands) throws NoSuchFieldException, IllegalAccessException {
        ArrayList<SubCommand> commands = new ArrayList<>(); // Reusing var
        Arrays.stream(subcommands).map(subcommand -> { // Initialize subcommands passing this plugin as an arg.
            try {
                Constructor<? extends SubCommand> constructor = subcommand.getConstructor(HitBallPlugin.class);
                return constructor.newInstance(this);
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                     InvocationTargetException e) {
                e.printStackTrace();
            }
            return null;
        }).forEach(commands::add);
        // Add main command to Minecraft mapping
        Field commandField = this.getServer().getClass().getDeclaredField("commandMap");
        commandField.setAccessible(true);
        CommandMap commandMap = (CommandMap) commandField.get(this.getServer());
        String commandName = "hitball";
        // Theoretically registered under 'minecraft' namespace.
        commandMap.register(commandName, new CoreCommand(
                this,
                commandName,
                "The main command for HitBall",
                "/" + commandName + " <subcommand>",
                Lists.newArrayList("of"),
                commands));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        glowingEntities.disable();
    }

}
