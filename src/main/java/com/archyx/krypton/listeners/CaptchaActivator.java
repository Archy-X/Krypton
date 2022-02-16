package com.archyx.krypton.listeners;

import com.archyx.krypton.Krypton;
import com.archyx.krypton.captcha.CaptchaActivateReason;
import com.archyx.krypton.captcha.CaptchaManager;
import com.archyx.krypton.captcha.CaptchaMenu;
import com.archyx.krypton.captcha.CaptchaPlayer;
import com.archyx.krypton.captcha.OfflineCaptchaPlayer;
import com.archyx.krypton.configuration.CaptchaMode;
import com.archyx.krypton.configuration.Option;
import com.archyx.krypton.configuration.OptionL;
import com.archyx.krypton.events.PlayerCaptchaActivateEvent;
import com.archyx.krypton.events.PlayerQuitDuringCaptchaEvent;
import com.archyx.krypton.messages.MessageKey;
import org.bukkit.Bukkit;
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

        if (!manager.isEnabled()) return; // Check if enabled

        if (manager.isOfflineCaptchaPlayer(player.getUniqueId())) {
            activate(
                player,
                manager.getOfflineCaptchaPlayers().get(player.getUniqueId()).getFailedAttempts(),
                CaptchaActivateReason.FROM_PREVIOUS_SESSION
            );

            manager.removeOfflineCaptchaPlayer(player.getUniqueId());
            return;
        }

        if (OptionL.getBoolean(Option.API_MODE)) return; // Skip if API mode

        if (!player.hasPlayedBefore() || OptionL.getBoolean(Option.CAPTCHA_EVERY_JOIN)) {
            activate(player, 0, CaptchaActivateReason.FROM_JOIN);
        }
    }

    public void activate(Player player, int failedAttempts, final CaptchaActivateReason activateReason) {
        if(manager.isCaptchaPlayer(player)) return;

        /* Call the event */
        final PlayerCaptchaActivateEvent activateEvent = new PlayerCaptchaActivateEvent(
            player,
            activateReason
        );
        Bukkit.getPluginManager().callEvent(activateEvent);
        if(activateEvent.isCancelled()) return;

        switch(OptionL.getMode()) {
            case MAP:
                activateMapCaptcha(player, failedAttempts);
                break;
            case MENU:
                activateMenuCaptcha(player, failedAttempts);
                break;
            default:
                throw new IllegalStateException("Unexpected state: " + OptionL.getMode());
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
        } else {
            captchaPlayer.setAllowMove(false);
        }
        player.sendMessage(plugin.getMessage(MessageKey.MAP_INFO));

        new BukkitRunnable() {
            @Override
            public void run() {
                if (manager.isCaptchaPlayer(player)) {
                    player.sendMessage(plugin.getMessage(MessageKey.MAP_INFO));
                } else {
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 100L, 100L);

        manager.addCaptchaPlayer(captchaPlayer);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        CaptchaPlayer captchaPlayer = manager.getCaptchaPlayer(player);
        if (captchaPlayer == null) return;

        if (captchaPlayer.getMode() == CaptchaMode.MAP) {
            player.getInventory().setItem(0, captchaPlayer.getSlotItem());
        }

        manager.removeCaptchaPlayer(player);
        manager.addOfflineCaptchaPlayer(new OfflineCaptchaPlayer(
            player.getUniqueId(),
            captchaPlayer.getTotalFailedAttempts()
        ));

        Bukkit.getPluginManager().callEvent(new PlayerQuitDuringCaptchaEvent(captchaPlayer));
    }

}
