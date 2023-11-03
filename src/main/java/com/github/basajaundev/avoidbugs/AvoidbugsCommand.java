package com.github.basajaundev.avoidbugs;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AvoidbugsCommand implements CommandExecutor, TabCompleter {

    public Avoidbugs plugin;

    public AvoidbugsCommand(Avoidbugs plugin) {
        this.plugin = plugin;
    }

    public ConfigMgr getConfig() {
        return plugin.configMgr;
    }

    public void sendMsg(String message, CommandSender sender) {
        sender.sendMessage(Utils.format(message));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return null;
    }

}
