package com.github.basajaundev.avoidbugs;

import com.github.basajaundev.avoidbugs.commands.MainCommand;
import com.github.basajaundev.avoidbugs.commands.DataSizeCommand;
import com.github.basajaundev.avoidbugs.listeners.PlayerEvents;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public final class Avoidbugs extends JavaPlugin {

    public ConfigMgr configMgr = new ConfigMgr(this);
    public ItemMgr itemMgr = new ItemMgr(this);
    public PlayerEvents playerEvents = new PlayerEvents(this);
    public HashMap<String, Integer> dupeFixCountdown = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        configMgr.load();
        getServer().getPluginManager().registerEvents(playerEvents, this);

        registerCommand("avoidbugs", new MainCommand(this));
        registerCommand("datasize", new DataSizeCommand(this));

        getLogger().info("Running");
    }

    @Override
    public void onDisable() {
        getLogger().info("Shutting down");
    }

    public void registerCommand(String name, AvoidbugsCommand handler) {
        PluginCommand command = getCommand(name);
        command.setExecutor(handler);
        if (Utils.hasMethod(handler, "onTabComplete")) command.setTabCompleter(handler);
    }
}
