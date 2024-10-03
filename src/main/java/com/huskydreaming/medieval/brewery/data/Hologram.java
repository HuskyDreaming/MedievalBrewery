package com.huskydreaming.medieval.brewery.data;

import com.huskydreaming.medieval.brewery.utils.TextUtils;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.persistence.PersistentDataType;

public class Hologram {

    private final ArmorStand header;
    private final ArmorStand footer;

    public Hologram(NamespacedKey namespacedKey, Block block) {
        Location location = block.getLocation();
        location.add(0.5, 0, 0.5);
        location.subtract(0, 0.5, 0);

        header = create(namespacedKey, location, "#8db1b5Brewery");
        footer = create(namespacedKey, location.subtract(0, 0.25, 0), "#dbd8adOpen barrel to begin!");
    }

    public void update(String header, String footer) {
        this.header.setCustomName(TextUtils.hex(header));
        this.footer.setCustomName(TextUtils.hex(footer));
    }

    public void delete() {
        header.remove();
        footer.remove();
    }

    private ArmorStand create(NamespacedKey namespacedKey, Location location, String title) {
        World world = location.getWorld();
        if (world == null) return null;

        ArmorStand armorStand = world.spawn(location, ArmorStand.class);
        armorStand.getPersistentDataContainer().set(namespacedKey, PersistentDataType.STRING, "HOLOGRAM");
        armorStand.setGravity(false);
        armorStand.setInvulnerable(true);
        armorStand.setInvisible(true);
        armorStand.setCustomNameVisible(true);
        armorStand.setAI(false);
        armorStand.setCollidable(false);
        armorStand.setCanPickupItems(false);
        armorStand.setCustomName(TextUtils.hex(title));

        return armorStand;
    }
}