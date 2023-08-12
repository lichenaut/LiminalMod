package com.lichenaut.liminalmod.util;

import com.lichenaut.liminalmod.LiminalMod;
import org.bukkit.plugin.java.JavaPlugin;

public class LMUpdateChecker {

    private final JavaPlugin plugin;
    private final LiminalMod lmPlugin;

    public LMUpdateChecker(JavaPlugin plugin, LiminalMod lmPlugin) {this.plugin = plugin;this.lmPlugin = lmPlugin;}

    /*public void getVersion(final Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + ??????).openStream(); Scanner scanner = new Scanner(inputStream)) {
                if (scanner.hasNext()) {consumer.accept(scanner.next());}
            } catch (IOException e) {
                lmPlugin.getLog().warning("Unable to check for updates!");
                e.printStackTrace();
            }
        });
    }*/
}
