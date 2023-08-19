package com.lichenaut.liminalmod.listening.twenty.passive;

import com.lichenaut.liminalmod.LiminalMod;
import com.lichenaut.liminalmod.util.LMMiscUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LMTargetListener20 extends LMMiscUtil implements Listener {

    private final Map<UUID, Long> cooldowns = new HashMap<>();// Minor efficiency improvement to use a cooldown.

    public LMTargetListener20(LiminalMod plugin) {super(plugin);}

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onZombieVillagerTarget(EntityTargetEvent e) {//Zombie Villagers in normal abandoned villages will not endanger themselves to attack.
        Entity entity = e.getEntity();

        Long cooldown = cooldowns.get(entity.getUniqueId());
        if (cooldown != null && (System.currentTimeMillis() - cooldown) < 4000) {// If it tried to target something within the last 4 seconds, cancel.
            e.setCancelled(true);
            return;
        }

        Entity target = e.getTarget();

        if (!entity.hasMetadata("abandoned")) return;
        if (target == null) return;
        if (entity.getType() != EntityType.ZOMBIE_VILLAGER) return;

        World w = target.getWorld();
        Location loc = target.getLocation();
        int highest = w.getHighestBlockYAt(loc);

        if (w.getTime() < 12000 && loc.getY() > highest) {// Cancel targeting if target is exposed to sunlight
            e.setCancelled(true);
            cooldowns.put(entity.getUniqueId(), System.currentTimeMillis());// Add to map

            Bukkit.getScheduler().runTaskLater(plugin, () -> cooldowns.remove(entity.getUniqueId()), 80);// Clean up
        }
    }
}
