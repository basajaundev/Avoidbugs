package com.github.basajaundev.avoidbugs;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.lang.reflect.Method;
import java.util.Map;

public class Utils {

    public static String format(String text) {
        if (text == null) return "";
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static String replaceMultiple(String str, Map<String, String> replacements) {
        for (Map.Entry<String, String> e : replacements.entrySet()) {
            str = str.replace(e.getKey(), e.getValue());
        }

        return str;
    }

    public static void reAddHandItem(ItemStack item, Player player) {
        if (item == null) return;

        PlayerInventory inv = player.getInventory();
        ItemStack im = inv.getItemInMainHand();
        ItemStack io = inv.getItemInOffHand();

        if (im != null && item.isSimilar(im)) {
            inv.setItemInMainHand(null);
            inv.setItemInMainHand(item);
            return;
        }

        if (io != null && item.isSimilar(io)) {
            inv.setItemInOffHand(null);
            inv.setItemInOffHand(item);
            return;
        }

        inv.remove(item);
        inv.addItem(item);
    }

    public static boolean hasMethod(Object object, String methodName) {
        Method[] methods = object.getClass().getMethods();

        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                return true;
            }
        }

        return false;
    }
}
