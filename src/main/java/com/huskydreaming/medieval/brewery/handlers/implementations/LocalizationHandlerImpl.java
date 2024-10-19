package com.huskydreaming.medieval.brewery.handlers.implementations;

import com.huskydreaming.medieval.brewery.MedievalBreweryPlugin;
import com.huskydreaming.medieval.brewery.handlers.interfaces.ConfigHandler;
import com.huskydreaming.medieval.brewery.handlers.interfaces.LocalizationHandler;
import com.huskydreaming.medieval.brewery.storage.Message;
import com.huskydreaming.medieval.brewery.storage.Yaml;
import org.bukkit.plugin.Plugin;

public class LocalizationHandlerImpl implements LocalizationHandler {

    private ConfigHandler configHandler;

    @Override
    public void initialize(MedievalBreweryPlugin plugin) {
        configHandler = plugin.getConfigHandler();
        reload(plugin);
    }

    @Override
    public void reload(Plugin plugin) {
        String language = configHandler.getLanguage();
        Yaml yaml = new Yaml("localization/" + language);

        yaml.load(plugin);
        yaml.save();

        Message.load(yaml);
    }
}