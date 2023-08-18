package com.lichenaut.liminalmod;

import com.lichenaut.liminalmod.listening.LMStructureEvent;
import com.lichenaut.liminalmod.load.LMListenerRegisterer;
import com.lichenaut.liminalmod.serialization.LMSerialization;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public final class LiminalMod extends JavaPlugin {

    private final Logger log = getLogger();
    private final Configuration config = getConfig();
    private List<Location> abandonmentMarkers = new ArrayList<>();
    private final Queue<LMStructureEvent> structureEvents = new LinkedList<>();//TODO: this is a mess

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
            int versionInt = Integer.parseInt(sVersion.split("-")[0].split(Pattern.quote("."))[1]);

            if (versionInt >= 20) {
                createResources();

                try {
                    ByteBuffer serializedData = loadFlatBufferFromFile(new File(getDataFolder(), "abandonmentMarkers.fb"));
                    if (serializedData.capacity() > 0) abandonmentMarkers = LMSerialization.deserializeMarkers(serializedData);
                } catch (IOException e) {throw new RuntimeException(e);}//TODO: What other classes should be static?

                new LMListenerRegisterer(this).registerListeners(versionInt);
            } else log.severe("Unsupported version detected: " + sVersion + "! Disabling plugin.");
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void onDisable() {
        ByteBuffer serializedData = LMSerialization.serializeMarkers(abandonmentMarkers);
        try (FileOutputStream fos = new FileOutputStream(new File(getDataFolder(), "abandonmentMarkers.fb")); FileChannel channel = fos.getChannel()) {
            channel.write(serializedData);
        } catch (IOException e) {e.printStackTrace();}
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void createResources() {
        File presetDir = new File(getDataFolder(), "presets");
        if (!presetDir.exists()) presetDir.mkdir();

        String[] resourceFiles = {"lichenauts-rebalance-preset.txt", "post-apoc-preset.txt", "abandonmentMarkers.fb"};
        for (String resourceFile : resourceFiles) {
            File destination;
            if (resourceFile.equals("abandonmentMarkers.fb")) destination = new File(getDataFolder(), resourceFile); else destination = new File(presetDir, resourceFile);
            if (destination.exists()) continue;

            try (InputStream in = getResource(resourceFile); OutputStream out = Files.newOutputStream(destination.toPath())) {
                if (in == null) throw new IOException("Resource not found: " + resourceFile);

                byte[] buffer = new byte[1024];
                int length;
                while ((length = in.read(buffer)) > 0) out.write(buffer, 0, length);
            } catch (Exception e) {e.printStackTrace();}
        }
    }
    private ByteBuffer loadFlatBufferFromFile(File file) throws IOException {
        if (!file.exists()) throw new FileNotFoundException("The file " + file.getAbsolutePath() + " was not found.");
        if (file.length() == 0) return ByteBuffer.allocate(0);
        byte[] data = Files.readAllBytes(file.toPath());
        ByteBuffer buffer = ByteBuffer.wrap(data);
        buffer.position(0);
        return buffer;
    }
    public Configuration getPluginConfig() {return config;}
    public List<Location> getAbandonmentMarkers() {return abandonmentMarkers;}
    public void addAbandonmentMarker(Location l) {abandonmentMarkers.add(l);}
    public Queue<LMStructureEvent> getStructureEvents() {return structureEvents;}
    public void addStructureEvent(LMStructureEvent e) {structureEvents.add(e);}
}