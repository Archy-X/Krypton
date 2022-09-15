package com.archyx.krypton.listeners;

import com.archyx.krypton.captcha.CaptchaManager;
import com.archyx.krypton.captcha.CaptchaPlayer;
import com.archyx.krypton.Krypton;
import com.archyx.krypton.configuration.CaptchaMode;
import com.archyx.krypton.configuration.Option;
import com.archyx.krypton.configuration.OptionL;
import com.archyx.krypton.events.PlayerCaptchaFailEvent;
import com.archyx.krypton.events.PlayerCaptchaSolveEvent;
import com.archyx.krypton.messages.MessageKey;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class CaptchaListener implements Listener {

    private final Krypton plugin;
    private final CaptchaManager manager;

    public CaptchaListener(Krypton plugin) {
        this.plugin = plugin;
        manager = plugin.getManager();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        CaptchaPlayer captchaPlayer = manager.getCaptchaPlayer(player);
        if (captchaPlayer != null) {
            event.setCancelled(true);
            if (captchaPlayer.getMode() == CaptchaMode.MAP) {
                if (ChatColor.stripColor(event.getMessage()).equals(captchaPlayer.getMapCode())) {
                    plugin.getServer().getScheduler().runTask(plugin, () ->
                            Bukkit.getPluginManager().callEvent(new PlayerCaptchaSolveEvent(captchaPlayer)));

                    player.sendMessage(plugin.getMessage(MessageKey.COMPLETE));
                    player.getInventory().setItem(0, captchaPlayer.getSlotItem());
                    manager.removeCaptchaPlayer(player);
                } else {
                    plugin.getServer().getScheduler().runTask(plugin, () ->
                            Bukkit.getPluginManager().callEvent(new PlayerCaptchaFailEvent(captchaPlayer)));

                    player.sendMessage(plugin.getMessage(MessageKey.MAP_INCORRECT));

                    if (OptionL.getBoolean(Option.ENABLE_FAIL_KICK)) {
                        captchaPlayer.incrementFailedAttempts();
                        if (captchaPlayer.getFailedAttempts() >= OptionL.getInt(Option.FAIL_KICK_MAX_ATTEMPTS)) {
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    player.getInventory().setItem(0, captchaPlayer.getSlotItem());
                                    player.kickPlayer(plugin.getMessage(MessageKey.KICK));
                                }
                            }.runTask(plugin);
                        }
                    }
                }
            }
        }
    }
}
