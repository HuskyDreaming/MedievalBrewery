package com.huskydreaming.medieval.brewery.repositories.implementations;

import com.google.common.reflect.TypeToken;
import com.huskydreaming.medieval.brewery.MedievalBreweryPlugin;
import com.huskydreaming.medieval.brewery.data.Quality;
import com.huskydreaming.medieval.brewery.repositories.interfaces.QualityRepository;
import com.huskydreaming.medieval.brewery.storage.Json;
import com.huskydreaming.medieval.brewery.utils.RandomCollection;

import java.lang.reflect.Type;
import java.util.*;

public class QualityRepositoryImpl implements QualityRepository {

    private Map<String, Quality> qualities = new HashMap<>();
    private final RandomCollection<String> probabilities = new RandomCollection<>();

    @Override
    public void deserialize(MedievalBreweryPlugin plugin) {
        Type type = new TypeToken<Map<String, Quality>>() {}.getType();
        qualities = Json.read(plugin, "data/qualities", type);
        if (qualities == null) {
            qualities = new HashMap<>();

            qualities.put("Low", getLowQuality());
            qualities.put("Average", getAverageQuality());
            qualities.put("Excellent", getExcellentQuality());

            Json.write(plugin, "data/qualities", qualities);
            plugin.getLogger().info("Successfully setup default qualities.");
        } else {
            plugin.getLogger().info("Successfully loaded " + qualities.size() + " qualities");
        }

        qualities.forEach((s, quality) -> probabilities.add(quality.getProbability(), s));
    }

    @Override
    public Quality getQuality(String name) {
        return qualities.get(name);
    }

    @Override
    public String getQuality() {
        return probabilities.next();
    }

    private Quality getLowQuality() {
        Quality lowQuality = new Quality();
        lowQuality.setMultiplier(1);
        lowQuality.setProbability(0.75f);
        return lowQuality;
    }

    private Quality getAverageQuality() {
        Quality averageQuality = new Quality();
        averageQuality.setMultiplier(2);
        averageQuality.setProbability(0.5f);
        return averageQuality;
    }

    private Quality getExcellentQuality() {
        Quality excellentQuality = new Quality();
        excellentQuality.setMultiplier(3);
        excellentQuality.setProbability(0.25f);
        return excellentQuality;
    }
}