package com.archyx.xcaptcha.listeners;

import com.archyx.xcaptcha.CaptchaManager;
import com.archyx.xcaptcha.configuration.CaptchaMode;
import com.archyx.xcaptcha.CaptchaPlayer;
import com.archyx.xcaptcha.XCaptcha;
import com.archyx.xcaptcha.configuration.Option;
import com.archyx.xcaptcha.configuration.OptionL;
import org.bukkit.ChatColor;
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
    private final XCaptcha plugin;

    public CaptchaActivator(XCaptcha plugin) {
        this.plugin = plugin;
        this.manager = plugin.getManager();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (manager.isOfflineCaptchaPlayer(player.getUniqueId())) {
            activate(player);
            manager.removeOfflineCaptchaPlayer(player.getUniqueId());
        }
        else if (!player.hasPlayedBefore()) {
            activate(player);
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
        else if (OptionL.getBoolean(Option.CAPTCHA_EVERY_JOIN)) {
            activate(player);
        }
    }

    public void activate(Player player) {
        if (!manager.isCaptchaPlayer(player)) {
            if (OptionL.getMode() == CaptchaMode.MAP) {
                CaptchaPlayer captchaPlayer = new CaptchaPlayer(player, CaptchaMode.MAP);
                //Map
                player.getInventory().setHeldItemSlot(0);
                captchaPlayer.setSlotItem(player.getInventory().getItem(0));
                String code = manager.getGenerator().generateCode();
                captchaPlayer.setMapCode(code);
                player.getInventory().setItem(0, manager.getGenerator().generateMap(player, code));
                captchaPlayer.setCaptchaStartTime(System.currentTimeMillis());
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
                player.sendMessage(ChatColor.RED + "Enter the code on the map in chat to be verified");
                scheduleTask(player);
                manager.addCaptchaPlayer(captchaPlayer);
            }
        }
    }

    private void scheduleTask(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (manager.isCaptchaPlayer(player)) {
                    player.sendMessage(ChatColor.RED + "Enter the code on the map in chat to be verified!");
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
            player.getInventory().setItem(0, captchaPlayer.getSlotItem());
            manager.removeCaptchaPlayer(player);
            manager.addOfflineCaptchaPlayer(player.getUniqueId());
        }
    }

}
