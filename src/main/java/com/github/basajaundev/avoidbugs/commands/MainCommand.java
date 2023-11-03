package com.github.basajaundev.avoidbugs.commands;

import com.github.basajaundev.avoidbugs.Avoidbugs;
import com.github.basajaundev.avoidbugs.AvoidbugsCommand;
import com.github.basajaundev.avoidbugs.ConfigMgr;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainCommand extends AvoidbugsCommand {

    class Action {
        public String[] aliases;
        public String description;
        public String permissions;

        public Action(String[] aliases, String description, String permissions) {
            this.aliases = aliases;
            this.description = description;
            this.permissions = permissions;
        }

        public Action(String alias, String description, String permissions) {
            this(new String[]{alias}, description, permissions);
        }

        public Action(String alias, String description) {
            this(new String[]{alias}, description, null);
        }

        public Action(String aliases[], String description) {
            this(aliases, description, null);
        }

        public String getPrimaryAlias() {
            return aliases[0];
        }

        public String aliasesAsString() {
            if (aliases.length == 1) return getPrimaryAlias();
            String str = "[";
            for (int i = 0; i < aliases.length; i++) {
                if (i != 0) str += ",";
                str += aliases[i];
            }
            str += "]";
            return str;
        }

        public boolean matches(String alias) {
            return Arrays.asList(aliases).contains(alias);
        }
    }

    Action[] actions = {
            new Action("reload", "Reloads the plugin's config.", "reload"),
    };

    public Action getAction(String alias) {
        for (Action a : actions) {
            if (a.matches(alias)) return a;
        }

        return null;
    }

    public MainCommand(Avoidbugs plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendMsg("&6&lAvoidbugs", sender);
            sendMsg("&cby &4GallaZ", sender);
            sendMsg("&b&nhttps://github.com/basajaundev/Avoidbugs", sender);
            return true;
        }

        String strAction = args[0];
        Action action = getAction(strAction);

        if (action == null) {
            sendMsg("&cUnrecognized option '" + strAction + "'", sender);
            return true;
        }

        if (action.permissions != null && !ConfigMgr.hasPermission(action.permissions, sender)) {
            sendMsg(getConfig().getMessage("noPermission"), sender);
            return true;
        }

        if (action.getPrimaryAlias().equals("reload")) {
            getConfig().reload();
            sendMsg("&aConfig reloaded", sender);
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> res = new ArrayList<>();

        if (args.length == 1) {
            for (Action a: actions) {
                for (String als : a.aliases) {
                    res.add(als);
                }
            }

            return res;
        }

        return res;
    }
}
