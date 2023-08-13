package com.lichenaut.liminalmod;

import com.lichenaut.liminalmod.load.LMStructure;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public final class LiminalMod extends JavaPlugin {

    private final LiminalMod plugin = this;
    private final Logger log = getLogger();
    private final Configuration config = getConfig();
    private int versionInt;
    private final HashMap<Chunk, HashSet<LMStructure>> structureChunks = new HashMap<>();//one chunk can have multiple structures

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

            if (versionInt >= 13) {
                //load serialized structureChunks

            } else log.severe("Unsupported version detected: " + sVersion + "! Disabling plugin.");
        }
    }

    @Override
    public void onDisable() {
        // serialize structureChunks
    }

    public Logger getLog() {return log;}
    public Configuration getPluginConfig() {return config;}
    public int getVersion() {return versionInt;}
    public HashMap<Chunk, HashSet<LMStructure>> getStructureChunks() {return structureChunks;}
    public void putStructureChunk(Chunk chunk, LMStructure structure) {
        getStructureChunks().computeIfAbsent(chunk, k -> new HashSet<>()).add(structure);
    }
}