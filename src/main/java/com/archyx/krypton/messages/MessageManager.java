package com.archyx.krypton.messages;

import com.archyx.krypton.Krypton;
import com.archyx.krypton.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class MessageManager {

    private final Krypton plugin;
    private final Map<MessageKey, String> messages;

    public MessageManager(Krypton plugin) {
        this.plugin = plugin;
        this.messages = new HashMap<>();
    }

    public String getMessage(MessageKey messageKey) {
        return messages.getOrDefault(messageKey, "");
    }

    public void load() {
        File file = new File(plugin.getDataFolder(), "messages.yml");
        if (!file.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        loadDefaultMessages();
        FileConfiguration config = updateFile(file, YamlConfiguration.loadConfiguration(file));
        loadMessages(config);
    }

    private void loadMessages(FileConfiguration config) {
        for (MessageKey key : MessageKey.values()) {
            String message = config.getString(key.getPath());
            if (message != null) {
                messages.put(key, TextUtil.replace(message, "&", "§"));
            }
        }
    }

    private void loadDefaultMessages() {
        InputStream inputStream = plugin.getResource("messages.yml");
        if (inputStream != null) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            loadMessages(config);
        }
    }

    private FileConfiguration updateFile(File file, FileConfiguration config) {
        if (config.contains("file_version")) {
            InputStream stream = plugin.getResource("messages.yml");
            if (stream != null) {
                int currentVersion = config.getInt("file_version");
                FileConfiguration imbConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(stream, StandardCharsets.UTF_8));
                int imbVersion = imbConfig.getInt("file_version");
                // If versions do not match
                if (currentVersion != imbVersion) {
                    try {
                        ConfigurationSection configSection = imbConfig.getConfigurationSection("");
                        int keysAdded = 0;
                        if (configSection != null) {
                            for (String key : configSection.getKeys(true)) {
                                if (!configSection.isConfigurationSection(key)) {
                                    if (!config.contains(key)) {
                                        config.set(key, imbConfig.get(key));
                                        keysAdded++;
                                    }
                                }
                            }
                        }
                        config.set("file_version", imbVersion);
                        config.save(file);
                        Bukkit.getLogger().info("[Krypton] messages.yml was updated to a new file version, " + keysAdded + " new keys were added.");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return YamlConfiguration.loadConfiguration(file);
    }

}
