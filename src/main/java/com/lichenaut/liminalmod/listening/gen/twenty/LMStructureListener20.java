package com.lichenaut.liminalmod.listening.gen.twenty;

import com.lichenaut.liminalmod.LiminalMod;
import com.lichenaut.liminalmod.abandonment.LMAbandoner;
import com.lichenaut.liminalmod.abandonment.twenty.LMAbandoner20;
import com.lichenaut.liminalmod.listening.LMStructureEvent;
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
    private final int blocksPerTick = config.getInt("blocks-per-tick", 2048);
    private final int ticksPerChunk = config.getInt("ticks-per-chunk", 5);
    private final Map<Structure, LMStructureProperties> structureProperties = new HashMap<>();
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
            structureProperties.put(getStructureByName(structure), new LMStructureProperties(structureSection.getInt("spawn-rate", 100), structureSection.getInt("abandoned-rate", 0), structureSection.getInt("loot-abandon-rate", 0)));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onStructureSpawn(AsyncStructureSpawnEvent e) {
        plugin.addStructureEvent(new LMStructureEvent(e));
    }

    private void processNextStructureEvent() {//TODO: this is a mess
        if (plugin.getStructureEvents().isEmpty()) return;
        AsyncStructureSpawnEvent e = plugin.getStructureEvents().poll().getEvent();
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
        Bukkit.getScheduler().runTaskLater(plugin, () -> loadRelevantChunks(e.getWorld(), e.getBoundingBox(), structure, properties.getLootAbandonRate(), abandoner), 20L);
    }

    private void loadRelevantChunks(World w, BoundingBox box, Structure structure, int abandonLootRate, LMAbandoner abandoner) {
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
        taskID.set(Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {// TODO: add another parameter to these functions to tell them they're the last function called in an if statement. that way you can make a callback to say the abandonment was completed
            if (!chunkQueue.isEmpty()) {
                Chunk chunk = chunkQueue.poll();
                if (!chunk.isLoaded()) chunk.load();
            } else {// Case-switch-ing not supported for this data type
                // Continuous spawning of Evokers and Vindicators stops when all have been killed, but this does not apply to Allays
                if (structure.equals(Structure.ANCIENT_CITY) || structure.equals(Structure.BURIED_TREASURE) || structure.equals(Structure.DESERT_PYRAMID) || structure.equals(Structure.FORTRESS) || structure.equals(Structure.IGLOO) || structure.equals(Structure.JUNGLE_PYRAMID) || structure.equals(Structure.NETHER_FOSSIL) || structure.equals(Structure.OCEAN_RUIN_COLD) || structure.equals(Structure.OCEAN_RUIN_WARM) || structure.equals(Structure.RUINED_PORTAL) || structure.equals(Structure.RUINED_PORTAL_DESERT) || structure.equals(Structure.RUINED_PORTAL_JUNGLE) || structure.equals(Structure.RUINED_PORTAL_MOUNTAIN) || structure.equals(Structure.RUINED_PORTAL_NETHER) || structure.equals(Structure.RUINED_PORTAL_OCEAN) || structure.equals(Structure.RUINED_PORTAL_SWAMP) || structure.equals(Structure.SHIPWRECK) || structure.equals(Structure.SHIPWRECK_BEACHED) || structure.equals(Structure.STRONGHOLD) || structure.equals(Structure.TRAIL_RUINS)) {
                    abandoner.nerfLoot(w, box, abandonLootRate, blocksPerTick, false);
                    // Non-Minecart Chest loot generators, not abandon-able
                } else if (structure.equals(Structure.BASTION_REMNANT)) {
                    abandoner.nerfLoot(w, box, abandonLootRate, blocksPerTick, false);
                    plugin.addAbandonmentMarker(box.getCenter().toLocation(w));
                    abandoner.bastionAbandon(w, box, BASTION_TYPES);
                    // Special case
                } else if (structure.equals(Structure.END_CITY)) {
                    abandoner.nerfLoot(w, box, abandonLootRate, blocksPerTick, false);
                    abandoner.endCityAbandon(w, box);
                    // Special case
                } else if (structure.equals(Structure.MONUMENT)) {
                    abandoner.nerfLoot(w, box, abandonLootRate, blocksPerTick, false);
                    plugin.addAbandonmentMarker(box.getCenter().toLocation(w));
                    abandoner.monumentAbandon(w, box, MONUMENT_TYPES);
                    // Special case
                } else if (structure.equals(Structure.SWAMP_HUT)) {
                    abandoner.overworldSettlementAbandon(w, box, blocksPerTick);
                    // Special case
                } else if (structure.equals(Structure.MINESHAFT) || structure.equals(Structure.MINESHAFT_MESA)) {
                    abandoner.nerfLoot(w, box, abandonLootRate, blocksPerTick, true);
                    // Has-Minecart Chests loot generators
                } else if (structure.equals(Structure.MANSION) || structure.equals(Structure.PILLAGER_OUTPOST)) {
                    plugin.addAbandonmentMarker(box.getCenter().toLocation(w));
                    // Plugin does not have to check for continuous Iron Golem spawning, as they will not spawn in an abandoned village
                    abandoner.nerfLoot(w, box, abandonLootRate, blocksPerTick, false);
                    abandoner.overworldSettlementAbandon(w, box, blocksPerTick);
                    // Characterized by wood/Cobblestone-based structures (except Desert Village), humanoid inhabitants, and non-Minecart Chest loot
                } else if (structure.equals(Structure.VILLAGE_DESERT) || structure.equals(Structure.VILLAGE_PLAINS) || structure.equals(Structure.VILLAGE_SAVANNA) || structure.equals(Structure.VILLAGE_SNOWY) || structure.equals(Structure.VILLAGE_TAIGA)) {// Plugin does not have to check for continuous Iron Golem spawning, as they will not spawn in an abandoned village
                    abandoner.nerfLoot(w, box, abandonLootRate, blocksPerTick, false);
                    abandoner.overworldSettlementAbandon(w, box, blocksPerTick);
                    // Characterized by wood/Cobblestone-based structures (except Desert Village), humanoid inhabitants, and non-Minecart Chest loot
                }

                Bukkit.getScheduler().cancelTask(taskID.get());
            }
        }, 0L, ticksPerChunk));
    }
}