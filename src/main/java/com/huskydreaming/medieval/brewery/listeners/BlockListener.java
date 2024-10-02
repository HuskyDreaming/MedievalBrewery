package com.huskydreaming.medieval.brewery.listeners;

import com.huskydreaming.medieval.brewery.MedievalBreweryPlugin;
import com.huskydreaming.medieval.brewery.data.Brewery;
import com.huskydreaming.medieval.brewery.data.Hologram;
import com.huskydreaming.medieval.brewery.data.Recipe;
import com.huskydreaming.medieval.brewery.enumerations.BreweryStatus;
import com.huskydreaming.medieval.brewery.repositories.interfaces.BreweryRepository;
import com.huskydreaming.medieval.brewery.repositories.interfaces.RecipeRepository;
import com.huskydreaming.medieval.brewery.utils.TextUtils;
import org.bukkit.Material;
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

public class BlockListener implements Listener {

    private final BreweryRepository breweryRepository;
    private final RecipeRepository recipeRepository;

    public BlockListener(MedievalBreweryPlugin plugin) {
        this.breweryRepository = plugin.getBreweryRepository();
        this.recipeRepository = plugin.getRecipeRepository();
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Block blockAgainst = event.getBlockAgainst();
        Block blockPlaced = event.getBlockPlaced();

        if (blockPlaced.getType() == Material.TRIPWIRE_HOOK && blockAgainst.getType() == Material.BARREL) {
            Player player = event.getPlayer();
            if(breweryRepository.isBrewery(blockAgainst)) {
                player.sendMessage(TextUtils.prefix("The barrel is already a brewery."));
                event.setCancelled(true);
                return;
            }

            if(breweryRepository.isOwner(player)) {
                player.sendMessage(TextUtils.prefix("You can only have one brewery at a time."));
                event.setCancelled(true);
                return;
            }
            breweryRepository.addBrewery(player, blockAgainst);
            player.sendMessage(TextUtils.prefix("You have created a brewery."));
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
                Brewery brewery = breweryRepository.getBrewery(relativeBlock);
                Hologram hologram = brewery.getHologram();
                hologram.delete();

                breweryRepository.removeBrewery(relativeBlock);
                event.getPlayer().sendMessage(TextUtils.prefix("You have removed the brewery."));
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
                player.sendMessage(TextUtils.prefix("There is already a brew in progress."));
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
                int remaining = brewery.getRemaining();

                if(remaining <= 1) {
                    hologram.update("#8db1b5Brewery", "#dbd8adOpen barrel to begin!");
                    brewery.setStatus(BreweryStatus.IDLE);
                } else {
                    String recipeName = brewery.getRecipeName();
                    Recipe recipe = recipeRepository.getRecipe(recipeName);
                    int uses = recipe.getUses();
                    remaining -= 1;
                    brewery.setRemaining(remaining);
                    itemStack.setAmount(itemStack.getAmount() -1);
                    ItemStack recipeItem = recipeRepository.getRecipeItem(brewery.getRecipeName());
                    Player player = event.getPlayer();
                    player.getInventory().addItem(recipeItem);
                    hologram.update(recipe.getColor() + recipeName, "#dbd8adReady to Collect! " + remaining + "/" + uses);
                }
            }
        }
    }
}