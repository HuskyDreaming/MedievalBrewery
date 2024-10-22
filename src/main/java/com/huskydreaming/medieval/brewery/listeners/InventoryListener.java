package com.huskydreaming.medieval.brewery.listeners;

import com.huskydreaming.medieval.brewery.MedievalBreweryPlugin;
import com.huskydreaming.medieval.brewery.data.Brewery;
import com.huskydreaming.medieval.brewery.data.Hologram;
import com.huskydreaming.medieval.brewery.data.Recipe;
import com.huskydreaming.medieval.brewery.enumerations.BreweryStatus;
import com.huskydreaming.medieval.brewery.repositories.interfaces.BreweryRepository;
import com.huskydreaming.medieval.brewery.repositories.interfaces.RecipeRepository;
import com.huskydreaming.medieval.brewery.storage.Message;
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

    private final BreweryRepository breweryRepository;
    private final RecipeRepository recipeRepository;

    public InventoryListener(MedievalBreweryPlugin plugin) {
        this.breweryRepository = plugin.getBreweryRepository();
        this.recipeRepository = plugin.getRecipeRepository();
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        if(inventory.getType() != InventoryType.BARREL) return;

        Location location = event.getInventory().getLocation();
        if(location == null) return;

        Block block = location.getBlock();
        if(!breweryRepository.isBrewery(block)) return;

        Player player = (Player) event.getPlayer();
        Recipe recipe = recipeRepository.getRecipe(inventory);
        if(recipe == null) {
            player.sendMessage(Message.GENERAL_RECIPE.prefix());
            return;
        }

        boolean hasPermission;
        String permission = recipe.getPermission();
        String recipeName = recipeRepository.getName(recipe);
        if(permission == null) {
            hasPermission = true;
        } else {
            hasPermission = player.hasPermission(permission);
        }

        if(hasPermission) {
            Brewery brewery = breweryRepository.getBrewery(block);
            brewery.setRecipeName(recipeName);
            brewery.setStatus(BreweryStatus.WATER);

            Hologram hologram = brewery.getHologram();
            String header = TextUtils.hex(recipe.getItem().getDisplayName());
            String footer = Message.TITLE_WATER_FOOTER.parameterize(brewery.getWaterLevel(), recipe.getWater());
            hologram.update(header, footer);

            inventory.clear();
        } else {
            player.sendMessage(Message.GENERAL_PERMISSION_BREW.prefix(recipeName));
        }
    }
}