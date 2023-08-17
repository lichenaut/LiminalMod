package com.lichenaut.liminalmod.listening.gen.twenty;

import com.lichenaut.liminalmod.LiminalMod;
import com.lichenaut.liminalmod.abandonment.LMAbandoner;
import com.lichenaut.liminalmod.abandonment.twenty.LMAbandoner20;
import com.lichenaut.liminalmod.load.LMStructureProperties;
import com.lichenaut.liminalmod.util.LMListenerUtil;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.AsyncStructureSpawnEvent;
import org.bukkit.generator.structure.Structure;
import org.bukkit.generator.structure.StructureType;
import org.bukkit.util.BoundingBox;

import java.util.HashMap;


public class LMStructureListener20 extends LMListenerUtil implements Listener {

    private final int version;
    private final int batchSize = plugin.getConfig().getInt("blocks-per-tick", 128);
    private final HashMap<Structure, LMStructureProperties> structureProperties = new HashMap<>();

    public LMStructureListener20(LiminalMod plugin, int version) {
        super(plugin);
        this.version = version;

        ConfigurationSection structuresSection = plugin.getConfig().getConfigurationSection("structures");
        if (structuresSection == null) return;
        for (String structure : structuresSection.getKeys(false)) {
            ConfigurationSection structureSection = structuresSection.getConfigurationSection(structure);
            if (structureSection == null) continue;
            structureProperties.put(getStructureByName(structure), new LMStructureProperties(structureSection.getInt("spawn-rate"), structureSection.getInt("abandoned-rate"), structureSection.getInt("loot-abandon-rate")));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onStructureSpawn(AsyncStructureSpawnEvent e) {
        Structure structure = e.getStructure();
        if (!structureProperties.containsKey(structure)) return;
        LMStructureProperties properties = structureProperties.get(structure);
        if (chance(100 - properties.getSpawnRate())) {e.setCancelled(true); return;}
        StructureType structureType = structure.getStructureType();
        if (structureType == StructureType.NETHER_FOSSIL) return;// The only structure that is neither lootable nor an abandon-able structure

        boolean abandoned = chance(properties.getAbandonedRate());
        if (!abandoned) return;

        World w = e.getWorld();
        BoundingBox box = e.getBoundingBox();
        Chunk minChunk = box.getMin().toLocation(w).getChunk();
        Chunk maxChunk = box.getMax().toLocation(w).getChunk();

        int x1 = minChunk.getX(), z1 = minChunk.getZ();
        int x2 = maxChunk.getX(), z2 = maxChunk.getZ();

        int minX = Math.min(x1, x2);
        int minZ = Math.min(z1, z2);
        int maxX = Math.max(x1, x2);
        int maxZ = Math.max(z1, z2);

        for (int x = minX; x <= maxX; x++) {// Load all structure chunks so that the whole structure has changes applied to it
            for (int z = minZ; z <= maxZ; z++) {
                Chunk chunk = w.getChunkAt(x, z);
                if (!chunk.isLoaded()) chunk.load();
            }
        }

        LMAbandoner abandoner = null;
        if (version == 20) {
            abandoner = new LMAbandoner20(plugin);
        }// Expands with each version
        if (abandoner == null) return;

        int abandonLootRate = properties.getLootAbandonRate();
        switch (structureType.toString()) {
            case "ANCIENT_CITY":
            case "BURIED_TREASURE":
            case "DESERT_PYRAMID":
            case "END_CITY":
                abandoner.endCityAbandon(w, box, batchSize);
            case "FORTRESS":
            case "IGLOO":
            case "JUNGLE_PYRAMID":
            case "NETHER_FOSSIL":
            case "OCEAN_RUIN_COLD":
            case "OCEAN_RUIN_WARM":
            case "RUINED_PORTAL":
            case "RUINED_PORTAL_DESERT":
            case "RUINED_PORTAL_JUNGLE":
            case "RUINED_PORTAL_MOUNTAIN":
            case "RUINED_PORTAL_NETHER":
            case "RUINED_PORTAL_OCEAN":
            case "RUINED_PORTAL_SWAMP":
            case "SHIPWRECK":
            case "SHIPWRECK_BEACHED":
            case "STRONGHOLD":
            case "TRAIL_RUINS":
                abandoner.nerfLoot(w, box, abandonLootRate, batchSize, false);
                break;
            case "BASTION_REMNANT":
                abandoner.bastionAbandon(w, box, batchSize);
            case "MINESHAFT":
            case "MINESHAFT_MESA":
                abandoner.nerfLoot(w, box, abandonLootRate, batchSize, true);// The Minecart Chest generators
                break;
            case "MONUMENT":
                abandoner.monumentAbandon(w, box, abandonLootRate, batchSize);// Sponge loot abandonment built into monumentAbandon
                break;
            case "MANSION":
            case "PILLAGER_OUTPOST":
            case "SWAMP_HUT":
            case "VILLAGE_DESERT":
            case "VILLAGE_PLAINS":
            case "VILLAGE_SAVANNA":
            case "VILLAGE_SNOWY":
            case "VILLAGE_TAIGA":
                abandoner.overworldSettlementAbandon(w, box, batchSize);
                abandoner.nerfLoot(w, box, abandonLootRate, batchSize, false);
        }
    }
}