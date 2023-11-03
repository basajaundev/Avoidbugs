package com.github.basajaundev.avoidbugs.listeners;

import com.github.basajaundev.avoidbugs.ConfigMgr;
import com.github.basajaundev.avoidbugs.Avoidbugs;
import com.github.basajaundev.avoidbugs.ItemMgr;
import com.github.basajaundev.avoidbugs.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class PlayerEvents implements Listener {

    Avoidbugs plugin;

    public PlayerEvents(Avoidbugs plugin) {
        this.plugin = plugin;
    }

    public ConfigMgr getConfig() {
        return plugin.configMgr;
    }

    public ItemMgr getItemMgr() {
        return plugin.itemMgr;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (!getConfig().getItemScanningFeatureEnabled("scanOnJoin")) return;
        Player player = e.getPlayer();
        int count = getItemMgr().processPlayer(player);

        if (count > 0) {
            player.sendMessage(getConfig().getMessage("itemsRemoved").replace("{count}", String.valueOf(count)));
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!getConfig().getItemScanningFeatureEnabled("scanOnInventoryClick")) return;

        if (e.getAction() == InventoryAction.NOTHING) return;
        if (e.getAction() == InventoryAction.COLLECT_TO_CURSOR) return;
        if (e.getAction() == InventoryAction.DROP_ALL_CURSOR) return;
        if (e.getAction() == InventoryAction.DROP_ALL_SLOT) return;
        if (e.getAction() == InventoryAction.DROP_ONE_CURSOR) return;
        if (e.getAction() == InventoryAction.DROP_ONE_SLOT) return;

        ItemStack current = e.getCurrentItem();
        if (current == null) return;
        Player player = (Player) e.getWhoClicked();

        if (getItemMgr().itemExceedsLimit(current)) {
            getConfig().sendItemRemoved(player, current);
            e.setCurrentItem(null);
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (!getConfig().getItemScanningFeatureEnabled("scanOnInteract")) return;
        ItemStack item = e.getItem();
        if (item == null) return;
        Player player = e.getPlayer();

        if (getItemMgr().processItem(item, player)) {
            getConfig().sendItemRemoved(player, item);
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityPickupItem(EntityPickupItemEvent e) {
        if (!getConfig().getItemScanningFeatureEnabled("scanOnPickup")) return;
        Entity entity = e.getEntity();
        if (!(entity instanceof Player)) return;
        Player player = (Player) entity;
        Item itemEntity = e.getItem();

        if (getItemMgr().itemExceedsLimit(itemEntity)) {
            player.sendMessage(getConfig().getMessage("cantPickup").replace("{itemName}", itemEntity.getItemStack().getType().toString()));
            if (getConfig().getMode().equals("remove")) {
                itemEntity.remove();
                e.setCancelled(true);
            } else if (getConfig().getMode().equals("clearmeta")) {
                itemEntity.setItemStack(ItemMgr.getWithoutMeta(itemEntity.getItemStack()));
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        if (!getConfig().getItemScanningFeatureEnabled("scanOnDrop")) return;
        Player player = e.getPlayer();
        Item itemEntity = e.getItemDrop();

        if (getItemMgr().itemExceedsLimit(itemEntity)) {
            player.sendMessage(getConfig().getMessage("cantDrop").replace("{itemName}", itemEntity.getItemStack().getType().toString()));
            if (getConfig().getMode().equals("remove")) {
                itemEntity.remove();
            } else if (getConfig().getMode().equals("clearmeta")) {
                itemEntity.setItemStack(ItemMgr.getWithoutMeta(itemEntity.getItemStack()));
            }
        }
    }

    @EventHandler
    public void onPlayerEditBook(PlayerEditBookEvent e) {
        if (!getConfig().getFeatureEnabled("bookTrimming")) return;
        Player player = e.getPlayer();
        BookMeta newMeta = e.getNewBookMeta();
        ItemStack item = ItemMgr.getItemInSlot(player.getInventory(), e.getSlot());

        if (getItemMgr().bookExceedsPageLimit(newMeta)) {
            BookMeta processedMeta = getItemMgr().processBookMeta(newMeta);
            e.setNewBookMeta(processedMeta);
            item.setItemMeta(processedMeta);
            Utils.reAddHandItem(item, player);
            player.sendMessage(getConfig().getMessage("bookTrimmed")
                    .replace("{max}", String.valueOf(getConfig().getMaxBookPages()))
            );
            e.setCancelled(true);
        }

        if (getConfig().getItemScanningFeatureEnabled("scanOnBookEdit")) {
            item = ItemMgr.getItemInSlot(player.getInventory(), e.getSlot());
            if (getItemMgr().itemExceedsLimit(item)) {
                if (getConfig().getMode().equals("remove")) {
                    ItemMgr.removeItem(player.getInventory(), e.getSlot());
                } else if (getConfig().getMode().equals("clearmeta")) {
                    player.getInventory().setItem(e.getSlot(), ItemMgr.getWithoutMeta(item));
                }

                getConfig().sendItemRemoved(player, item);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onShear(PlayerShearEntityEvent e) {
        if (e.getEntity().getType() == EntityType.MUSHROOM_COW) {
            if (e.getPlayer().hasCooldown(Material.SHEARS)) {
                e.setCancelled(true);
                return;
            }

            e.getPlayer().setCooldown(Material.SHEARS, 2);
        }
    }

    @EventHandler
    public void onCropPlace(BlockPlaceEvent e) {
        if (e.getBlock().getType().toString().contains("BED")) {
            Block block = e.getBlock();
            Location blockLocation = block.getLocation();
            blockLocation.setY(blockLocation.getY() - 1);
            List<Material> crops = Arrays.asList(
                    Material.WHEAT,
                    Material.CARROTS,
                    Material.POTATOES,
                    Material.BEETROOTS,
                    Material.NETHER_WART,
                    Material.WHEAT_SEEDS);

            if (crops.contains(blockLocation.getBlock().getType())) {
                e.setCancelled(true);
                HashMap<String, Integer> countdown = plugin.dupeFixCountdown;
                String player = e.getPlayer().getName();

                if (!countdown.containsKey(player)) {
                    countdown.put(player, 1);
                } else {
                    if (countdown.get(player) >= getConfig().getMaxWarnings()) {
                        forceCommand(Utils.format(
                                getConfig().getBanCommand()
                                        .replace("{PLAYER}", player)
                                        .replace("{X}", String.valueOf(blockLocation.getX()))
                                        .replace("{Y}", String.valueOf(blockLocation.getY()))
                                        .replace("{Z}", String.valueOf(blockLocation.getZ()))
                        ));
                        countdown.remove(player);
                    } else {
                        e.getPlayer().sendMessage(getConfig().getMessage("tryingToDupe")
                                .replace("{COUNTDOWN}", countdown.get(player) + "/" + getConfig().getMaxWarnings())
                        );

                        if (getConfig().getStrikeLightning()) {
                            Bukkit.getWorld(e.getPlayer().getWorld().getName()).strikeLightning(e.getPlayer().getLocation());
                        }

                        countdown.put(player, countdown.get(player) + 1);
                    }
                }
            }
        }
    }

    public static void forceCommand(String command) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }
}
