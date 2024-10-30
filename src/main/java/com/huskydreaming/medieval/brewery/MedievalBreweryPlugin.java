package com.huskydreaming.medieval.brewery;

import com.huskydreaming.huskycore.HuskyPlugin;
import com.huskydreaming.huskycore.handlers.interfaces.CommandHandler;
import com.huskydreaming.medieval.brewery.commands.BreweryCommand;
import com.huskydreaming.medieval.brewery.commands.RecipeCommand;
import com.huskydreaming.medieval.brewery.commands.ReloadCommand;
import com.huskydreaming.medieval.brewery.commands.RemoveCommand;
import com.huskydreaming.medieval.brewery.handlers.implementations.BreweryHandlerImpl;
import com.huskydreaming.medieval.brewery.handlers.implementations.ConfigHandlerImpl;
import com.huskydreaming.medieval.brewery.handlers.implementations.DependencyHandlerImpl;
import com.huskydreaming.medieval.brewery.handlers.implementations.LocalizationHandlerImpl;
import com.huskydreaming.medieval.brewery.handlers.interfaces.BreweryHandler;
import com.huskydreaming.medieval.brewery.handlers.interfaces.ConfigHandler;
import com.huskydreaming.medieval.brewery.handlers.interfaces.DependencyHandler;
import com.huskydreaming.medieval.brewery.handlers.interfaces.LocalizationHandler;
import com.huskydreaming.medieval.brewery.listeners.BlockListener;
import com.huskydreaming.medieval.brewery.listeners.EntityListener;
import com.huskydreaming.medieval.brewery.listeners.InventoryListener;
import com.huskydreaming.medieval.brewery.listeners.PlayerListener;
import com.huskydreaming.medieval.brewery.repositories.implementations.BreweryRepositoryImpl;
import com.huskydreaming.medieval.brewery.repositories.implementations.QualityRepositoryImpl;
import com.huskydreaming.medieval.brewery.repositories.implementations.RecipeRepositoryImpl;
import com.huskydreaming.medieval.brewery.repositories.interfaces.BreweryRepository;
import com.huskydreaming.medieval.brewery.repositories.interfaces.QualityRepository;
import com.huskydreaming.medieval.brewery.repositories.interfaces.RecipeRepository;
import org.bukkit.NamespacedKey;

public class MedievalBreweryPlugin extends HuskyPlugin {

    private static NamespacedKey namespacedKey;

    @Override
    public void onInitialize() {
        namespacedKey = new NamespacedKey(this, "MedievalBrewery");

        handlerRegistry.register(ConfigHandler.class, new ConfigHandlerImpl());
        handlerRegistry.register(LocalizationHandler.class, new LocalizationHandlerImpl());
        handlerRegistry.register(BreweryHandler.class, new BreweryHandlerImpl());
        handlerRegistry.register(DependencyHandler.class, new DependencyHandlerImpl());
        repositoryRegistry.register(BreweryRepository.class, new BreweryRepositoryImpl());
        repositoryRegistry.register(RecipeRepository.class, new RecipeRepositoryImpl());
    }

    @Override
    public void onPostInitialize() {
        ConfigHandler configHandler = handlerRegistry.provide(ConfigHandler.class);
        if(configHandler.hasQualities()) {
            repositoryRegistry.register(QualityRepository.class, new QualityRepositoryImpl());
        }

        CommandHandler commandHandler = handlerRegistry.provide(CommandHandler.class);
        commandHandler.setCommandExecutor(new BreweryCommand(this));
        commandHandler.add(new RecipeCommand(this));
        commandHandler.add(new ReloadCommand(this));
        commandHandler.add(new RemoveCommand(this));

        registerListeners(
                new BlockListener(this),
                new EntityListener(),
                new InventoryListener(this),
                new PlayerListener(this)
        );
    }

    public static NamespacedKey getNamespacedKey() {
        return namespacedKey;
    }
}