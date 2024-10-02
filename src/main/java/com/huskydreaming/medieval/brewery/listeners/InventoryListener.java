package com.huskydreaming.medieval.brewery.listeners;

import com.huskydreaming.medieval.brewery.MedievalBreweryPlugin;
import com.huskydreaming.medieval.brewery.data.Brewery;
import com.huskydreaming.medieval.brewery.data.Recipe;
import com.huskydreaming.medieval.brewery.enumerations.BreweryStatus;
import com.huskydreaming.medieval.brewery.handlers.interfaces.BreweryHandler;
import com.huskydreaming.medieval.brewery.handlers.interfaces.RecipeHandler;
import com.huskydreaming.medieval.brewery.utils.TextUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public class InventoryListener implements Listener {

    private final BreweryHandler breweryHandler;
    private final RecipeHandler recipeHandler;

    public InventoryListener(MedievalBreweryPlugin plugin) {
        this.breweryHandler = plugin.getBreweryHandler();
        this.recipeHandler = plugin.getRecipeHandler();
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        if(inventory.getType() != InventoryType.BARREL) return;

        Location location = event.getInventory().getLocation();
        if(location == null) return;

        Block block = location.getBlock();
        if(!breweryHandler.isBrewery(block)) return;

        Player player = (Player) event.getPlayer();
        Recipe recipe = recipeHandler.getRecipe(inventory);
        if(recipe == null) {
            player.sendMessage(TextUtils.prefix("You must provide a valid recipe..."));
            return;
        }

        String recipeName = recipeHandler.getName(recipe);
        Brewery brewery = breweryHandler.getBrewery(block);
        brewery.setRecipeName(recipeName);
        brewery.setRemaining(recipe.getUses());
        brewery.setStatus(BreweryStatus.BREWING);

        long systemTime = System.currentTimeMillis() + (recipe.getSeconds() * 1000L);
        brewery.setTimeStamp(systemTime);
    }
}