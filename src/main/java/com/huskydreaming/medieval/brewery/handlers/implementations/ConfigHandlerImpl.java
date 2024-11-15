package com.huskydreaming.medieval.brewery.handlers.implementations;

import com.huskydreaming.huskycore.HuskyPlugin;
import com.huskydreaming.medieval.brewery.handlers.interfaces.ConfigHandler;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigHandlerImpl implements ConfigHandler {

    private int limit;
    private boolean qualities;
    private boolean notifyPlayer;
    private boolean receiveBook;
    private String language;

    @Override
    public void initialize(HuskyPlugin plugin) {
        plugin.saveDefaultConfig();
        FileConfiguration configuration = plugin.getConfig();

        limit = configuration.getInt("brewery-limit");
        qualities = configuration.getBoolean("qualities");
        notifyPlayer = configuration.getBoolean("notify-player");
        receiveBook = configuration.getBoolean("receive-book");
        language = configuration.getString("language");
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

    @Override
    public boolean isReceiveBook() {
        return receiveBook;
    }

    @Override
    public String getLanguage() {
        return language;
    }
}