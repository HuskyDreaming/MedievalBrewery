package com.huskydreaming.medieval.brewery.handlers.implementations;

import com.huskydreaming.medieval.brewery.MedievalBreweryPlugin;
import com.huskydreaming.medieval.brewery.enumerations.BreweryStatus;
import com.huskydreaming.medieval.brewery.repositories.interfaces.BreweryRepository;
import com.huskydreaming.medieval.brewery.repositories.interfaces.RecipeRepository;
import com.huskydreaming.medieval.brewery.data.Brewery;
import com.huskydreaming.medieval.brewery.data.Hologram;
import com.huskydreaming.medieval.brewery.data.Recipe;
import com.huskydreaming.medieval.brewery.handlers.interfaces.BreweryHandler;
import com.huskydreaming.medieval.brewery.utils.TimeUtil;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

public class BreweryHandlerImpl implements BreweryHandler {

    private final NamespacedKey namespacedKey;
    private final BreweryRepository breweryRepository;
    private final RecipeRepository recipeRepository;


    public BreweryHandlerImpl(MedievalBreweryPlugin plugin) {
        this.namespacedKey = plugin.getNamespacedKey();
        this.breweryRepository = plugin.getBreweryRepository();
        this.recipeRepository = plugin.getRecipeRepository();
    }

    @Override
    public void initialize(MedievalBreweryPlugin plugin) {
        for(Brewery brewery : breweryRepository.getBreweries()) {
            Block block = brewery.getPosition().toBlock();
            if (block == null) continue;

            Hologram hologram = new Hologram(namespacedKey, block);
            brewery.setHologram(hologram);

            String recipeName = brewery.getRecipeName();
            Recipe recipe = recipeRepository.getRecipe(recipeName);
            if (recipe == null) continue;

            switch (brewery.getStatus()) {
                case READY -> {
                    int uses = recipe.getUses();
                    int remaining = brewery.getRemaining();
                    if (uses < remaining) {
                        hologram.update(recipe.getColor() + recipeName, "#dbd8adReady to Collect! " + remaining + "/" + uses);
                    } else {
                        hologram.update(recipe.getColor() + recipeName, "#dbd8adReady to collect!");
                    }
                }
                case IDLE -> hologram.update("#8db1b5Brewery", "#dbd8adOpen barrel to begin!");
            }

            brewery.setHologram(hologram);
        }
    }

    @Override
    public void finalize(MedievalBreweryPlugin plugin) {
        for(Brewery brewery : breweryRepository.getBreweries()) {
            Hologram hologram = brewery.getHologram();
            if(hologram != null) hologram.delete();
        }
    }

    @Override
    public void run(MedievalBreweryPlugin plugin) {
        new BukkitRunnable() {
            @Override
            public void run() {
                for(Brewery brewery : breweryRepository.getBreweries()) {
                    if(brewery.getStatus() != BreweryStatus.BREWING) continue;
                    if(brewery.getTimeStamp() <= 0) continue;

                    Hologram hologram = brewery.getHologram();
                    if(hologram == null) continue;

                    String recipeName = brewery.getRecipeName();
                    if(recipeName == null) continue;

                    Recipe recipe = recipeRepository.getRecipe(recipeName);
                    if(recipe == null) continue;

                    long systemTime = System.currentTimeMillis();
                    long breweryTime = brewery.getTimeStamp();
                    long timeDifference = breweryTime - systemTime;

                    String timeString = TimeUtil.convertTimeStamp(timeDifference);
                    if(timeDifference <= 0) {
                        hologram.update(recipe.getColor() + recipeName, "#dbd8adReady to collect!");
                        brewery.setStatus(BreweryStatus.READY);
                        brewery.setTimeStamp(0L);

                        Block block = brewery.getPosition().toBlock();
                        if(block instanceof Container container) {
                            Inventory inventory = container.getInventory();
                            inventory.clear();
                        }
                    } else {
                        hologram.update(recipe.getColor() + recipeName, "#dbd8adFinishes in " + timeString);
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }
}
