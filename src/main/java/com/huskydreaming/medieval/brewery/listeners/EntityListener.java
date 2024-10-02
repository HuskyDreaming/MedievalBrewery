package com.huskydreaming.medieval.brewery.listeners;

import com.huskydreaming.medieval.brewery.MedievalBreweryPlugin;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class EntityListener implements Listener {

    private final MedievalBreweryPlugin plugin;

    public EntityListener(MedievalBreweryPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager().getType() != EntityType.PLAYER) return;

        Entity entity = event.getEntity();
        if (entity.getType() != EntityType.ARMOR_STAND) return;

        NamespacedKey namespacedKey = plugin.getNamespacedKey();
        PersistentDataContainer persistentDataContainer = entity.getPersistentDataContainer();
        if (persistentDataContainer.has(namespacedKey, PersistentDataType.STRING)) {
            event.setCancelled(true);
        }
    }
}