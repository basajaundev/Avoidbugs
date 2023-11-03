package com.github.basajaundev.avoidbugs.commands;

import com.github.basajaundev.avoidbugs.Avoidbugs;
import com.github.basajaundev.avoidbugs.AvoidbugsCommand;
import com.github.basajaundev.avoidbugs.ConfigMgr;
import com.github.basajaundev.avoidbugs.ItemMgr;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DataSizeCommand extends AvoidbugsCommand {

    public DataSizeCommand(Avoidbugs plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sendMsg("This command can only be run by a player", sender);
            return true;
        }

        if (!ConfigMgr.hasPermission("size", sender)) {
            sendMsg(getConfig().getMessage("noPermission"), sender);
            return true;
        }

        Player player = (Player) sender;
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item == null) {
            sendMsg(getConfig().getMessage("notHolding"), sender);
            return true;
        }

        sendMsg(getConfig().getMessage("size")
                .replace("{itemName}", item.getType().toString())
                .replace("{size}", String.valueOf(ItemMgr.getSize(item)))
                , sender);

        return true;
    }
}
