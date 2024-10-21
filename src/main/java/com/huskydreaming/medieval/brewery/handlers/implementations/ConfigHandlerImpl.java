package com.huskydreaming.medieval.brewery.handlers.implementations;

import com.huskydreaming.medieval.brewery.MedievalBreweryPlugin;
import com.huskydreaming.medieval.brewery.handlers.interfaces.ConfigHandler;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigHandlerImpl implements ConfigHandler {

    private int limit;
    private boolean qualities;
    private boolean notifyPlayer;

    @Override
    public void initialize(MedievalBreweryPlugin plugin) {
        FileConfiguration configuration = plugin.getConfig();

        limit = configuration.getInt("brewery-limit");
        qualities = configuration.getBoolean("qualities");
        notifyPlayer = configuration.getBoolean("notify-player");
    }

    @Override
    public int getLimit() {
        return limit;
    }

    @Override
    public boolean hasQualities() {
        return qualities;
    }

    @Override
    public boolean hasNotifyPlayer() {
        return notifyPlayer;
    }
}