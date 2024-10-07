package com.huskydreaming.medieval.brewery.repositories.implementations;

import com.google.common.reflect.TypeToken;
import com.huskydreaming.medieval.brewery.MedievalBreweryPlugin;
import com.huskydreaming.medieval.brewery.data.Brewery;
import com.huskydreaming.medieval.brewery.data.Hologram;
import com.huskydreaming.medieval.brewery.data.Position;
import com.huskydreaming.medieval.brewery.repositories.interfaces.BreweryRepository;
import com.huskydreaming.medieval.brewery.storage.Json;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class BreweryRepositoryImpl implements BreweryRepository {

    private Set<Brewery> breweries = new HashSet<>();

    @Override
    public void deserialize(MedievalBreweryPlugin plugin) {
        Type type = new TypeToken<Set<Brewery>>() {}.getType();
        breweries = Json.read(plugin, "data/breweries", type);
        if (breweries == null) {
            breweries = new HashSet<>();
        } else {
            plugin.getLogger().info("Successfully loaded " + breweries.size() + " breweries");
        }
    }

    @Override
    public void serialize(MedievalBreweryPlugin plugin) {
        Json.write(plugin, "data/breweries", breweries);
        breweries.clear();
        plugin.getLogger().info("Successfully saved " + breweries.size() + " breweries");
    }

    @Override
    public void addBrewery(Player player, Block block) {
        Brewery brewery = new Brewery();
        NamespacedKey namespacedKey = MedievalBreweryPlugin.getNamespacedKey();
        Hologram hologram = Hologram.create(namespacedKey, block);
        brewery.setHologram(hologram);
        brewery.setPosition(Position.of(block));
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
    public Set<Brewery> getBreweries() {
        return Collections.unmodifiableSet(breweries);
    }
}