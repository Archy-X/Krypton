package com.archyx.xcaptcha;

import co.aikar.commands.PaperCommandManager;
import com.archyx.xcaptcha.configuration.OptionL;
import com.archyx.xcaptcha.listeners.CaptchaActivator;
import com.archyx.xcaptcha.listeners.CaptchaBlockers;
import com.archyx.xcaptcha.listeners.CaptchaListener;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

public final class XCaptcha extends JavaPlugin {

    private CaptchaManager manager;
    private MapGenerator generator;
    private OptionL optionL;

    @Override
    public void onEnable() {
        generator = new MapGenerator();
        manager = new CaptchaManager(this);
        //Load config and options
        loadConfig();
        optionL = new OptionL(this);
        optionL.loadOptions();
        //Register events and commands
        registerEvents();
        registerCommands();
        Bukkit.getLogger().info("[XCaptcha] XCaptcha has been enabled");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void registerEvents() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new CaptchaActivator(this), this);
        pm.registerEvents(new CaptchaBlockers(this), this);
        pm.registerEvents(new CaptchaListener(this), this);
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
}
