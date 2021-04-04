package com.archyx.krypton.data;

import com.archyx.krypton.Krypton;
import com.archyx.krypton.captcha.CaptchaManager;
import com.archyx.krypton.captcha.CaptchaPlayer;
import com.archyx.krypton.captcha.OfflineCaptchaPlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class DataLoader {

    private final CaptchaManager manager;
    private final Krypton plugin;

    public DataLoader(Krypton plugin) {
        this.manager = plugin.getManager();
        this.plugin = plugin;
    }

    public void loadData() {
        File file = new File(plugin.getDataFolder(), "data.yml");
        if (!file.exists()) {
            plugin.saveResource("data.yml", false);
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection failedPlayers = config.getConfigurationSection("failed_players");
        if (failedPlayers != null) {
            for (String stringId : failedPlayers.getKeys(false)) {
                UUID id = UUID.fromString(stringId);
                int failedAttempts = failedPlayers.getInt(stringId, 0);
                OfflineCaptchaPlayer player = new OfflineCaptchaPlayer(id, failedAttempts);
                manager.addOfflineCaptchaPlayer(player);
            }
        }
    }

    public void saveData() {
        File file = new File(plugin.getDataFolder(), "data.yml");
        if (!file.exists()) {
            plugin.saveResource("data.yml", false);
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        for (OfflineCaptchaPlayer player : manager.getOfflineCaptchaPlayers().values()) {
            config.set("failed_players." + player.getId().toString(), player.getFailedAttempts());
        }
        for (CaptchaPlayer player : manager.getCaptchaPlayers().values()) {
            config.set("failed_players." + player.getPlayer().getUniqueId().toString(), player.getFailedAttempts());
        }
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
