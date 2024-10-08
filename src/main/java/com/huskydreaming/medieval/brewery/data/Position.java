package com.huskydreaming.medieval.brewery.data;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.Objects;
import java.util.UUID;

public class Position {

    private final int x;
    private final int y;
    private final int z;
    private final UUID worldUID;

    public static Position of(Block block) {
        return new Position(block);
    }

    public Position(Block block) {
        x = block.getX();
        y = block.getY();
        z = block.getZ();
        worldUID = block.getWorld().getUID();
    }

    public Block toBlock() {
        World world = Bukkit.getWorld(worldUID);
        if(world == null) return null;
        return world.getBlockAt(x, y, z);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return x == position.x &&
                y == position.y &&
                z == position.z &&
                Objects.equals(worldUID, position.worldUID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z, worldUID);
    }
}