package com.lichenaut.liminalmod.listening.gen.twenty;

import com.lichenaut.liminalmod.LiminalMod;
import com.lichenaut.liminalmod.load.LMStructure;
import com.lichenaut.liminalmod.util.LMListenerUtil;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.AsyncStructureSpawnEvent;
import org.bukkit.generator.structure.Structure;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


public class LMStructureListener20 extends LMListenerUtil implements Listener {

    private final HashSet<Structure> notLootable = new HashSet<>(3);
    private final HashSet<Structure> villages = new HashSet<>(5);

    public LMStructureListener20(LiminalMod plugin) {
        super(plugin);
        notLootable.add(Structure.MONUMENT);
        notLootable.add(Structure.NETHER_FOSSIL);
        notLootable.add(Structure.SWAMP_HUT);
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
        if (abandoned) {
            for (int x = minX; x <= maxX; x++) {
                for (int z = minZ; z <= maxZ; z++) {
                    w.getChunkAt(x, z).load();// Load structure chunks so that the whole structure has changes applied to it
                }
            }

            if (villages.contains(structure)) Bukkit.getScheduler().runTaskLater(plugin, () -> abandonVillage(w, box), 20L); // Abandon village after this event's completion
            if (!notLootable.contains(structure)) Bukkit.getScheduler().runTaskLater(plugin, () -> nerfLoot(w, box, structureSection), 20L); // Nerf loot after this event's completion
        }

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                plugin.putStructureChunk(w.getChunkAt(x, z), new LMStructure(structure, abandoned));
            }
        }
    }

    public void abandonVillage(World w, BoundingBox box) {// Manually abandon the village, as changing the structure type through the AsyncStructureSpawnEvent is not possible
        for (int x = (int) box.getMinX(); x <= (int) box.getMaxX()+1; x++) {
            for (int y = (int) box.getMinY(); y <= (int) box.getMaxY()+1; y++) {
                for (int z = (int) box.getMinZ(); z <= (int) box.getMaxZ()+1; z++) {
                    Block block = w.getBlockAt(x, y, z);
                    switch (block.getType()) {
                        case ACACIA_DOOR:
                        case JUNGLE_DOOR:
                        case OAK_DOOR:
                        case SPRUCE_DOOR:
                        case TORCH:
                            block.setType(Material.AIR);
                            break;
                        case COBBLESTONE:
                            if (chance(75)) block.setType(Material.MOSSY_COBBLESTONE);
                            break;
                        case ACACIA_FENCE:
                        case ACACIA_LOG:
                        case ACACIA_PLANKS:
                        case ACACIA_STAIRS:
                        case CUT_SANDSTONE:
                        case OAK_FENCE:
                        case OAK_LOG:
                        case OAK_PLANKS:
                        case OAK_STAIRS:
                        case ORANGE_TERRACOTTA:
                        case SANDSTONE:
                        case SANDSTONE_SLAB:
                        case SANDSTONE_STAIRS:
                        case SMOOTH_SANDSTONE:
                        case SPRUCE_FENCE:
                        case SPRUCE_LOG:
                        case SPRUCE_PLANKS:
                        case SPRUCE_STAIRS:
                        case STRIPPED_ACACIA_LOG:
                        case STRIPPED_OAK_LOG:
                        case STRIPPED_SPRUCE_LOG:
                            if (chance(10)) block.setType(Material.COBWEB);
                            break;
                        case GLASS_PANE:
                            block.setType(Material.BROWN_STAINED_GLASS_PANE);
                            break;
                    }
                }
            }
        }

        for (Entity entity : w.getNearbyEntities(box)) {// Remove all Iron Golems, infect all Villagers
            EntityType type = entity.getType();
            if (type == EntityType.IRON_GOLEM) entity.remove();
            else if (type == EntityType.VILLAGER) infectVillager(w, (Villager) entity);
        }
    }

    public void nerfLoot(World w, BoundingBox box, ConfigurationSection structureSection) {
        for (int x = (int) box.getMinX(); x <= (int) box.getMaxX()+1; x++) {
            for (int y = (int) box.getMinY(); y <= (int) box.getMaxY()+1; y++) {
                for (int z = (int) box.getMinZ(); z <= (int) box.getMaxZ()+1; z++) {
                    Block block = w.getBlockAt(x, y, z);
                    Material type = block.getType();
                    if (type == Material.CHEST || type == Material.CHEST_MINECART) {
                        Inventory inv = null;
                        BlockState state = block.getState();
                        if (state instanceof Chest) {
                            Chest chest = (Chest) state;
                            inv = chest.getInventory();
                        } else if (state instanceof StorageMinecart) {
                            StorageMinecart minecartChest = (StorageMinecart) state;
                            inv = minecartChest.getInventory();
                        }

                        if (inv == null) return;

                        List<ItemStack> items = new ArrayList<>();
                        for (ItemStack item : inv.getContents()) {
                            if (item != null) items.add(item);
                        }

                        int remove = (int) Math.round(items.size() * (structureSection.getInt("loot-multiplier") / 100.0));
                        for (int i = 0; i < remove; i++) inv.remove(items.get(i));
                    }
                }
            }
        }
    }
}