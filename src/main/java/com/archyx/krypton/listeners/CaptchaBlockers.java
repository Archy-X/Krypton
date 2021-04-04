package com.archyx.krypton.listeners;

import com.archyx.krypton.captcha.CaptchaManager;
import com.archyx.krypton.captcha.CaptchaPlayer;
import com.archyx.krypton.Krypton;
import com.archyx.krypton.configuration.Option;
import com.archyx.krypton.configuration.OptionL;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;

public class CaptchaBlockers implements Listener {

    private final CaptchaManager manager;

    public CaptchaBlockers(Krypton plugin) {
        this.manager = plugin.getManager();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMove(PlayerMoveEvent event) {
        CaptchaPlayer captchaPlayer = manager.getCaptchaPlayer(event.getPlayer());
        if (captchaPlayer != null) {
            if (!captchaPlayer.isAllowMove()) {
                if (OptionL.getBoolean(Option.MAP_BLOCK_LOOK)) {
                    event.setCancelled(true);
                } else {
                    Location from = event.getFrom();
                    Location to = event.getTo();
                    if (to != null) {
                        if (from.getX() != to.getX() || from.getY() != to.getY() || from.getZ() != to.getZ()) {
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSwitchItems(PlayerItemHeldEvent event) {
        if (manager.isCaptchaPlayer(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event) {
        CaptchaPlayer captchaPlayer = manager.getCaptchaPlayer(event.getPlayer());
        if (captchaPlayer != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        if (manager.isCaptchaPlayer(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (manager.isCaptchaPlayer(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAttack(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            if (manager.isCaptchaPlayer(player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (manager.isCaptchaPlayer(player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDrop(PlayerDropItemEvent event) {
        if (manager.isCaptchaPlayer(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    @SuppressWarnings("deprecation")
    public void onPickup(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        if (manager.isCaptchaPlayer(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            CaptchaPlayer captchaPlayer = manager.getCaptchaPlayer(player);
            if (captchaPlayer != null) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        if (manager.isCaptchaPlayer(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (manager.isCaptchaPlayer(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

}
