package com.lichenaut.liminalmod.util;

import com.lichenaut.liminalmod.LiminalMod;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.entity.ZombieVillager;
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
}
