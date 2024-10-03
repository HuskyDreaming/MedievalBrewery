package com.huskydreaming.medieval.brewery.handlers.implementations;

import com.huskydreaming.medieval.brewery.MedievalBreweryPlugin;
import com.huskydreaming.medieval.brewery.handlers.interfaces.DependencyHandler;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.block.Block;
import org.bukkit.plugin.PluginManager;

public class DependencyHandlerImpl implements DependencyHandler {

    private boolean worldGuard;

    @Override
    public void initialize(MedievalBreweryPlugin plugin) {
        PluginManager pluginManager = plugin.getServer().getPluginManager();
        if (pluginManager.getPlugin("WorldGuard") != null) {
            worldGuard = true;
        }
    }

    @Override
    public boolean isBlockInsideRegion(Block block) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        Location location = BukkitAdapter.adapt(block.getLocation());
        RegionQuery query = container.createQuery();
        ApplicableRegionSet applicableRegionSet = query.getApplicableRegions(location);
        return !applicableRegionSet.getRegions().isEmpty();
    }

    @Override
    public boolean isWorldGuard() {
        return worldGuard;
    }
}