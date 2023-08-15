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
import org.bukkit.generator.structure.Structure;
import org.bukkit.util.BoundingBox;

import java.util.Arrays;
import java.util.HashSet;

public class LMSpawnListener20 extends LMListenerUtil implements Listener {

    private final HashSet<BoundingBox> noPatrolBoxes = new HashSet<>();

    public LMSpawnListener20(LiminalMod plugin) {super(plugin);}

    @EventHandler
    public void onCreatureSpawnEvent(CreatureSpawnEvent e) {
        CreatureSpawnEvent.SpawnReason spawnReason = e.getSpawnReason();
        if (!(spawnReason == CreatureSpawnEvent.SpawnReason.NATURAL || spawnReason == CreatureSpawnEvent.SpawnReason.PATROL)) return;
        switch (e.getEntityType()) {
            case ALLAY:
                if (shouldCancelFromAbandonment(e.getLocation().getChunk(), Structure.PILLAGER_OUTPOST, Structure.MANSION)) e.setCancelled(true);
                break;
            case CAT:
            case WITCH:
                if (shouldCancelFromAbandonment(e.getLocation().getChunk(), Structure.SWAMP_HUT)) e.setCancelled(true);
                break;
            case ELDER_GUARDIAN:
            case GUARDIAN:
                if (shouldCancelFromAbandonment(e.getLocation().getChunk(), Structure.MONUMENT)) e.setCancelled(true);
                break;
            case EVOKER:
            case VINDICATOR:
                if (shouldCancelFromAbandonment(e.getLocation().getChunk(), Structure.MANSION)) e.setCancelled(true);
                break;
            case IRON_GOLEM:
                if (shouldCancelFromAbandonment(e.getLocation().getChunk(), Structure.PILLAGER_OUTPOST, Structure.VILLAGE_DESERT, Structure.VILLAGE_SNOWY, Structure.VILLAGE_SAVANNA, Structure.VILLAGE_PLAINS, Structure.VILLAGE_TAIGA)) e.setCancelled(true);
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
                } else if (shouldCancelFromAbandonment(e.getLocation().getChunk(), Structure.PILLAGER_OUTPOST)) e.setCancelled(true);
                break;
            case PIGLIN:
                shouldCancelFromAbandonment(e.getLocation().getChunk(), Structure.BASTION_REMNANT);
                if (chance(plugin.getPluginConfig().getInt("piglin-spawn-rate"))) e.setCancelled(true);
                break;
            case PIGLIN_BRUTE:
                if (shouldCancelFromAbandonment(e.getLocation().getChunk(), Structure.BASTION_REMNANT)) e.setCancelled(true);
                break;
            case SHULKER:
                if (shouldCancelFromAbandonment(e.getLocation().getChunk(), Structure.END_CITY)) e.setCancelled(true);
                break;
            case VILLAGER:
                if (shouldCancelFromAbandonment(e.getLocation().getChunk(), Structure.VILLAGE_DESERT, Structure.VILLAGE_SNOWY, Structure.VILLAGE_SAVANNA, Structure.VILLAGE_PLAINS, Structure.VILLAGE_TAIGA)) infectVillager(e.getEntity().getWorld(), (Villager) e.getEntity());
                break;
            case WANDERING_TRADER:
                if (chance(plugin.getPluginConfig().getInt("wandering-trader-spawn-rate"))) e.setCancelled(true);
                break;
            case WARDEN:
                if (shouldCancelFromAbandonment(e.getLocation().getChunk(), Structure.ANCIENT_CITY)) e.setCancelled(true);
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

    public boolean shouldCancelFromAbandonment(Chunk chunk, Structure relevantStructure) {// Minor efficiency improvement to not create a HashSet for a single structure
        HashSet<LMStructure> chunkStructures = plugin.getStructureChunks().get(chunk);
        if (chunkStructures == null || chunkStructures.isEmpty()) return false;

        for (LMStructure lmStructure : chunkStructures) {
            if (lmStructure.getStructureType().equals(relevantStructure) && lmStructure.getAbandoned()) return true;
        }

        return false;
    }
    
    public boolean shouldCancelFromAbandonment(Chunk chunk, Structure... relevantStructure) {
        HashSet<LMStructure> chunkStructures = plugin.getStructureChunks().get(chunk);
        if (chunkStructures == null || chunkStructures.isEmpty()) return false;
        HashSet<Structure> relevantStructures = new HashSet<>(Arrays.asList(relevantStructure));

        for (LMStructure lmStructure : chunkStructures) {
            if (relevantStructures.contains(lmStructure.getStructureType()) && lmStructure.getAbandoned()) return true;
        }

        return false;
    }
}