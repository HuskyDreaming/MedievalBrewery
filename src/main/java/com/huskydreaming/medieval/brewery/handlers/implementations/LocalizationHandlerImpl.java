package com.huskydreaming.medieval.brewery.handlers.implementations;

import com.huskydreaming.medieval.brewery.MedievalBreweryPlugin;
import com.huskydreaming.medieval.brewery.handlers.interfaces.LocalizationHandler;
import com.huskydreaming.medieval.brewery.storage.Message;
import com.huskydreaming.medieval.brewery.storage.Yaml;
import org.bukkit.plugin.Plugin;

public class LocalizationHandlerImpl implements LocalizationHandler {

    private Yaml yaml;
    private String language;

    @Override
    public void initialize(MedievalBreweryPlugin plugin) {
        reload(plugin);
    }

    @Override
    public void reload(Plugin plugin) {
        language = plugin.getConfig().getString("language");
        yaml = new Yaml("localization/" + language);

        yaml.load(plugin);
        yaml.save();

        Message.load(yaml);
    }

    @Override
    public Yaml getYaml() {
        return yaml;
    }

    @Override
    public String getLanguage() {
        return language;
    }
}
