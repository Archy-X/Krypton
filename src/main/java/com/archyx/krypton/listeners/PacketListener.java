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
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PacketListener {

    private final Krypton krypton;
    private final ProtocolManager protocolManager;
    private final CaptchaManager manager;

    public PacketListener(Krypton krypton) {
        this.krypton = krypton;
        protocolManager = ProtocolLibrary.getProtocolManager();
        manager = krypton.getManager();
    }

    public void registerPacketListener() {
        protocolManager.addPacketListener(new PacketAdapter(krypton, ListenerPriority.NORMAL, PacketType.Play.Client.CHAT) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                if (event.getPacketType() == PacketType.Play.Client.CHAT) {
                    PacketContainer packet = event.getPacket();
                    String message = packet.getStrings().read(0);
                    Player player = event.getPlayer();
                    CaptchaPlayer captchaPlayer = manager.getCaptchaPlayer(player);
                    if (captchaPlayer == null) return;
                    if (!OptionL.getBoolean(Option.MAP_USE_PROTOCOL_LIB)) return;

                    event.setCancelled(true);
                    if (captchaPlayer.getMode() == CaptchaMode.MAP) {
                        if (ChatColor.stripColor(message).equals(captchaPlayer.getMapCode())) {
                            // Call API event on main thread
                            plugin.getServer().getScheduler().runTask(plugin, () ->
                                    Bukkit.getPluginManager().callEvent(new PlayerCaptchaSolveEvent(captchaPlayer)));

                            player.sendMessage(krypton.getMessage(MessageKey.COMPLETE));
                            player.getInventory().setItem(0, captchaPlayer.getSlotItem());
                            manager.removeCaptchaPlayer(player);
                        } else {
                            // Call API event on main thread
                            plugin.getServer().getScheduler().runTask(plugin, () ->
                                    Bukkit.getPluginManager().callEvent(new PlayerCaptchaFailEvent(captchaPlayer)));

                            player.sendMessage(krypton.getMessage(MessageKey.MAP_INCORRECT));

                            if (OptionL.getBoolean(Option.ENABLE_FAIL_KICK)) {
                                captchaPlayer.incrementFailedAttempts();
                                if (captchaPlayer.getFailedAttempts() >= OptionL.getInt(Option.FAIL_KICK_MAX_ATTEMPTS)) {
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            player.getInventory().setItem(0, captchaPlayer.getSlotItem());
                                            player.kickPlayer(krypton.getMessage(MessageKey.KICK));
                                        }
                                    }.runTask(plugin);
                                }
                            }
                        }
                    }
                }
            }
        });
    }

}
