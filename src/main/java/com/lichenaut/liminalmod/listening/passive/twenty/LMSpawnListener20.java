package com.lichenaut.liminalmod.listening.passive.twenty;

import com.lichenaut.liminalmod.LiminalMod;
import com.lichenaut.liminalmod.util.LMMiscUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.Configuration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.ArrayList;
import java.util.List;

public class LMSpawnListener20 extends LMMiscUtil implements Listener {

    private final Configuration config = plugin.getPluginConfig();
    private final int patrolSpawnRate = config.getInt("patrol-spawn-rate");
    private final int piglinSpawnRate = config.getInt("piglin-spawn-rate");
    private final int wanderingTraderSpawnRate = config.getInt("wandering-trader-spawn-rate");
    private final List<Location> noPatrolMarkers = new ArrayList<>();

    public LMSpawnListener20(LiminalMod plugin) {super(plugin);}

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCreatureSpawnEvent(CreatureSpawnEvent e) {
        CreatureSpawnEvent.SpawnReason spawnReason = e.getSpawnReason();
        if (!(spawnReason == CreatureSpawnEvent.SpawnReason.NATURAL || spawnReason == CreatureSpawnEvent.SpawnReason.PATROL)) return;
        switch (e.getEntityType()) {
            case ALLAY:
                if (foundMarker(e.getLocation(), plugin.getAbandonmentMarkers(), 122)) e.setCancelled(true);
                break;
            case ELDER_GUARDIAN:
            case GUARDIAN:
                if (foundMarker(e.getLocation(), plugin.getAbandonmentMarkers(), 80)) e.setCancelled(true);
                break;
            case PILLAGER:
                Location l = e.getLocation();
                if (spawnReason == CreatureSpawnEvent.SpawnReason.PATROL) {
                    if (foundMarker(l, noPatrolMarkers, 32)) {
                        e.setCancelled(true);
                    } else if (chance(100 - patrolSpawnRate)) {
                        e.setCancelled(true);
                        noPatrolMarkers.add(l);
                        Bukkit.getScheduler().runTaskLater(plugin, () -> noPatrolMarkers.remove(l), 4L);
                    }
                } else if (foundMarker(l, plugin.getAbandonmentMarkers(), 49)) e.setCancelled(true);
                break;
            case PIGLIN:
                if (chance(100 - piglinSpawnRate) || foundMarker(e.getLocation(), plugin.getAbandonmentMarkers(), 94)) e.setCancelled(true);
                break;
            case WANDERING_TRADER:
                if (chance(100 - wanderingTraderSpawnRate)) e.setCancelled(true);
        }
    }

    private boolean foundMarker(Location l1, List<Location> ls, int radius) {
        for (Location l2 : ls) {// TODO: Watch this for performance issues, consider spatial hashing if it's lagging on large lists
            if (l1.getWorld() != l2.getWorld()) continue;
            double x = l1.getX() - l2.getX(), z = l1.getZ() - l2.getZ();
            if (x * x + z * z <= radius * radius) {return true;}
        }
        return false;
    }
}