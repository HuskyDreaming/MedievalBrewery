package com.huskydreaming.medieval.brewery.handlers.implementations;

import com.google.common.reflect.TypeToken;
import com.huskydreaming.medieval.brewery.MedievalBreweryPlugin;
import com.huskydreaming.medieval.brewery.utils.Json;
import com.huskydreaming.medieval.brewery.utils.TimeUtil;
import com.huskydreaming.medieval.brewery.data.Brewery;
import com.huskydreaming.medieval.brewery.data.Hologram;
import com.huskydreaming.medieval.brewery.data.Position;
import com.huskydreaming.medieval.brewery.data.Recipe;
import com.huskydreaming.medieval.brewery.enumerations.BreweryStatus;
import com.huskydreaming.medieval.brewery.handlers.interfaces.BreweryHandler;
import com.huskydreaming.medieval.brewery.handlers.interfaces.RecipeHandler;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

public class BreweryHandlerImpl implements BreweryHandler {

    private final NamespacedKey namespacedKey;
    private final RecipeHandler recipeHandler;
    private Set<Brewery> breweries = new HashSet<>();

    public BreweryHandlerImpl(MedievalBreweryPlugin plugin) {
        this.recipeHandler = plugin.getRecipeHandler();
        this.namespacedKey = plugin.getNamespacedKey();
    }

    @Override
    public void initialize(MedievalBreweryPlugin plugin) {
        Type type = new TypeToken<Set<Brewery>>(){}.getType();
        breweries = Json.read(plugin, "breweries", type);
        if(breweries == null) {
            breweries = new HashSet<>();
        } else {
            for(Brewery brewery : breweries) {
                Block block = brewery.getPosition().toBlock();
                if(block == null) continue;

                Hologram hologram = new Hologram(namespacedKey, block);
                brewery.setHologram(hologram);

                String recipeName = brewery.getRecipeName();
                Recipe recipe = recipeHandler.getRecipe(recipeName);
                if(recipe == null) continue;

                switch (brewery.getStatus()) {
                    case READY -> {
                        int uses = recipe.getUses();
                        int remaining = brewery.getRemaining();
                        if(uses < remaining) {
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

        new BukkitRunnable() {
            @Override
            public void run() {
                for(Brewery brewery : breweries) {
                    if(brewery.getStatus() != BreweryStatus.BREWING) continue;
                    if(brewery.getTimeStamp() <= 0) continue;

                    Hologram hologram = brewery.getHologram();
                    if(hologram == null) continue;

                    String recipeName = brewery.getRecipeName();
                    if(recipeName == null) continue;

                    Recipe recipe = recipeHandler.getRecipe(recipeName);
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

    @Override
    public void finalize(MedievalBreweryPlugin plugin) {
        for(Brewery brewery : breweries) {
            Hologram hologram = brewery.getHologram();
            if(hologram != null) hologram.delete();
        }
        Json.write(plugin, "breweries", breweries);
    }

    @Override
    public void addBrewery(Player player, Block block) {
        Brewery brewery = new Brewery();
        Position position = new Position(block);
        Hologram hologram = new Hologram(namespacedKey, block);
        brewery.setHologram(hologram);
        brewery.setPosition(position);
        brewery.setOwner(player);
        breweries.add(brewery);
    }

    @Override
    public void removeBrewery(Block block) {
        Position position = new Position(block);
        breweries.removeIf(b -> b.getPosition().equals(position));
    }

    @Override
    public boolean isBrewery(Block block) {
        Position position = new Position(block);
        return breweries.stream().anyMatch(b -> b.getPosition().equals(position));
    }

    @Override
    public boolean isOwner(Player player) {
        return breweries.stream().anyMatch(b -> b.getOwner().equals(player.getUniqueId()));
    }

    @Override
    public Brewery getBrewery(Block block) {
        Position position = new Position(block);
        return breweries.stream()
                .filter(b -> b.getPosition().equals(position))
                .findFirst()
                .orElse(null);
    }
}
