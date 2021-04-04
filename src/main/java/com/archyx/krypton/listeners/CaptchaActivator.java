package com.archyx.krypton.listeners;

import com.archyx.krypton.Krypton;
import com.archyx.krypton.captcha.CaptchaManager;
import com.archyx.krypton.captcha.CaptchaMenu;
import com.archyx.krypton.captcha.CaptchaPlayer;
import com.archyx.krypton.captcha.OfflineCaptchaPlayer;
import com.archyx.krypton.configuration.CaptchaMode;
import com.archyx.krypton.configuration.Option;
import com.archyx.krypton.configuration.OptionL;
import com.archyx.krypton.messages.MessageKey;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class CaptchaActivator implements Listener {

    private final CaptchaManager manager;
    private final Krypton plugin;

    public CaptchaActivator(Krypton plugin) {
        this.plugin = plugin;
        this.manager = plugin.getManager();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (manager.isOfflineCaptchaPlayer(player.getUniqueId())) {
            activate(player, manager.getOfflineCaptchaPlayers().get(player.getUniqueId()).getFailedAttempts());
            manager.removeOfflineCaptchaPlayer(player.getUniqueId());
        }
        else if (!player.hasPlayedBefore()) {
            activate(player, 0);
            if (OptionL.getMode() == CaptchaMode.MAP) {
                if (OptionL.getBoolean(Option.MAP_FORCE_PITCH_ENABLED)) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            Location location = player.getLocation();
                            location.setPitch((float) OptionL.getDouble(Option.MAP_FORCE_PITCH_PITCH));
                            player.teleport(location);
                        }
                    }.runTaskLater(plugin, 5L);
                }
            }
        }
        else if (OptionL.getBoolean(Option.CAPTCHA_EVERY_JOIN)) {
            activate(player, 0);
        }
    }

    public void activate(Player player, int failedAttempts) {
        if (!manager.isCaptchaPlayer(player)) {
            if (OptionL.getMode() == CaptchaMode.MAP) {
                activateMapCaptcha(player, failedAttempts);
            }
            else if (OptionL.getMode() == CaptchaMode.MENU) {
                activateMenuCaptcha(player, failedAttempts);
            }
        }
    }

    private void activateMenuCaptcha(Player player, int failedAttempts) {
       CaptchaPlayer captchaPlayer = new CaptchaPlayer(player, CaptchaMode.MENU, failedAttempts);
       manager.addCaptchaPlayer(captchaPlayer);
       new BukkitRunnable() {
           @Override
           public void run() {
               CaptchaMenu.getInventory(captchaPlayer, plugin).open(player);
           }
       }.runTaskLater(plugin, 1L);
    }

    private void activateMapCaptcha(Player player, int failedAttempts) {
        CaptchaPlayer captchaPlayer = new CaptchaPlayer(player, CaptchaMode.MAP, failedAttempts);
        player.getInventory().setHeldItemSlot(0);
        captchaPlayer.setSlotItem(player.getInventory().getItem(0));
        String code = manager.getGenerator().generateCode();
        captchaPlayer.setMapCode(code);
        player.getInventory().setItem(0, manager.getGenerator().generateMap(player, code));
        //Force pitch if enabled
        if (OptionL.getBoolean(Option.MAP_FORCE_PITCH_ENABLED)) {
            Location location = player.getLocation();
            location.setPitch((float) OptionL.getDouble(Option.MAP_FORCE_PITCH_PITCH));
            player.teleport(location);
        }
        //Allow move if enabled
        if (OptionL.getBoolean(Option.MAP_ALLOW_MOVE_ENABLED)) {
            captchaPlayer.setAllowMove(true);
            new BukkitRunnable() {
                @Override
                public void run() {
                    captchaPlayer.setAllowMove(false);
                }
            }.runTaskLater(plugin, Math.abs(OptionL.getInt(Option.MAP_ALLOW_MOVE_DURATION_TICKS)));
        }
        else {
            captchaPlayer.setAllowMove(false);
        }
        player.sendMessage(plugin.getMessage(MessageKey.MAP_INFO));
        scheduleTask(player);
        manager.addCaptchaPlayer(captchaPlayer);
    }

    private void scheduleTask(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (manager.isCaptchaPlayer(player)) {
                    player.sendMessage(plugin.getMessage(MessageKey.MAP_INFO));
                }
                else {
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 100L, 100L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        CaptchaPlayer captchaPlayer = manager.getCaptchaPlayer(player);
        if (captchaPlayer != null) {
            if (captchaPlayer.getMode() == CaptchaMode.MAP) {
                player.getInventory().setItem(0, captchaPlayer.getSlotItem());
            }
            manager.removeCaptchaPlayer(player);
            manager.addOfflineCaptchaPlayer(new OfflineCaptchaPlayer(player.getUniqueId(), captchaPlayer.getTotalFailedAttempts()));
        }
    }

}
