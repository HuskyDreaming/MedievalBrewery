package com.huskydreaming.medieval.brewery.handlers.interfaces;

import com.huskydreaming.medieval.brewery.handlers.Handler;
import com.huskydreaming.medieval.brewery.storage.Yaml;
import org.bukkit.plugin.Plugin;

public interface LocalizationHandler extends Handler {

    void reload(Plugin plugin);

    Yaml getYaml();

    String getLanguage();
}
