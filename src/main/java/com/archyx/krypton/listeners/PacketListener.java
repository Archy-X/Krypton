package com.archyx.krypton.listeners;

import com.archyx.krypton.captcha.CaptchaManager;
import com.archyx.krypton.captcha.CaptchaPlayer;
import com.archyx.krypton.Krypton;
import com.archyx.krypton.configuration.CaptchaMode;
import com.archyx.krypton.configuration.Option;
import com.archyx.krypton.configuration.OptionL;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class PacketListener {

    private final Krypton plugin;
    private final ProtocolManager protocolManager;
    private final CaptchaManager manager;
    private final NumberFormat nf = new DecimalFormat("#.##");

    public PacketListener(Krypton plugin) {
        this.plugin = plugin;
        protocolManager = ProtocolLibrary.getProtocolManager();
        manager = plugin.getManager();
    }

    public void registerPacketListener() {
        protocolManager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Client.CHAT) {
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
                            long startTime = captchaPlayer.getCaptchaStartTime();
                            long endTime = System.currentTimeMillis();
                            double seconds = (double) (endTime - startTime) / 1000;
                            player.sendMessage(ChatColor.GREEN + "Captcha Complete! " + ChatColor.AQUA + "(" + nf.format(seconds) + "s)");
                            player.getInventory().setItem(0, captchaPlayer.getSlotItem());
                            manager.removeCaptchaPlayer(player);
                        }
                        else {
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
        });
    }

}
