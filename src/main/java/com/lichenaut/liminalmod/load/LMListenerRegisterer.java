package com.lichenaut.liminalmod.load;

import com.lichenaut.liminalmod.LiminalMod;
import com.lichenaut.liminalmod.listening.gen.twenty.LMStructureListener20;
import com.lichenaut.liminalmod.listening.passive.twenty.LMCureListener20;
import com.lichenaut.liminalmod.listening.passive.twenty.LMSpawnListener20;
import com.lichenaut.liminalmod.listening.passive.twenty.LMTargetListener20;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

public class LMListenerRegisterer {

    private final LiminalMod plugin;

    public LMListenerRegisterer(LiminalMod plugin) {this.plugin = plugin;}

    public void registerListeners(int version) {
        PluginManager pMan = Bukkit.getPluginManager();
        for (int v = version; v > 0; v--) {// Expands with each version
            if (v == 20) {
                pMan.registerEvents(new LMStructureListener20(plugin, v), plugin);
                pMan.registerEvents(new LMCureListener20(plugin), plugin);
                pMan.registerEvents(new LMSpawnListener20(plugin), plugin);
                pMan.registerEvents(new LMTargetListener20(plugin), plugin);
                break;
            }
        }
    }

}
