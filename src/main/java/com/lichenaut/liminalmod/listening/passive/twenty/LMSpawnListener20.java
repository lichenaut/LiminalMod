package com.lichenaut.liminalmod.listening.passive.twenty;

import com.lichenaut.liminalmod.LiminalMod;
import com.lichenaut.liminalmod.util.LMListenerUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.util.BoundingBox;

import java.util.HashSet;

public class LMSpawnListener20 extends LMListenerUtil implements Listener {

    private final HashSet<BoundingBox> noPatrolBoxes = new HashSet<>();// TODO: convert to HashSet of locations, just check whether location is 46 blocks away from any of them

    public LMSpawnListener20(LiminalMod plugin) {super(plugin);}

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCreatureSpawnEvent(CreatureSpawnEvent e) {
        CreatureSpawnEvent.SpawnReason spawnReason = e.getSpawnReason();
        if (!(spawnReason == CreatureSpawnEvent.SpawnReason.NATURAL || spawnReason == CreatureSpawnEvent.SpawnReason.PATROL)) return;
        switch (e.getEntityType()) {
            case ELDER_GUARDIAN:
            case GUARDIAN:
                //TODO: if x and z are near a structure void in the sky, cancel. radius = 56
                break;
            case PILLAGER:
                if (spawnReason == CreatureSpawnEvent.SpawnReason.PATROL) {
                    Location l = e.getLocation();
                    if (partOfCancelledPatrol(l)) {
                        e.setCancelled(true);
                    } else if (chance(plugin.getPluginConfig().getInt("patrol-spawn-rate"))) {
                        e.setCancelled(true);
                        createNoPatrolBox(l);
                    }
                }// TODO: else-if for passive spawning on outpost (if structure void in sky, etc..). radius = 72
                break;
            case PIGLIN:
                // TODO: if x and z are near a structure void in the sky, cancel. radius = 64
                if (chance(plugin.getPluginConfig().getInt("piglin-spawn-rate"))) e.setCancelled(true);
                break;
            case WANDERING_TRADER:
                if (chance(plugin.getPluginConfig().getInt("wandering-trader-spawn-rate"))) e.setCancelled(true);
                break;
        }
    }

    public void createNoPatrolBox(Location l) {// Create a bounding box around location (radius 32) and temporarily add to noPatrolBoxes for 10 seconds
        int RADIUS = 32;
        BoundingBox box = new BoundingBox(l.getX() - RADIUS, l.getY() - RADIUS, l.getZ() - RADIUS, l.getX() + RADIUS, l.getY() + RADIUS, l.getZ() + RADIUS);
        noPatrolBoxes.add(box);

        long PATROL_BOX_LIFETIME = 200L;
        Bukkit.getScheduler().runTaskLater(plugin, () -> noPatrolBoxes.remove(box), PATROL_BOX_LIFETIME);
    }

    public boolean partOfCancelledPatrol(Location l) {// Check if location is within any of the bounding boxes in noPatrolBoxes
        for (BoundingBox box : noPatrolBoxes) {// A small number of bounding boxes is typical, which means O(n) should be fine (no chunk hashmap creation/other data structures)
            if (box.contains(l.getX(), l.getY(), l.getZ())) return true;
        }
        return false;
    }
}