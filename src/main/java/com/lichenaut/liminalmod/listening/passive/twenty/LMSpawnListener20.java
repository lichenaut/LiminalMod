package com.lichenaut.liminalmod.listening.passive.twenty;

import com.lichenaut.liminalmod.LiminalMod;
import com.lichenaut.liminalmod.load.LMStructure;
import com.lichenaut.liminalmod.util.LMListenerUtil;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.generator.structure.Structure;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BoundingBox;

import java.util.Arrays;
import java.util.HashSet;

public class LMSpawnListener20 extends LMListenerUtil implements Listener {

    private final HashSet<BoundingBox> noPatrolBoxes = new HashSet<>();

    public LMSpawnListener20(LiminalMod plugin) {super(plugin);}

    @EventHandler
    public void onEntitySpawnEvent(EntitySpawnEvent e) {
        switch (e.getEntityType()) {
            case ALLAY:
                if (shouldCancelFromAbandonment(e.getLocation().getChunk(), Structure.PILLAGER_OUTPOST, Structure.MANSION)) e.setCancelled(true);
                break;
            case EVOKER:
            case VINDICATOR:
                if (shouldCancelFromAbandonment(e.getLocation().getChunk(), Structure.MANSION)) e.setCancelled(true);
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
            case GUARDIAN:
            case ELDER_GUARDIAN:
                if (shouldCancelFromAbandonment(e.getLocation().getChunk(), Structure.MONUMENT)) e.setCancelled(true);
                break;
            case IRON_GOLEM:
                if (e.getEntity().getEntitySpawnReason() == CreatureSpawnEvent.SpawnReason.BUILD_IRONGOLEM) {return;}
                if (shouldCancelFromAbandonment(e.getLocation().getChunk(), Structure.PILLAGER_OUTPOST, Structure.VILLAGE_DESERT, Structure.VILLAGE_SNOWY, Structure.VILLAGE_SAVANNA, Structure.VILLAGE_PLAINS, Structure.VILLAGE_TAIGA)) e.setCancelled(true);
                break;
            case VILLAGER:
                if (shouldCancelFromAbandonment(e.getLocation().getChunk(), Structure.VILLAGE_DESERT, Structure.VILLAGE_SNOWY, Structure.VILLAGE_SAVANNA, Structure.VILLAGE_PLAINS, Structure.VILLAGE_TAIGA)) infectVillager(e.getEntity().getWorld(), (Villager) e.getEntity());
                break;
            case WITCH:
            case CAT:
                if (shouldCancelFromAbandonment(e.getLocation().getChunk(), Structure.SWAMP_HUT)) e.setCancelled(true);
                break;
            case WANDERING_TRADER:
                if (chance(plugin.getPluginConfig().getInt("wandering-trader-spawn-rate"))) e.setCancelled(true);
                break;
            case PIGLIN:
                shouldCancelFromAbandonment(e.getLocation().getChunk(), Structure.BASTION_REMNANT);

                if (chance(plugin.getPluginConfig().getInt("piglin-spawn-rate"))) e.setCancelled(true);
                break;
            case PIGLIN_BRUTE:
                if (shouldCancelFromAbandonment(e.getLocation().getChunk(), Structure.BASTION_REMNANT)) e.setCancelled(true);
                break;
            case WARDEN:
                if (shouldCancelFromAbandonment(e.getLocation().getChunk(), Structure.ANCIENT_CITY)) e.setCancelled(true);
                break;
            case SHULKER:
                if (shouldCancelFromAbandonment(e.getLocation().getChunk(), Structure.END_CITY)) e.setCancelled(true);
                break;
        }
    }

    public void createNoPatrolBox(Location l) {// Create a bounding box around location (radius 32) and temporarily add to noPatrolBoxes for 10 seconds
        BoundingBox box = new BoundingBox(l.getX() - 32, l.getY() - 32, l.getZ() - 32, l.getX() + 32, l.getY() + 32, l.getZ() + 32);
        noPatrolBoxes.add(box);

        Bukkit.getScheduler().runTaskLater((Plugin) this, () -> noPatrolBoxes.remove(box), 200L);
    }

    public boolean partOfCancelledPatrol(Location l) {// Check if location is within any of the bounding boxes in noPatrolBoxes
        for (BoundingBox box : noPatrolBoxes) {// A small number of bounding boxes is typical, which means O(n) is fine (no chunk hashmap creation/other data structures)
            if (box.contains(l.getX(), l.getY(), l.getZ())) return true;
        }
        return false;
    }

    public boolean shouldCancelFromAbandonment(Chunk chunk, Structure relevantStructure) {
        if (chunk == null || relevantStructure == null) return false;

        HashSet<LMStructure> chunkStructures = plugin.getStructureChunks().get(chunk);
        if (chunkStructures != null && !chunkStructures.isEmpty()) {
            for (LMStructure lmStructure : chunkStructures) {
                if (lmStructure.getStructureType().equals(relevantStructure) && lmStructure.getAbandoned()) return true;
            }
        }

        return false;
    }
    
    public boolean shouldCancelFromAbandonment(Chunk chunk, Structure... relevantStructure) {
        HashSet<Structure> relevantStructures = new HashSet<>(Arrays.asList(relevantStructure));
        if (chunk == null) return false;

        HashSet<LMStructure> chunkStructures = plugin.getStructureChunks().get(chunk);
        if (chunkStructures != null && !chunkStructures.isEmpty()) {
            for (LMStructure lmStructure : chunkStructures) {
                if (relevantStructures.contains(lmStructure.getStructureType()) && lmStructure.getAbandoned()) return true;
            }
        }

        return false;
    }
}