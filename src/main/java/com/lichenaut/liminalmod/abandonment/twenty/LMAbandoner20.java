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
import org.bukkit.entity.EntityType;
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
                Material type = block.getType();
                if (type == Material.CHEST) {
                    BlockState state = block.getState();

                    if (!(state instanceof Chest)) continue;
                    Chest chest = (Chest) state;

                    nerfInventoryContents(chest.getInventory(), abandonLootRate);
                } else if (type == Material.WET_SPONGE)
                    if (chance(abandonLootRate)) block.setType(Material.AIR);

                batchCount++;
            }

            if (!bi.hasNext()) {
                if (hasCarts)
                    w.getNearbyEntities(box).stream().filter(entity -> entity instanceof StorageMinecart).forEach(entity -> nerfInventoryContents(((StorageMinecart) entity).getInventory(), abandonLootRate));

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

    public void endCityAbandon(World w, BoundingBox box) {
        w.getNearbyEntities(box).stream().filter(entity -> entity.getType() == EntityType.SHULKER).forEach(Entity::remove);
    }

    public void bastionAbandon(World w, BoundingBox box, List<EntityType> types) {
        w.getNearbyEntities(box).stream().filter(entity -> types.contains(entity.getType())).forEach(Entity::remove);
    }

    public void monumentAbandon(World w, BoundingBox box, List<EntityType> types) {
        w.getNearbyEntities(box).stream().filter(entity -> types.contains(entity.getType())).forEach(Entity::remove);
    }

    public void overworldSettlementAbandon(World w, BoundingBox box, int batchSize) {
        for (Entity entity : w.getNearbyEntities(box)) {
            switch (entity.getType()) {
                case CAT:
                    if (entity.getWorld().getBiome(entity.getLocation()) != Biome.SWAMP) break;
                case ALLAY:
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

        AtomicInteger taskID = new AtomicInteger();
        LMBlockIterator bi = new LMBlockIterator(w, box);
        boolean escaped = chance(50);

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
                    case ACACIA_LOG:
                    case DARK_OAK_LOG:
                    case OAK_LOG:
                    case SPRUCE_LOG:
                        if (isTreeBlock(block)) continue;
                    case ACACIA_FENCE:
                    case ACACIA_PLANKS:
                    case ACACIA_STAIRS:
                    case BIRCH_PLANKS:
                    case CUT_SANDSTONE:
                    case DARK_OAK_PLANKS:
                    case DARK_OAK_SLAB:
                    case DARK_OAK_STAIRS:
                    case OAK_FENCE:
                    case OAK_PLANKS:
                    case OAK_STAIRS:
                    case ORANGE_TERRACOTTA:
                    case SANDSTONE:
                    case SANDSTONE_SLAB:
                    case SANDSTONE_STAIRS:
                    case SMOOTH_SANDSTONE:
                    case SPRUCE_FENCE:
                    case SPRUCE_PLANKS:
                    case SPRUCE_STAIRS:
                    case STRIPPED_ACACIA_LOG:
                    case STRIPPED_OAK_LOG:
                    case STRIPPED_SPRUCE_LOG:
                        if (chance(10)) block.setType(Material.COBWEB);
                        break;
                    case COBBLESTONE:
                        if (chance(75)) block.setType(Material.MOSSY_COBBLESTONE);
                        break;
                    case COBBLESTONE_SLAB:
                        if (chance(75)) block.setType(Material.MOSSY_COBBLESTONE_SLAB);
                        break;
                    case COBBLESTONE_WALL:
                        if (chance(75)) block.setType(Material.MOSSY_COBBLESTONE_WALL);
                        break;
                    case DARK_OAK_FENCE:// "Did the Iron Golems and Allay escape from the outpost?"
                        Material highest = block.getWorld().getHighestBlockAt(block.getLocation()).getType();
                        if (highest == Material.WHITE_WOOL || highest == Material.HAY_BLOCK) return;
                        if (escaped) block.setType(Material.AIR); else if (chance(10)) block.setType(Material.COBWEB);
                        break;
                    case GLASS_PANE:
                        block.setType(Material.BROWN_STAINED_GLASS_PANE);
                }

                batchCount++;
            }

            if (!bi.hasNext()) Bukkit.getScheduler().cancelTask(taskID.get());
        }, 0L, 1L));
    }

    public boolean isTreeBlock(Block b) {
        Block highest = b.getWorld().getHighestBlockAt(b.getLocation());
        return highest.getType() == Material.OAK_LEAVES || highest.getType() == Material.DARK_OAK_LEAVES || highest.getType() == Material.ACACIA_LEAVES || highest.getType() == Material.SPRUCE_LEAVES || highest.getType() == Material.BIRCH_LEAVES || highest.getType() == Material.JUNGLE_LEAVES || highest.getType() == Material.AZALEA_LEAVES || highest.getType() == Material.MANGROVE_LEAVES || highest.getType() == Material.CHERRY_LEAVES;
    }
}