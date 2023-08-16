package com.lichenaut.liminalmod;

import com.lichenaut.liminalmod.listening.gen.twenty.LMStructureListener20;
import com.lichenaut.liminalmod.listening.passive.twenty.LMCureListener20;
import com.lichenaut.liminalmod.listening.passive.twenty.LMSpawnListener20;
import com.lichenaut.liminalmod.listening.passive.twenty.LMTargetListener20;
import com.lichenaut.liminalmod.load.LMStructure;
import com.lichenaut.liminalmod.util.LMSerialization;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public final class LiminalMod extends JavaPlugin {

    private final LiminalMod plugin = this;
    private final LMSerialization serialization = new LMSerialization(this);
    private final Logger log = getLogger();
    private final Configuration config = getConfig();
    private int versionInt;
    private HashMap<Chunk, HashSet<LMStructure>> structureChunks = new HashMap<>();//one chunk can have multiple structures

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
                createResource("structureChunks.txt"); //TODO: fix serialization file interaction
                structureChunks = serialization.deserializeStructures();

                PluginManager pMan = Bukkit.getPluginManager();
                pMan.registerEvents(new LMStructureListener20(this), this);
                pMan.registerEvents(new LMCureListener20(this), this);
                pMan.registerEvents(new LMSpawnListener20(this), this);
                pMan.registerEvents(new LMTargetListener20(this), this);
            } else log.severe("Unsupported version detected: " + sVersion + "! Disabling plugin.");
        }
    }

    @Override
    public void onDisable() {serialization.serializeStructures(structureChunks);}

    public Logger getLog() {return log;}
    public Configuration getPluginConfig() {return config;}
    public int getVersion() {return versionInt;}
    public HashMap<Chunk, HashSet<LMStructure>> getStructureChunks() {return structureChunks;}
    public void putStructureChunk(Chunk chunk, LMStructure structure) {
        getStructureChunks().computeIfAbsent(chunk, k -> new HashSet<>()).add(structure);
    }
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void createResource(String resourceName) {
        File resourceFile = new File(getDataFolder(), resourceName);
        if (!resourceFile.exists()) {
            try {
                if (!getDataFolder().exists()) getDataFolder().mkdirs();
                resourceFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}