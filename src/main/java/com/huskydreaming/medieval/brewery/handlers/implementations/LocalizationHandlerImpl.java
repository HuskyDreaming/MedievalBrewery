package com.huskydreaming.medieval.brewery.handlers.implementations;

import com.huskydreaming.huskycore.HuskyPlugin;
import com.huskydreaming.huskycore.storage.Yaml;
import com.huskydreaming.medieval.brewery.handlers.interfaces.ConfigHandler;
import com.huskydreaming.medieval.brewery.handlers.interfaces.LocalizationHandler;
import com.huskydreaming.medieval.brewery.enumerations.Message;

public class LocalizationHandlerImpl implements LocalizationHandler {

    @Override
    public void initialize(HuskyPlugin plugin) {
        ConfigHandler configHandler = plugin.provide(ConfigHandler.class);

        String language = configHandler.getLanguage();
        Yaml yaml = new Yaml("localization/" + language);

        yaml.load(plugin);
        yaml.save();

        Message.load(yaml);
    }
}