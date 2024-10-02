package com.huskydreaming.medieval.brewery;

import com.huskydreaming.medieval.brewery.handlers.implementations.BreweryHandlerImpl;
import com.huskydreaming.medieval.brewery.handlers.implementations.RecipeHandlerImpl;
import com.huskydreaming.medieval.brewery.handlers.interfaces.BreweryHandler;
import com.huskydreaming.medieval.brewery.handlers.interfaces.RecipeHandler;
import com.huskydreaming.medieval.brewery.listeners.BlockListener;
import com.huskydreaming.medieval.brewery.listeners.EntityListener;
import com.huskydreaming.medieval.brewery.listeners.InventoryListener;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class MedievalBreweryPlugin extends JavaPlugin {

    private NamespacedKey namespacedKey;
    private RecipeHandler recipeHandler;
    private BreweryHandler breweryHandler;

    @Override
    public void onEnable() {
        namespacedKey = new NamespacedKey(this, "MedievalBrewery");

        recipeHandler = new RecipeHandlerImpl();
        recipeHandler.initialize(this);

        breweryHandler = new BreweryHandlerImpl(this);
        breweryHandler.initialize(this);

        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new BlockListener(this), this);
        pluginManager.registerEvents(new EntityListener(this), this);
        pluginManager.registerEvents(new InventoryListener(this), this);
    }

    @Override
    public void onDisable() {
        recipeHandler.finalize(this);
        breweryHandler.finalize(this);
    }

    public NamespacedKey getNamespacedKey() {
        return namespacedKey;
    }

    public RecipeHandler getRecipeHandler() {
        return recipeHandler;
    }

    public BreweryHandler getBreweryHandler() {
        return breweryHandler;
    }
}