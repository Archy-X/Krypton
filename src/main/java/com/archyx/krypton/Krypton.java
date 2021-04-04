package com.archyx.krypton;

import co.aikar.commands.PaperCommandManager;
import com.archyx.krypton.captcha.CaptchaManager;
import com.archyx.krypton.captcha.MapGenerator;
import com.archyx.krypton.commands.Commands;
import com.archyx.krypton.configuration.OptionL;
import com.archyx.krypton.data.DataLoader;
import com.archyx.krypton.listeners.CaptchaActivator;
import com.archyx.krypton.listeners.CaptchaBlockers;
import com.archyx.krypton.listeners.CaptchaListener;
import com.archyx.krypton.listeners.PacketListener;
import fr.minuskube.inv.InventoryManager;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

public final class Krypton extends JavaPlugin {

    private CaptchaManager manager;
    private MapGenerator generator;
    private OptionL optionL;
    private InventoryManager inventoryManager;

    @Override
    public void onEnable() {
        generator = new MapGenerator();
        manager = new CaptchaManager(this);
        inventoryManager = new InventoryManager(this);
        inventoryManager.init();
        // Load config and options
        loadConfig();
        optionL = new OptionL(this);
        optionL.loadOptions();
        // Register events and commands
        registerEvents();
        registerCommands();
        new Metrics(this, 9430);
        // Load data
        new DataLoader(this).loadData();
        Bukkit.getLogger().info("[Krypton] Krypton has been enabled");
    }

    @Override
    public void onDisable() {
        // Save data
        new DataLoader(this).saveData();
    }

    private void registerEvents() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new CaptchaActivator(this), this);
        pm.registerEvents(new CaptchaBlockers(this), this);
        pm.registerEvents(new CaptchaListener(this), this);
        if (getServer().getPluginManager().isPluginEnabled("ProtocolLib")) {
            PacketListener packetListener = new PacketListener(this);
            packetListener.registerPacketListener();
        }
    }

    private void registerCommands() {
        PaperCommandManager manager = new PaperCommandManager(this);
        manager.registerCommand(new Commands(this));
    }

    private void loadConfig() {
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        try {
            InputStream is = getResource("config.yml");
            if (is != null) {
                YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(is));
                for (String key : Objects.requireNonNull(defConfig.getConfigurationSection("")).getKeys(true)) {
                    if (!getConfig().contains(key)) {
                        getConfig().set(key, defConfig.get(key));
                    }
                }
                saveConfig();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public OptionL getOptionL() {
        return optionL;
    }

    public CaptchaManager getManager() {
        return manager;
    }

    public MapGenerator getGenerator() {
        return generator;
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }
}
