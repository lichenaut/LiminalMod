package com.lichenaut.liminalmod.abandonment;

import com.lichenaut.liminalmod.LiminalMod;
import com.lichenaut.liminalmod.util.LMListenerUtil;
import org.bukkit.World;
import org.bukkit.util.BoundingBox;

public abstract class LMAbandoner extends LMListenerUtil {

    public LMAbandoner(LiminalMod plugin) {super(plugin);}

    public abstract void nerfLoot(World w, BoundingBox box, int abandonLootRate, int batchSize, boolean hasCarts);
    public abstract void endCityAbandon(World w, BoundingBox box, int batchSize);
    public abstract void bastionAbandon(World w, BoundingBox box, int batchSize);
    public abstract void monumentAbandon(World w, BoundingBox box, int abandonLootRate, int batchSize);
    public abstract void overworldSettlementAbandon(World w, BoundingBox box, int batchSize);

}