package com.archyx.xcaptcha;

import org.bukkit.entity.Player;

import java.util.*;

public class CaptchaManager {

    private final Map<Player, CaptchaPlayer> captchaPlayers;
    private final Set<UUID> offlineCaptchaPlayers;

    private final MapGenerator generator;

    public CaptchaManager(XCaptcha plugin) {
        this.generator = plugin.getGenerator();
        captchaPlayers = new HashMap<>();
        offlineCaptchaPlayers = new HashSet<>();
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
        return offlineCaptchaPlayers.contains(id);
    }

    public Set<UUID> getOfflineCaptchaPlayers() {
        return offlineCaptchaPlayers;
    }

    public void addCaptchaPlayer(CaptchaPlayer player) {
        captchaPlayers.put(player.getPlayer(), player);
    }

    public void removeCaptchaPlayer(Player player) {
        captchaPlayers.remove(player);
    }

    public void addOfflineCaptchaPlayer(UUID id) {
        offlineCaptchaPlayers.add(id);
    }

    public void removeOfflineCaptchaPlayer(UUID id) {
        offlineCaptchaPlayers.remove(id);
    }

    public MapGenerator getGenerator() {
        return generator;
    }

}
