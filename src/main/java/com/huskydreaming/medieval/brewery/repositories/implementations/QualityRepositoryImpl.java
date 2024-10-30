package com.huskydreaming.medieval.brewery.repositories.implementations;

import com.huskydreaming.huskycore.HuskyPlugin;
import com.huskydreaming.huskycore.storage.Yaml;
import com.huskydreaming.medieval.brewery.data.Quality;
import com.huskydreaming.medieval.brewery.repositories.interfaces.QualityRepository;
import com.huskydreaming.medieval.brewery.utils.RandomCollection;
import org.apache.logging.log4j.util.Strings;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class QualityRepositoryImpl implements QualityRepository {

    private final Map<String, Quality> qualities = new ConcurrentHashMap<>();
    private Yaml yaml;

    private final RandomCollection<String> probabilities = new RandomCollection<>();

    @Override
    public void postDeserialize(HuskyPlugin plugin) {
        qualities.clear();
        probabilities.clear();

        if (yaml == null) yaml = new Yaml("qualities");
        yaml.load(plugin);

        if (load(plugin)) {
            plugin.getLogger().info("[Storage] Successfully loaded " + qualities.size() + " qualities");
        } else {
            if (setup(plugin)) {
                plugin.getLogger().info("[Storage] Successfully setup default qualities");
            }
        }

        qualities.forEach((s, quality) -> probabilities.add(quality.getProbability(), s));
    }

    @Override
    public boolean load(HuskyPlugin plugin) {
        FileConfiguration configuration = yaml.getConfiguration();
        ConfigurationSection configurationSection = configuration.getConfigurationSection(Strings.EMPTY);
        if (configurationSection == null) return false;

        for (String key : configurationSection.getKeys(false)) {
            Quality quality = new Quality();

            double probability = configuration.getDouble(key + ".probability");
            quality.setProbability(probability);

            int multiplier = configuration.getInt(key + ".multiplier");
            quality.setMultiplier(multiplier);

            String displayName = configuration.getString(key + ".displayName");
            if (displayName != null) quality.setDisplayName(displayName);

            qualities.put(key, quality);
        }
        return !qualities.isEmpty();
    }

    @Override
    public boolean setup(HuskyPlugin plugin) {
        FileConfiguration configuration = yaml.getConfiguration();
        configuration.set("low.displayName", "✦✧✧");
        configuration.set("low.probability", 0.75);
        configuration.set("low.multiplier", 1);

        configuration.set("average.displayName", "✦✦✧");
        configuration.set("average.probability", 0.5);
        configuration.set("average.multiplier", 2);

        configuration.set("excellent.displayName", "✦✦✦");
        configuration.set("excellent.probability", 0.25);
        configuration.set("excellent.multiplier", 3);

        yaml.save();
        return load(plugin);
    }

    @Override
    public Quality getQuality(String name) {
        return qualities.get(name);
    }

    @Override
    public String getQuality() {
        return probabilities.next();
    }
}