package com.huskydreaming.medieval.brewery.data;

import com.huskydreaming.medieval.brewery.storage.Message;
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

    public static Hologram create(NamespacedKey namespacedKey, Block block, String header, String footer) {
        return new Hologram(namespacedKey, block, header, footer);
    }

    public static Hologram create(NamespacedKey namespacedKey, Block block) {
        return new Hologram(namespacedKey, block, Message.TITLE_IDLE_HEADER.parse(), Message.TITLE_IDLE_FOOTER.parse());
    }

    public Hologram(NamespacedKey namespacedKey, Block block, String header, String footer) {
        Location location = block.getLocation();
        location.add(0.5, 0, 0.5);
        location.subtract(0, 0.5, 0);

        this.header = create(namespacedKey, location, header);
        this.footer = create(namespacedKey, location.subtract(0, 0.25, 0), footer);
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