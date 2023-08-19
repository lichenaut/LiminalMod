package com.lichenaut.liminalmod.listening.twenty.gen;

import com.lichenaut.liminalmod.LiminalMod;
import com.lichenaut.liminalmod.abandonment.LMAbandoner;
import com.lichenaut.liminalmod.abandonment.twenty.LMAbandoner20;
import com.lichenaut.liminalmod.load.LMStructureProperties;
import com.lichenaut.liminalmod.util.LMMiscUtil;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.AsyncStructureSpawnEvent;
import org.bukkit.generator.structure.Structure;
import org.bukkit.generator.structure.StructureType;
import org.bukkit.util.BoundingBox;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


public class LMStructureListener20 extends LMMiscUtil implements Listener {

    private final int version;
    private final Configuration config = plugin.getConfig();
    private final int blocksPerTick = config.getInt("blocks-per-tick", 0);
    private final int ticksPerChunk = config.getInt("ticks-per-chunk", 5);
    private final Map<Structure, LMStructureProperties> structureProperties = new HashMap<>();
    private final Queue<AsyncStructureSpawnEvent> structuresToProcess = new LinkedList<>();
    private boolean isProcessing = false;
    private final List<EntityType> BASTION_TYPES =  List.of(EntityType.PIGLIN, EntityType.PIGLIN_BRUTE, EntityType.HOGLIN);
    private final List<EntityType> MONUMENT_TYPES = List.of(EntityType.GUARDIAN, EntityType.ELDER_GUARDIAN);

    public LMStructureListener20(LiminalMod plugin, int version) {
        super(plugin);
        this.version = version;

        ConfigurationSection structuresSection = config.getConfigurationSection("structures");
        if (structuresSection == null) return;
        for (String structure : structuresSection.getKeys(false)) {
            ConfigurationSection structureSection = structuresSection.getConfigurationSection(structure);
            if (structureSection == null) continue;
            structureProperties.put(getStructureByName(structure), new LMStructureProperties(structureSection.getInt("spawn-rate", 100), structureSection.getInt("abandoned-rate", 0), structureSection.getInt("loot-abandon-rate", 0), structureSection.getBoolean("transform-structure", false)));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onStructureSpawn(AsyncStructureSpawnEvent e) {Bukkit.getScheduler().runTask(plugin, () -> addEvent(e));}

    public void addEvent(AsyncStructureSpawnEvent e) {
        structuresToProcess.add(e);
        processNext();
    }

    private void processNext() {
        if (isProcessing || structuresToProcess.isEmpty()) return;

        AsyncStructureSpawnEvent e = structuresToProcess.poll();
        if (e == null) return;

        Structure structure = e.getStructure();
        if (!structureProperties.containsKey(structure)) return;

        LMStructureProperties properties = structureProperties.get(structure);
        if (chance(100 - properties.getSpawnRate())) {e.setCancelled(true); return;}

        StructureType structureType = structure.getStructureType();
        if (structureType == StructureType.NETHER_FOSSIL) return;// The only structure that is neither lootable nor an abandon-able structure



        boolean abandoned = chance(properties.getAbandonedRate());
        if (!abandoned) return;

        LMAbandoner abandoner;
        if (version >= 20) abandoner = new LMAbandoner20(plugin); else return;// Expands with each version

        isProcessing = true;
        System.out.println("processing structure");
        processNextStructureEvent(structure, abandoner, e.getWorld(), e.getBoundingBox(), properties.getLootAbandonRate(), () -> {
            isProcessing = false;
            System.out.println("finished processing structure");
            processNext();
        });
    }

    private void processNextStructureEvent(Structure structure, LMAbandoner abandoner, World w, BoundingBox box, int abandonLootRate, Runnable callback) {
        Chunk minChunk = box.getMin().toLocation(w).getChunk();
        Chunk maxChunk = box.getMax().toLocation(w).getChunk();

        int x1 = minChunk.getX(), z1 = minChunk.getZ();
        int x2 = maxChunk.getX(), z2 = maxChunk.getZ();

        int minX = Math.min(x1, x2);
        int minZ = Math.min(z1, z2);
        int maxX = Math.max(x1, x2);
        int maxZ = Math.max(z1, z2);

        Queue<Chunk> chunkQueue = new LinkedList<>();
        for (int x = minX; x <= maxX; x++) for (int z = minZ; z <= maxZ; z++) chunkQueue.add(w.getChunkAt(x, z));

        AtomicInteger taskID = new AtomicInteger();
        taskID.set(Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            if (!chunkQueue.isEmpty()) {
                Chunk chunk = chunkQueue.poll();
                if (!chunk.isLoaded()) chunk.load();
            } else {
                boolean transformStructure = structureProperties.get(structure).getTransformStructure();
                // A huge amount of if-else-s is the only way I found out how to do this sadly
                // Continuous spawning of Evokers and Vindicators stops when all have been killed, but this does not apply to Allays
                if (structure.equals(Structure.ANCIENT_CITY) || structure.equals(Structure.BURIED_TREASURE) || structure.equals(Structure.DESERT_PYRAMID) || structure.equals(Structure.FORTRESS) || structure.equals(Structure.IGLOO) || structure.equals(Structure.JUNGLE_PYRAMID) || structure.equals(Structure.NETHER_FOSSIL) || structure.equals(Structure.OCEAN_RUIN_COLD) || structure.equals(Structure.OCEAN_RUIN_WARM) || structure.equals(Structure.RUINED_PORTAL) || structure.equals(Structure.RUINED_PORTAL_DESERT) || structure.equals(Structure.RUINED_PORTAL_JUNGLE) || structure.equals(Structure.RUINED_PORTAL_MOUNTAIN) || structure.equals(Structure.RUINED_PORTAL_NETHER) || structure.equals(Structure.RUINED_PORTAL_OCEAN) || structure.equals(Structure.RUINED_PORTAL_SWAMP) || structure.equals(Structure.SHIPWRECK) || structure.equals(Structure.SHIPWRECK_BEACHED) || structure.equals(Structure.STRONGHOLD) || structure.equals(Structure.TRAIL_RUINS)) {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        if (transformStructure && blocksPerTick > 0) abandoner.nerfLoot(w, box, abandonLootRate, blocksPerTick, callback);
                        callback.run();
                    });
                    // Non-Minecart Chest loot generators, not abandon-able
                } else if (structure.equals(Structure.BASTION_REMNANT)) {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        if (transformStructure && blocksPerTick > 0) abandoner.nerfLoot(w, box, abandonLootRate, blocksPerTick, callback);
                        plugin.addAbandonmentMarker(box.getCenter().toLocation(w));
                        abandoner.bastionAbandon(w, box, BASTION_TYPES);
                        callback.run();
                    });// Special case
                } else if (structure.equals(Structure.END_CITY)) {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        if (transformStructure && blocksPerTick > 0) abandoner.nerfLoot(w, box, abandonLootRate, blocksPerTick, callback);
                        abandoner.endCityAbandon(w, box);
                        callback.run();
                    });// Special case
                } else if (structure.equals(Structure.MONUMENT)) {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        if (transformStructure && blocksPerTick > 0) abandoner.nerfLoot(w, box, abandonLootRate, blocksPerTick, callback);
                        plugin.addAbandonmentMarker(box.getCenter().toLocation(w));
                        abandoner.monumentAbandon(w, box, MONUMENT_TYPES);
                        callback.run();
                    });// Special case
                } else if (structure.equals(Structure.SWAMP_HUT)) {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        abandoner.overworldSettlementAbandon(w, box, blocksPerTick, (transformStructure && blocksPerTick > 0), callback);
                        callback.run();
                    });// Special case
                } else if (structure.equals(Structure.MINESHAFT) || structure.equals(Structure.MINESHAFT_MESA)) {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        if (transformStructure && blocksPerTick > 0) {
                            abandoner.nerfLoot(w, box, abandonLootRate, blocksPerTick, callback);
                            abandoner.nerfMinecartChests(w, box, abandonLootRate);
                        }
                        callback.run();
                    });// Has-Minecart Chests loot generators
                } else if (structure.equals(Structure.MANSION) || structure.equals(Structure.PILLAGER_OUTPOST)) {
                    // Plugin does not have to check for continuous Iron Golem spawning, as they will not spawn in an abandoned village
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        plugin.addAbandonmentMarker(box.getCenter().toLocation(w));
                        if (transformStructure && blocksPerTick > 0) abandoner.nerfLoot(w, box, abandonLootRate, blocksPerTick, callback);
                        abandoner.overworldSettlementAbandon(w, box, blocksPerTick, (transformStructure && blocksPerTick > 0), callback);
                        callback.run();
                    });
                } else if (structure.equals(Structure.VILLAGE_DESERT) || structure.equals(Structure.VILLAGE_PLAINS) || structure.equals(Structure.VILLAGE_SAVANNA) || structure.equals(Structure.VILLAGE_SNOWY) || structure.equals(Structure.VILLAGE_TAIGA)) {// Plugin does not have to check for continuous Iron Golem spawning, as they will not spawn in an abandoned village
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        if (transformStructure && blocksPerTick > 0) abandoner.nerfLoot(w, box, abandonLootRate, blocksPerTick, callback);
                        abandoner.overworldSettlementAbandon(w, box, blocksPerTick, (transformStructure && blocksPerTick > 0), callback);
                        callback.run();
                    });// Characterized by wood/Cobblestone-based structures (except Desert Village), humanoid inhabitants, and non-Minecart Chest loot
                }

                Bukkit.getScheduler().cancelTask(taskID.get());
            }
        }, 0L, ticksPerChunk));
    }
}