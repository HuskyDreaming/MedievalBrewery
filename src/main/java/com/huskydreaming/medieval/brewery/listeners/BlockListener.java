package com.huskydreaming.medieval.brewery.listeners;

import com.huskydreaming.huskycore.HuskyPlugin;
import com.huskydreaming.medieval.brewery.data.Brewery;
import com.huskydreaming.medieval.brewery.data.Hologram;
import com.huskydreaming.medieval.brewery.handlers.interfaces.ConfigHandler;
import com.huskydreaming.medieval.brewery.handlers.interfaces.DependencyHandler;
import com.huskydreaming.medieval.brewery.repositories.interfaces.BreweryRepository;
import com.huskydreaming.medieval.brewery.enumerations.Message;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.TripwireHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.Set;

public class BlockListener implements Listener {

    private final BreweryRepository breweryRepository;
    private final ConfigHandler configHandler;
    private final DependencyHandler dependencyHandler;

    public BlockListener(HuskyPlugin plugin) {
        this.breweryRepository = plugin.provide(BreweryRepository.class);
        this.configHandler = plugin.provide(ConfigHandler.class);
        this.dependencyHandler = plugin.provide(DependencyHandler.class);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Block blockAgainst = event.getBlockAgainst();
        Block blockPlaced = event.getBlockPlaced();

        if (blockPlaced.getType() == Material.TRIPWIRE_HOOK && blockAgainst.getType() == Material.BARREL) {
            Player player = event.getPlayer();
            if (breweryRepository.isBrewery(blockAgainst)) {
                player.sendMessage(Message.GENERAL_BARREL.prefix());
                event.setCancelled(true);
                return;
            }

            if (dependencyHandler.isWorldGuard() && dependencyHandler.isBlockInsideRegion(player, blockPlaced)) {
                player.sendMessage(Message.GENERAL_WORLD_GUARD_PROTECTED.prefix());
                return;
            }

            Set<Brewery> breweries = breweryRepository.getBreweries(player);
            int limit = configHandler.getLimit();
            if (breweries.size() >= limit) {
                player.sendMessage(Message.GENERAL_LIMIT.prefix(limit));
                event.setCancelled(true);
                return;
            }
            breweryRepository.addBrewery(player, blockAgainst);
            player.sendMessage(Message.GENERAL_CREATE.prefix());
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        if (block.getType() == Material.BARREL && breweryRepository.isBrewery(block)) {
            event.setCancelled(true);
        } else if (block.getType() == Material.TRIPWIRE_HOOK) {
            TripwireHook tripwireHook = (TripwireHook) block.getState().getBlockData();
            BlockFace blockFace = tripwireHook.getFacing().getOppositeFace();
            Block relativeBlock = block.getRelative(blockFace);

            if (relativeBlock.getType() == Material.BARREL && breweryRepository.isBrewery(relativeBlock)) {
                Player player = event.getPlayer();
                Brewery brewery = breweryRepository.getBrewery(relativeBlock);

                if (!brewery.getOwner().equals(player.getUniqueId())) {
                    player.sendMessage(Message.GENERAL_OWNER.prefix());
                    event.setCancelled(true);
                    return;
                }

                Hologram hologram = brewery.getHologram();
                if (hologram != null) hologram.delete();

                breweryRepository.removeBrewery(relativeBlock);
                player.sendMessage(Message.GENERAL_REMOVE.prefix());
            }
        }
    }

    @EventHandler
    public void onFromTo(BlockFromToEvent event) {
        Block block = event.getToBlock();

        if (block.getType() != Material.TRIPWIRE_HOOK) return;

        TripwireHook tripwireHook = (TripwireHook) block.getState().getBlockData();
        BlockFace blockFace = tripwireHook.getFacing().getOppositeFace();

        Block relativeBlock = block.getRelative(blockFace);
        if (relativeBlock.getType() == Material.BARREL && breweryRepository.isBrewery(relativeBlock)) {
            Brewery brewery = breweryRepository.getBrewery(relativeBlock);

            Hologram hologram = brewery.getHologram();
            if (hologram != null) hologram.delete();

            breweryRepository.removeBrewery(relativeBlock);

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(brewery.getOwner());
            if (offlinePlayer.hasPlayedBefore() && offlinePlayer.isOnline()) {
                Player player = offlinePlayer.getPlayer();
                if (player != null) player.sendMessage(Message.GENERAL_REMOVE_BLOCK.prefix());
            }
        }
    }

    @EventHandler
    public void onBlockExplode(EntityExplodeEvent event) {
        for (Block block : event.blockList()) {
            if (block.getType() != Material.BARREL && block.getType() != Material.TRIPWIRE_HOOK) continue;

            Block breweryBlock = null;
            if (block.getType() == Material.TRIPWIRE_HOOK) {
                TripwireHook tripwireHook = (TripwireHook) block.getState().getBlockData();
                BlockFace blockFace = tripwireHook.getFacing().getOppositeFace();
                breweryBlock = block.getRelative(blockFace);
            } else if (block.getType() == Material.BARREL) {
                breweryBlock = block;
            }

            if (breweryBlock != null && breweryRepository.isBrewery(breweryBlock)) {
                Brewery brewery = breweryRepository.getBrewery(breweryBlock);

                Hologram hologram = brewery.getHologram();
                if (hologram != null) hologram.delete();

                breweryRepository.removeBrewery(breweryBlock);

                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(brewery.getOwner());
                if (offlinePlayer.hasPlayedBefore() && offlinePlayer.isOnline()) {
                    Player player = offlinePlayer.getPlayer();
                    if (player != null) player.sendMessage(Message.GENERAL_REMOVE_BLOCK.prefix());
                }
            }
        }
    }
}