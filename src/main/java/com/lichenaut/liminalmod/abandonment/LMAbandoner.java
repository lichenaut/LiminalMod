package com.lichenaut.liminalmod.abandonment;

import com.lichenaut.liminalmod.LiminalMod;
import com.lichenaut.liminalmod.util.LMMiscUtil;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.util.BoundingBox;

import java.util.List;

public abstract class LMAbandoner extends LMMiscUtil {

    public LMAbandoner(LiminalMod plugin) {super(plugin);}

    public abstract void nerfLoot(World w, BoundingBox box, int abandonLootRate, int batchSize, boolean hasCarts);
    public abstract void endCityAbandon(World w, BoundingBox box);
    public abstract void bastionAbandon(World w, BoundingBox box, List<EntityType> types);
    public abstract void monumentAbandon(World w, BoundingBox box, List<EntityType> types);
    public abstract void overworldSettlementAbandon(World w, BoundingBox box, int batchSize);
    public abstract boolean isTreeBlock(Block b);

}