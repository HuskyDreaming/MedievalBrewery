package com.huskydreaming.medieval.brewery.listeners;

import com.huskydreaming.medieval.brewery.MedievalBreweryPlugin;
import com.huskydreaming.medieval.brewery.data.Brewery;
import com.huskydreaming.medieval.brewery.data.Hologram;
import com.huskydreaming.medieval.brewery.data.Recipe;
import com.huskydreaming.medieval.brewery.enumerations.BreweryStatus;
import com.huskydreaming.medieval.brewery.handlers.interfaces.DependencyHandler;
import com.huskydreaming.medieval.brewery.repositories.interfaces.BreweryRepository;
import com.huskydreaming.medieval.brewery.repositories.interfaces.RecipeRepository;
import com.huskydreaming.medieval.brewery.storage.Message;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.TripwireHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Set;

public class BlockListener implements Listener {

    private final Plugin plugin;

    private final BreweryRepository breweryRepository;
    private final RecipeRepository recipeRepository;

    private final DependencyHandler dependencyHandler;

    public BlockListener(MedievalBreweryPlugin plugin) {
        this.plugin = plugin;
        this.breweryRepository = plugin.getBreweryRepository();
        this.recipeRepository = plugin.getRecipeRepository();
        this.dependencyHandler = plugin.getDependencyHandler();
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Block blockAgainst = event.getBlockAgainst();
        Block blockPlaced = event.getBlockPlaced();

        if (blockPlaced.getType() == Material.TRIPWIRE_HOOK && blockAgainst.getType() == Material.BARREL) {
            Player player = event.getPlayer();
            if(breweryRepository.isBrewery(blockAgainst)) {
                player.sendMessage(Message.GENERAL_BARREL.prefix());
                event.setCancelled(true);
                return;
            }

            if(dependencyHandler.isWorldGuard() && dependencyHandler.isBlockInsideRegion(blockPlaced)) {
                player.sendMessage(Message.GENERAL_WORLD_GUARD_PROTECTED.prefix());
                return;
            }

            Set<Brewery> breweries = breweryRepository.getBreweries(player);
            int limit = plugin.getConfig().getInt("brewery-limit");
            if(breweries.size() >= limit) {
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

        if(block.getType() == Material.BARREL && breweryRepository.isBrewery(block)) {
            event.setCancelled(true);
        } else if (block.getType() == Material.TRIPWIRE_HOOK) {
            TripwireHook tripwireHook = (TripwireHook) block.getState().getBlockData();
            BlockFace blockFace = tripwireHook.getFacing().getOppositeFace();
            Block relativeBlock = block.getRelative(blockFace);

            if(relativeBlock.getType() == Material.BARREL && breweryRepository.isBrewery(relativeBlock)) {
                Player player = event.getPlayer();
                Brewery brewery = breweryRepository.getBrewery(relativeBlock);

                if(!brewery.getOwner().equals(player.getUniqueId())) {
                    player.sendMessage(Message.GENERAL_OWNER.prefix());
                    event.setCancelled(true);
                    return;
                }

                Hologram hologram = brewery.getHologram();
                if(hologram != null) hologram.delete();

                breweryRepository.removeBrewery(relativeBlock);
                player.sendMessage(Message.GENERAL_REMOVE.prefix());
            }
        }
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block block = event.getClickedBlock();
        if (block == null) return;

        if(block.getType() == Material.BARREL) {
            if(!breweryRepository.isBrewery(block)) return;

            Player player = event.getPlayer();
            Brewery brewery = breweryRepository.getBrewery(block);

            if(brewery.getStatus() == BreweryStatus.BREWING) {
                player.sendMessage(Message.GENERAL_IN_PROGRESS.prefix());
                event.setCancelled(true);
                return;
            } else if(brewery.getStatus() == BreweryStatus.READY) {
                event.setCancelled(true);
            }
        }

        if(block.getType() == Material.TRIPWIRE_HOOK) {

            ItemStack itemStack = event.getItem();
            if(itemStack == null || itemStack.getType() != Material.GLASS_BOTTLE) return;

            TripwireHook tripwireHook = (TripwireHook) block.getState().getBlockData();
            BlockFace blockFace = tripwireHook.getFacing().getOppositeFace();
            Block relativeBlock = block.getRelative(blockFace);

            if(!breweryRepository.isBrewery(relativeBlock)) return;

            event.setCancelled(true);

            Brewery brewery = breweryRepository.getBrewery(relativeBlock);
            if(brewery.getStatus() == BreweryStatus.READY) {
                Hologram hologram = brewery.getHologram();
                String recipeName = brewery.getRecipeName();
                int remaining = brewery.getRemaining();

                if(remaining <= 1) {
                    hologram.update(Message.TITLE_IDLE_HEADER.parse(), Message.TITLE_IDLE_FOOTER.parse());
                    brewery.setStatus(BreweryStatus.IDLE);
                } else {
                    Recipe recipe = recipeRepository.getRecipe(recipeName);
                    int uses = recipe.getUses();
                    brewery.setRemaining(remaining -=1);
                    String header = Message.TITLE_READY_HEADER.parameterize(recipe.getItemColor(), recipeName);
                    String footer = Message.TITLE_READY_FOOTER.parameterize(remaining, uses);
                    hologram.update(header, footer);
                }

                itemStack.setAmount(itemStack.getAmount() -1);
                ItemStack recipeItem = recipeRepository.getRecipeItem(recipeName);
                Player player = event.getPlayer();
                player.getInventory().addItem(recipeItem);

                Location location = block.getLocation();
                World world = location.getWorld();
                if(world != null) {
                    world.playSound(block.getLocation(), Sound.ENTITY_WANDERING_TRADER_DRINK_MILK, 1, 1);
                }
            }
        }
    }
}