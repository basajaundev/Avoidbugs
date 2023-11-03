package com.github.basajaundev.avoidbugs;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BookMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemMgr {

    Avoidbugs plugin;

    public ItemMgr(Avoidbugs plugin) {
        this.plugin = plugin;
    }

    ConfigMgr getConfig() {
        return plugin.configMgr;
    }

    public boolean processItem(ItemStack item, PlayerInventory sourceInventory, boolean dontCheckContains) {
        if (!isActualItem(item)) return false;
        if (!dontCheckContains && !sourceInventory.contains(item)) return false;
        if (!itemExceedsLimit(item)) return false;

        return processItemInSlot(getSlot(item, sourceInventory), sourceInventory);
    }

    public boolean processItem(ItemStack item, PlayerInventory sourceInventory) {
        return processItem(item, sourceInventory, false);
    }

    public boolean processItem(ItemStack item, Player player) {
        return processItem(item, player.getInventory());
    }

    public boolean processItemInSlot(int slot, PlayerInventory inventory) {
        ItemStack item;
        if (slot == -1) item = inventory.getItemInOffHand();
        else item = inventory.getItem(slot);
        if (!isActualItem(item)) return false;
        if (!itemExceedsLimit(item)) return false;

        if (getConfig().getMode().equals("remove")) {
            removeItem(inventory, slot);
        } else if (getConfig().getMode().equals("clearmeta")) {
            ItemStack newItem = getWithoutMeta(item);
            if (slot == -1) inventory.setItemInOffHand(newItem);
            inventory.setItem(slot, newItem);
        }

        return true;
    }

    public int processPlayer(Player player) {
        int res = 0;
        PlayerInventory inv = player.getInventory();

        for (int i = -1; i < inv.getSize(); i++) {
            if (processItemInSlot(i, inv)) res++;
        }

        return res;
    }

    public boolean bookExceedsPageLimit(BookMeta meta) {
        return meta.getPageCount() > getConfig().getMaxBookPages();
    }

    public BookMeta processBookMeta(BookMeta meta) {
        if (!bookExceedsPageLimit(meta)) return meta;

        List<String> pages = meta.getPages();
        List<String> newPages = new ArrayList<>();

        for (int i = 0; i < meta.getPageCount() - 1; i++) {
            if (i + 1 <= getConfig().getMaxBookPages()) {
                newPages.add(pages.get(i));
            }
        }

        meta.setPages(newPages);

        return meta;
    }

    public static boolean isBook(ItemStack item) {
        return item.getType() == Material.WRITABLE_BOOK || item.getType() == Material.WRITTEN_BOOK;
    }

    public boolean itemExceedsLimit(ItemStack item) {
        return getSize(item) > getConfig().getMaxDataSize();
    }

    public boolean itemExceedsLimit(Item itemEntity) {
        return itemExceedsLimit(itemEntity.getItemStack());
    }

    public static int getSize(ConfigurationSerializable serializable) {
        return serializable.serialize().toString().getBytes().length;
    }

    public static ItemStack getItemInSlot(PlayerInventory inventory, int slot) {
        if (slot == -1) return inventory.getItemInOffHand();

        return inventory.getItem(slot);
    }

    public static ItemStack getItemSlot(Player player, int slot) {
        return getItemInSlot(player.getInventory(), slot);
    }

    public static boolean hasItem(PlayerInventory inventory, ItemStack item) {
        if (inventory.getItemInOffHand().equals(item)) return true;
        return inventory.contains(item);
    }

    public static void removeItem(PlayerInventory inventory, ItemStack item) {
        if (inventory.getItemInOffHand().equals(item)) inventory.setItemInOffHand(null);
        inventory.remove(item);
    }

    public static void removeItem(PlayerInventory inventory, int slot) {
        if (slot == -1) inventory.setItemInOffHand(null);
        else inventory.setItem(slot, null);
    }

    public static ItemStack getWithoutMeta(ItemStack item) {
        return new ItemStack(item.getType());
    }

    public static void setWithoutMeta(ItemStack item, PlayerInventory inventory, int slot) {
        if (item.getItemMeta() == null) return;
        ItemStack newItem = getWithoutMeta(item);

        if (slot == -1) {
            inventory.setItemInOffHand(newItem);
        } else {
            inventory.setItem(slot, newItem);
        }
    }

    public static boolean isActualItem(Material material) {
        return material != null && !material.equals(Material.AIR);
    }

    public static boolean isActualItem(ItemStack item) {
        if (item == null) return false;
        return isActualItem(item.getType());
    }

    public static int getSlot(ItemStack item, PlayerInventory inventory) {
        for (int i = -1; i < inventory.getSize(); i++) {
            ItemStack it = getItemInSlot(inventory, i);
            if (it != null && it.equals(item)) return i;
        }

        return -2;
    }
}
