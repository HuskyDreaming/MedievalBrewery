package com.huskydreaming.medieval.brewery.handlers.implementations;

import com.huskydreaming.medieval.brewery.MedievalBreweryPlugin;
import com.huskydreaming.medieval.brewery.handlers.interfaces.ConfigHandler;
import com.huskydreaming.medieval.brewery.handlers.interfaces.LocalizationHandler;
import com.huskydreaming.medieval.brewery.storage.Message;
import com.huskydreaming.medieval.brewery.storage.Yaml;

public class LocalizationHandlerImpl implements LocalizationHandler {

    @Override
    public void initialize(MedievalBreweryPlugin plugin) {
        ConfigHandler configHandler = plugin.getConfigHandler();

        String language = configHandler.getLanguage();
        Yaml yaml = new Yaml("localization/" + language);

        yaml.load(plugin);
        yaml.save();

        Message.load(yaml);
    }
}