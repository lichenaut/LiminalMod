package com.lichenaut.liminalmod.listening.passive.twenty;

import com.lichenaut.liminalmod.LiminalMod;
import com.lichenaut.liminalmod.load.LMStructure;
import com.lichenaut.liminalmod.util.LMListenerUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.generator.structure.Structure;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BoundingBox;

import java.util.HashSet;

public class LMSpawnListener20 extends LMListenerUtil implements Listener {

    private HashSet<BoundingBox> noPatrolBoxes = new HashSet<>();

    public LMSpawnListener20(LiminalMod plugin) {super(plugin);}

    @EventHandler
    public void onEntitySpawnEvent(EntitySpawnEvent e) {
        switch (e.getEntityType()) {
            case WANDERING_TRADER:
                if (chance(plugin.getPluginConfig().getInt("wandering-trader-spawn-rate"))) e.setCancelled(true);
                break;
            case PILLAGER:
                if (e.getEntity().getEntitySpawnReason() == CreatureSpawnEvent.SpawnReason.PATROL) {
                    Location l = e.getLocation();
                    if (partOfCancelledPatrol(l)) {
                        e.setCancelled(true);
                    } else if (chance(plugin.getPluginConfig().getInt("patrol-spawn-rate"))) {
                        e.setCancelled(true);
                        createNoPatrolBox(l);
                    }
                }
                break;
            case PIGLIN:
                HashSet<LMStructure> chunkStructures = plugin.getStructureChunks().get(e.getLocation().getChunk());
                if (chunkStructures != null) {
                    for (LMStructure structure : chunkStructures) {
                        if (structure.getStructureType() == Structure.BASTION_REMNANT && structure.getAbandoned()) {
                            e.setCancelled(true);
                            return;
                        }
                    }
                }

                if (chance(plugin.getPluginConfig().getInt("piglin-spawn-rate"))) e.setCancelled(true);
                break;
            default:
                break;
        }
    }

    public void createNoPatrolBox(Location l) {//create a bounding box around location (radius 32) and temporarily add to noPatrolBoxes for 10 seconds
        BoundingBox box = new BoundingBox(l.getX() - 32, l.getY() - 32, l.getZ() - 32, l.getX() + 32, l.getY() + 32, l.getZ() + 32);
        noPatrolBoxes.add(box);

        Bukkit.getScheduler().runTaskLater((Plugin) this, new Runnable() {
            @Override
            public void run() {noPatrolBoxes.remove(box);}
        }, 200L);
    }

    public boolean partOfCancelledPatrol(Location l) {//check if location is within any of the bounding boxes in noPatrolBoxes
        for (BoundingBox box : noPatrolBoxes) {//a small number of bounding boxes is typical, which means O(n) is fine (no chunk hashmap creation/other data structures)
            if (box.contains(l.getX(), l.getY(), l.getZ())) return true;
        }
        return false;
    }

    public int getPiglinCount() {
        int piglinCount = 0;
        for (World w : plugin.getServer().getWorlds()) {
            assert EntityType.PIGLIN.getEntityClass() != null;
            piglinCount += w.getEntitiesByClass(EntityType.PIGLIN.getEntityClass()).size();
        }
        return piglinCount;
    }
}