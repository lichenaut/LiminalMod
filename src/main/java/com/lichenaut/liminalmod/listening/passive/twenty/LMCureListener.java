package com.lichenaut.liminalmod.listening.passive.twenty;

import com.lichenaut.liminalmod.LiminalMod;
import com.lichenaut.liminalmod.util.LMListenerUtil;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTransformEvent;

public class LMCureListener extends LMListenerUtil implements Listener {

    public LMCureListener(LiminalMod plugin) {super(plugin);}

    @EventHandler
    public void onVillagerCure(EntityTransformEvent e) {
        if (e.getTransformReason() == EntityTransformEvent.TransformReason.CURED && e.getTransformedEntity().getType() == EntityType.VILLAGER && e.getEntityType() == EntityType.ZOMBIE_VILLAGER) {
            ZombieVillager zombieVillager = (ZombieVillager) e.getEntity();
            Villager villager = (Villager) e.getTransformedEntity();
            if (zombieVillager.hasMetadata("level")) {villager.setVillagerLevel(zombieVillager.getMetadata("level").get(0).asInt());}
            if (zombieVillager.hasMetadata("abandoned")) {villager.removeMetadata("abandoned", plugin);}
        }
    }
}