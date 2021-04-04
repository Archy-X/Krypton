package com.archyx.krypton.listeners;

import com.archyx.krypton.captcha.CaptchaManager;
import com.archyx.krypton.captcha.CaptchaPlayer;
import com.archyx.krypton.Krypton;
import com.archyx.krypton.configuration.CaptchaMode;
import com.archyx.krypton.configuration.Option;
import com.archyx.krypton.configuration.OptionL;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class CaptchaListener implements Listener {

    private final Krypton plugin;
    private final CaptchaManager manager;
    private final NumberFormat nf = new DecimalFormat("#.##");

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
                    long startTime = captchaPlayer.getCaptchaStartTime();
                    long endTime = System.currentTimeMillis();
                    double seconds = (double) (endTime - startTime) / 1000;
                    player.sendMessage(ChatColor.GREEN + "Captcha Complete! " + ChatColor.AQUA + "(" + nf.format(seconds) + "s)");
                    player.getInventory().setItem(0, captchaPlayer.getSlotItem());
                    manager.removeCaptchaPlayer(player);
                } else {
                    player.sendMessage(ChatColor.YELLOW + "Captcha incorrect, try again!");
                    if (OptionL.getBoolean(Option.ENABLE_FAIL_KICK)) {
                        captchaPlayer.setFailedAttempts(captchaPlayer.getFailedAttempts() + 1);
                        if (captchaPlayer.getFailedAttempts() >= OptionL.getInt(Option.FAIL_KICK_MAX_ATTEMPTS)) {
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    player.kickPlayer(ChatColor.RED + "Captcha failed too many times!");
                                }
                            }.runTask(plugin);
                        }
                    }
                }
            }
        }
    }
}
