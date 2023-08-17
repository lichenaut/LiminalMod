package com.lichenaut.liminalmod.abandonment.twenty;

import com.lichenaut.liminalmod.LiminalMod;
import com.lichenaut.liminalmod.abandonment.LMAbandoner;
import com.lichenaut.liminalmod.abandonment.LMBlockIterator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Villager;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class LMAbandoner20 extends LMAbandoner {

    public LMAbandoner20(LiminalMod plugin) {super(plugin);}

    public void nerfLoot(World w, BoundingBox box, int abandonLootRate, int batchSize, boolean hasCarts) {
        AtomicInteger taskID = new AtomicInteger();
        LMBlockIterator bi = new LMBlockIterator(w, box);

        taskID.set(Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            int batchCount = 0;
            while (bi.hasNext() && batchCount < batchSize) {
                Block block = bi.next();
                if (block.getType() == Material.CHEST) {
                    BlockState state = block.getState();

                    if (!(state instanceof Chest)) continue;
                    Chest chest = (Chest) state;

                    nerfInventoryContents(chest.getInventory(), abandonLootRate);
                }

                batchCount++;
            }

            if (!bi.hasNext()) {
                if (hasCarts) {
                    for (Entity entity : w.getNearbyEntities(box)) {
                        if (entity instanceof StorageMinecart) nerfInventoryContents(((StorageMinecart) entity).getInventory(), abandonLootRate);
                    }
                }

                Bukkit.getScheduler().cancelTask(taskID.get());
            }
        }, 0L, 1L));
    }

    private void nerfInventoryContents(Inventory inv, int abandonLootRate) {
        if (inv.isEmpty()) return;

        List<ItemStack> items = new ArrayList<>(Arrays.asList(inv.getContents()));
        items.removeIf(Objects::isNull);

        int removeCount = (int) Math.round(items.size() * (abandonLootRate / 100.0));
        Set<Integer> removedIndices = new HashSet<>();// Keeps track of which indices have already been removed
        Random rand = new Random();
        while (removedIndices.size() < removeCount) {
            int indexToRemove = rand.nextInt(items.size());
            if (!removedIndices.contains(indexToRemove)) {
                inv.remove(items.get(indexToRemove));
                removedIndices.add(indexToRemove);
            }
        }
    }

    public void endCityAbandon(World w, BoundingBox box, int batchSize) {

    }

    public void bastionAbandon(World w, BoundingBox box, int batchSize) {

    }

    public void monumentAbandon(World w, BoundingBox box, int abandonLootRate, int batchSize) {

    }

    public void overworldSettlementAbandon(World w, BoundingBox box, int batchSize) {
        AtomicInteger taskID = new AtomicInteger();
        LMBlockIterator bi = new LMBlockIterator(w, box);

        taskID.set(Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            int batchCount = 0;
            while (bi.hasNext() && batchCount < batchSize) {
                Block block = bi.next();
                switch (block.getType()) {
                    case ACACIA_DOOR:
                    case JUNGLE_DOOR:
                    case LANTERN:
                    case OAK_DOOR:
                    case SPRUCE_DOOR:
                    case TORCH:
                    case WALL_TORCH:
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
                    case DARK_OAK_FENCE:// "Did the Iron Golems and Allay escape from the outpost?"
                        if (chance(75)) block.setType(Material.AIR);
                        break;
                    case GLASS_PANE:
                        block.setType(Material.BROWN_STAINED_GLASS_PANE);
                        break;
                }

                batchCount++;
            }

            if (!bi.hasNext()) Bukkit.getScheduler().cancelTask(taskID.get());
        }, 0L, 1L));

        for (Entity entity : w.getNearbyEntities(box)) {
            switch (entity.getType()) {
                case ALLAY:
                case CAT:
                    if (entity.getWorld().getBiome(entity.getLocation()) != Biome.SWAMP) break;
                case EVOKER:
                case IRON_GOLEM:
                case PILLAGER:
                case VINDICATOR:
                case WITCH:
                    entity.remove();
                    break;
                case VILLAGER:
                    infectVillager(w, (Villager) entity);
            }
        }
    }
}