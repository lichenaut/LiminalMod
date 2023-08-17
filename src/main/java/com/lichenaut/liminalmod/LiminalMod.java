package com.lichenaut.liminalmod;

import com.lichenaut.liminalmod.load.LMListenerRegisterer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;
import java.util.regex.Pattern;

public final class LiminalMod extends JavaPlugin {

    private final LiminalMod plugin = this;
    private final Logger log = getLogger();
    private final Configuration config = getConfig();
    private int versionInt;

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        //int pluginId = ?????;
        //Metrics metrics = new Metrics(plugin, pluginId);

        if (config.getBoolean("disable-plugin"))
            log.info("Plugin disabled in config.yml.");
        else {
            //new LMUpdateChecker(this, plugin).getVersion(version -> {if (!this.getDescription().getVersion().equals(version)) {getLog().info("Update available.");}});

            String sVersion = Bukkit.getServer().getBukkitVersion();
            versionInt = Integer.parseInt(sVersion.split("-")[0].split(Pattern.quote("."))[1]);

            if (versionInt >= 20) {
                new LMListenerRegisterer(this).registerListeners(versionInt);
            } else log.severe("Unsupported version detected: " + sVersion + "! Disabling plugin.");
        }
    }

    public Logger getLog() {return log;}
    public Configuration getPluginConfig() {return config;}
    public int getVersion() {return versionInt;}
}