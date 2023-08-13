package com.lichenaut.liminalmod.listening.gen.twenty;

import com.lichenaut.liminalmod.LiminalMod;
import com.lichenaut.liminalmod.load.LMStructure;
import com.lichenaut.liminalmod.util.LMListenerUtil;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.AsyncStructureSpawnEvent;
import org.bukkit.generator.structure.Structure;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BoundingBox;

import java.util.HashSet;


public class LMStructureListener20 extends LMListenerUtil implements Listener {

    private final HashSet<Structure> villages = new HashSet<>(5);

    public LMStructureListener20(LiminalMod plugin) {
        super(plugin);
        villages.add(Structure.VILLAGE_DESERT);
        villages.add(Structure.VILLAGE_PLAINS);
        villages.add(Structure.VILLAGE_SAVANNA);
        villages.add(Structure.VILLAGE_SNOWY);
        villages.add(Structure.VILLAGE_TAIGA);
    }

    @EventHandler
    public void onStructureSpawn(AsyncStructureSpawnEvent e) {
        ConfigurationSection structures = plugin.getConfig().getConfigurationSection("structures");
        if (structures == null) return;
        Structure structure = e.getStructure();
        ConfigurationSection structureSection = structures.getConfigurationSection(structure.getStructureType().toString());
        if (structureSection == null) return;
        if (chance(structureSection.getInt("spawn-rate"))) {e.setCancelled(true); return;}

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

        boolean abandoned = chance(structureSection.getInt("abandoned-rate"));
        if (abandoned && villages.contains(structure)) {// Depends on config, but checking for abandoned will usually fail faster
            for (int x = minX; x <= maxX; x++) {
                for (int z = minZ; z <= maxZ; z++) {
                    w.getChunkAt(x, z).load();// Load structure chunks so that the village won't be half-abandoned
                }
            }

            Bukkit.getScheduler().runTaskLater((Plugin) this, new Runnable() {// Abandon village after event completion
                @Override
                public void run() {abandonVillage(w, box);}
            }, 20L);
        }

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                plugin.putStructureChunk(w.getChunkAt(x, z), new LMStructure(structure, abandoned));
            }
        }
    }

    public void abandonVillage(World w, BoundingBox box) {
        for (int x = (int) box.getMinX(); x <= (int) box.getMaxX()+1; x++) {
            for (int y = (int) box.getMinY(); y <= (int) box.getMaxY()+1; y++) {
                for (int z = (int) box.getMinZ(); z <= (int) box.getMaxZ()+1; z++) {
                    Block block = w.getBlockAt(x, y, z);
                    switch (block.getType()) {
                        case TORCH:
                        case OAK_DOOR:
                        case JUNGLE_DOOR:
                        case ACACIA_DOOR:
                        case SPRUCE_DOOR:
                            block.setType(Material.AIR);
                            break;
                        case COBBLESTONE:
                            if (chance(75)) block.setType(Material.MOSSY_COBBLESTONE);
                            break;
                            // TODO: Add more blocks
                    }
                    if (block.getType() == Material.TORCH || block.getType().toString().contains("DOOR")) {
                        block.setType(Material.AIR);
                    }
                }
            }
        }

        for (Entity entity : w.getNearbyEntities(box)) {
            EntityType type = entity.getType();
            if (type == EntityType.IRON_GOLEM) {
                entity.remove();
            } else if (type == EntityType.VILLAGER) {
                infectVillager(w, (Villager) entity);
            }
        }
    }
}
