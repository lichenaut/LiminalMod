package com.lichenaut.liminalmod.util;

import com.lichenaut.liminalmod.LiminalMod;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.generator.structure.Structure;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;

public class LMListenerUtil {

    protected final LiminalMod plugin;

    public LMListenerUtil(LiminalMod plugin) {this.plugin = plugin;}

    public boolean chance(int chance) {return Math.random() * 100 < chance;}

    @SuppressWarnings("deprecation")
    public void infectVillager(World w, Villager villager) {
        Villager.Profession profession = villager.getProfession();
        Villager.Type type = villager.getVillagerType();
        int level = villager.getVillagerLevel();
        String customName = villager.getName();
        boolean customNameVisible = villager.isCustomNameVisible();
        Collection<PotionEffect> potionEffects = villager.getActivePotionEffects();

        villager.remove();

        ZombieVillager zombie = (ZombieVillager) w.spawnEntity(villager.getLocation(), EntityType.ZOMBIE_VILLAGER);
        zombie.setVillagerProfession(profession);
        zombie.setVillagerType(type);
        zombie.setMetadata("level", new FixedMetadataValue(plugin, level));
        zombie.setCustomName(customName);
        zombie.setCustomNameVisible(customNameVisible);
        zombie.addPotionEffects(potionEffects);
        zombie.setPersistent(true);
        zombie.setMetadata("abandoned", new FixedMetadataValue(plugin, true));
    }

    public Structure getStructureByName(String name) {
        switch (name) {
            case "ANCIENT_CITY":
                return Structure.ANCIENT_CITY;
            case "BASTION_REMNANT":
                return Structure.BASTION_REMNANT;
            case "BURIED_TREASURE":
                return Structure.BURIED_TREASURE;
            case "DESERT_PYRAMID":
                return Structure.DESERT_PYRAMID;
            case "END_CITY":
                return Structure.END_CITY;
            case "FORTRESS":
                return Structure.FORTRESS;
            case "IGLOO":
                return Structure.IGLOO;
            case "JUNGLE_PYRAMID":
                return Structure.JUNGLE_PYRAMID;
            case "MANSION":
                return Structure.MANSION;
            case "MINESHAFT":
                return Structure.MINESHAFT;
            case "MINESHAFT_MESA":
                return Structure.MINESHAFT_MESA;
            case "MONUMENT":
                return Structure.MONUMENT;
            case "NETHER_FOSSIL":
                return Structure.NETHER_FOSSIL;
            case "OCEAN_RUIN_COLD":
                return Structure.OCEAN_RUIN_COLD;
            case "OCEAN_RUIN_WARM":
                return Structure.OCEAN_RUIN_WARM;
            case "PILLAGER_OUTPOST":
                return Structure.PILLAGER_OUTPOST;
            case "RUINED_PORTAL":
                return Structure.RUINED_PORTAL;
            case "RUINED_PORTAL_DESERT":
                return Structure.RUINED_PORTAL_DESERT;
            case "RUINED_PORTAL_JUNGLE":
                return Structure.RUINED_PORTAL_JUNGLE;
            case "RUINED_PORTAL_MOUNTAIN":
                return Structure.RUINED_PORTAL_MOUNTAIN;
            case "RUINED_PORTAL_NETHER":
                return Structure.RUINED_PORTAL_NETHER;
            case "RUINED_PORTAL_OCEAN":
                return Structure.RUINED_PORTAL_OCEAN;
            case "RUINED_PORTAL_SWAMP":
                return Structure.RUINED_PORTAL_SWAMP;
            case "SHIPWRECK":
                return Structure.SHIPWRECK;
            case "SHIPWRECK_BEACHED":
                return Structure.SHIPWRECK_BEACHED;
            case "STRONGHOLD":
                return Structure.STRONGHOLD;
            case "SWAMP_HUT":
                return Structure.SWAMP_HUT;
            case "TRAIL_RUINS":
                return Structure.TRAIL_RUINS;
            case "VILLAGE_DESERT":
                return Structure.VILLAGE_DESERT;
            case "VILLAGE_PLAINS":
                return Structure.VILLAGE_PLAINS;
            case "VILLAGE_SAVANNA":
                return Structure.VILLAGE_SAVANNA;
            case "VILLAGE_SNOWY":
                return Structure.VILLAGE_SNOWY;
            case "VILLAGE_TAIGA":
                return Structure.VILLAGE_TAIGA;
            default:
                throw new IllegalArgumentException("Unknown structure: " + name);
        }
    }
}
