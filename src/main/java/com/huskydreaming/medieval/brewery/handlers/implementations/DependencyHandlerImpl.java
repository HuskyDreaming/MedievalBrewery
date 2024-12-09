package com.huskydreaming.medieval.brewery.handlers.implementations;

import com.huskydreaming.huskycore.HuskyPlugin;
import com.huskydreaming.medieval.brewery.handlers.interfaces.DependencyHandler;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import java.util.UUID;

public class DependencyHandlerImpl implements DependencyHandler {

    private boolean worldGuard;

    @Override
    public void initialize(HuskyPlugin plugin) {
        PluginManager pluginManager = plugin.getServer().getPluginManager();
        if (pluginManager.getPlugin("WorldGuard") != null) {
            worldGuard = true;
        }
    }

    @Override
    public boolean isBlockInsideRegion(Player player, Block block) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        Location location = BukkitAdapter.adapt(block.getLocation());
        RegionQuery query = container.createQuery();
        ApplicableRegionSet applicableRegionSet = query.getApplicableRegions(location);
        for(ProtectedRegion region : applicableRegionSet.getRegions()) {
            for(UUID uuid : region.getOwners().getUniqueIds()) {
                if(uuid.equals(player.getUniqueId())) return false;
            }

            for(UUID uuid : region.getMembers().getUniqueIds()) {
                if(uuid.equals(player.getUniqueId())) return false;
            }
        }
        return !applicableRegionSet.getRegions().isEmpty();
    }

    @Override
    public boolean isWorldGuard() {
        return worldGuard;
    }
}