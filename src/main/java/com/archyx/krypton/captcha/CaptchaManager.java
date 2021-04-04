package com.archyx.krypton.captcha;

import com.archyx.krypton.Krypton;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CaptchaManager {

    private final ConcurrentMap<Player, CaptchaPlayer> captchaPlayers;
    private final ConcurrentMap<UUID, OfflineCaptchaPlayer> offlineCaptchaPlayers;

    private final MapGenerator generator;

    public CaptchaManager(Krypton plugin) {
        this.generator = plugin.getGenerator();
        captchaPlayers = new ConcurrentHashMap<>();
        offlineCaptchaPlayers = new ConcurrentHashMap<>();
    }

    public Map<Player, CaptchaPlayer> getCaptchaPlayers() {
        return captchaPlayers;
    }

    public CaptchaPlayer getCaptchaPlayer(Player player) {
        return captchaPlayers.get(player);
    }

    public boolean isCaptchaPlayer(Player player) {
        return captchaPlayers.containsKey(player);
    }

    public boolean isOfflineCaptchaPlayer(UUID id) {
        return offlineCaptchaPlayers.containsKey(id);
    }

    public Map<UUID, OfflineCaptchaPlayer> getOfflineCaptchaPlayers() {
        return offlineCaptchaPlayers;
    }

    public void addCaptchaPlayer(CaptchaPlayer player) {
        captchaPlayers.put(player.getPlayer(), player);
    }

    public void removeCaptchaPlayer(Player player) {
        captchaPlayers.remove(player);
    }

    public void addOfflineCaptchaPlayer(OfflineCaptchaPlayer player) {
        offlineCaptchaPlayers.put(player.getId(), player);
    }

    public void removeOfflineCaptchaPlayer(UUID id) {
        offlineCaptchaPlayers.remove(id);
    }

    public MapGenerator getGenerator() {
        return generator;
    }

}
