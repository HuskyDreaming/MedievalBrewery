package com.huskydreaming.medieval.brewery.repositories.implementations;

import com.huskydreaming.huskycore.HuskyPlugin;
import com.huskydreaming.huskycore.storage.Yaml;
import com.huskydreaming.medieval.brewery.MedievalBreweryPlugin;
import com.huskydreaming.medieval.brewery.data.Brewery;
import com.huskydreaming.medieval.brewery.data.Hologram;
import com.huskydreaming.medieval.brewery.data.Position;
import com.huskydreaming.medieval.brewery.enumerations.BreweryStatus;
import com.huskydreaming.medieval.brewery.repositories.interfaces.BreweryRepository;
import org.apache.logging.log4j.util.Strings;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class BreweryRepositoryImpl implements BreweryRepository {

    private final List<Brewery> breweries = new ArrayList<>();
    private Yaml yaml;

    @Override
    public void postDeserialize(HuskyPlugin plugin) {
        yaml = new Yaml("breweries");
        yaml.load(plugin);

        FileConfiguration configuration = yaml.getConfiguration();
        ConfigurationSection configurationSection = configuration.getConfigurationSection(Strings.EMPTY);
        if(configurationSection == null) return;

        for(String key : configurationSection.getKeys(false)) {
            Brewery brewery = new Brewery();

            String qualityName = configuration.getString(key + ".qualityName");
            if(qualityName != null) brewery.setQualityName(qualityName);

            String recipeName = configuration.getString(key + ".recipeName");
            if(recipeName != null) brewery.setRecipeName(recipeName);

            String status = configuration.getString(key + ".status");
            if(status != null) brewery.setStatus(BreweryStatus.valueOf(status));

            String owner = configuration.getString(key + ".owner");
            if(owner != null) brewery.setOwner(UUID.fromString(owner));

            int remaining = configuration.getInt(key + ".remaining");
            brewery.setRemaining(remaining);

            long timeStamp = configuration.getLong(key + ".timestamp");
            brewery.setTimeStamp(timeStamp);

            int waterLevel = configuration.getInt(key + ".waterLevel");
            brewery.setWaterLevel(waterLevel);

            int x = configuration.getInt(key + ".position.x");
            int y = configuration.getInt(key + ".position.y");
            int z = configuration.getInt(key + ".position.z");
            String worldUID = configuration.getString(key + ".position.worldUID");
            if(worldUID != null) brewery.setPosition(Position.of(x, y, z, UUID.fromString(worldUID)));

            breweries.add(brewery);
        }

        plugin.getLogger().info("[Storage] Successfully loaded " + breweries.size() + " breweries");
    }

    @Override
    public void serialize(HuskyPlugin plugin) {
        FileConfiguration configuration = yaml.getConfiguration();
        ConfigurationSection configurationSection = configuration.getConfigurationSection(Strings.EMPTY);
        if(configurationSection == null) return;

        for(String key : configurationSection.getKeys(false)) {
            configuration.set(key, null);
        }

        for(int i = 0; i < breweries.size(); i++) {
            Brewery brewery = breweries.get(i);
            if(brewery == null) continue;

            configuration.set(i + ".qualityName", brewery.getQualityName());
            configuration.set(i + ".recipeName", brewery.getRecipeName());
            configuration.set(i + ".status", brewery.getStatus().name());
            configuration.set(i + ".owner", brewery.getOwner().toString());
            configuration.set(i + ".footer", brewery.getOwner().toString());
            configuration.set(i + ".header", brewery.getOwner().toString());
            configuration.set(i + ".remaining", brewery.getRemaining());
            configuration.set(i + ".timestamp", brewery.getTimeStamp());
            configuration.set(i + ".waterLevel", brewery.getWaterLevel());

            Position position = brewery.getPosition();
            configuration.set(i + ".position.x", position.getX());
            configuration.set(i + ".position.y", position.getY());
            configuration.set(i + ".position.z", position.getZ());
            configuration.set(i + ".position.worldUID", position.getWorldUID().toString());
        }
        yaml.save();

        plugin.getLogger().info("[Storage] Successfully saved " + breweries.size() + " breweries");
    }

    @Override
    public void addBrewery(Player player, Block block) {
        Brewery brewery = new Brewery();
        NamespacedKey namespacedKey = MedievalBreweryPlugin.getNamespacedKey();
        Hologram hologram = Hologram.create(namespacedKey, block);
        brewery.setHologram(hologram);
        brewery.setPosition(Position.of(block));
        brewery.setStatus(BreweryStatus.IDLE);
        brewery.setWaterLevel(0);
        brewery.setOwner(player);
        breweries.add(brewery);
    }

    @Override
    public void removeBrewery(Block block) {
        breweries.removeIf(b -> b.getPosition().equals(Position.of(block)));
    }

    @Override
    public boolean isBrewery(Block block) {
        return breweries.stream().anyMatch(b -> b.getPosition().equals(Position.of(block)));
    }

    @Override
    public Brewery getBrewery(Block block) {
        return breweries.stream()
                .filter(b -> b.getPosition().equals(Position.of(block)))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Set<Brewery> getBreweries(Player player) {
        return this.breweries.stream()
                .filter(b -> b.getOwner().equals(player.getUniqueId()))
                .collect(Collectors.toSet());
    }

    @Override
    public List<Brewery> getBreweries() {
        return Collections.unmodifiableList(breweries);
    }
}