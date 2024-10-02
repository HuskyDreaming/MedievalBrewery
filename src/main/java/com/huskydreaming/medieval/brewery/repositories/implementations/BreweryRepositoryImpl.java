package com.huskydreaming.medieval.brewery.repositories.implementations;

import com.google.common.reflect.TypeToken;
import com.huskydreaming.medieval.brewery.MedievalBreweryPlugin;
import com.huskydreaming.medieval.brewery.data.Brewery;
import com.huskydreaming.medieval.brewery.data.Hologram;
import com.huskydreaming.medieval.brewery.data.Position;
import com.huskydreaming.medieval.brewery.repositories.interfaces.BreweryRepository;
import com.huskydreaming.medieval.brewery.utils.Json;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class BreweryRepositoryImpl implements BreweryRepository {

    private NamespacedKey namespacedKey;
    private Set<Brewery> breweries = new HashSet<>();

    @Override
    public void deserialize(MedievalBreweryPlugin plugin) {
        Type type = new TypeToken<Set<Brewery>>() {}.getType();
        namespacedKey = plugin.getNamespacedKey();
        breweries = Json.read(plugin, "breweries", type);
        if (breweries == null) breweries = new HashSet<>();
    }

    @Override
    public void serialize(MedievalBreweryPlugin plugin) {
        Json.write(plugin, "breweries", breweries);
    }

    @Override
    public void addBrewery(Player player, Block block) {
        Brewery brewery = new Brewery();
        Position position = new Position(block);
        Hologram hologram = new Hologram(namespacedKey, block);
        brewery.setHologram(hologram);
        brewery.setPosition(position);
        brewery.setOwner(player);
        breweries.add(brewery);
    }

    @Override
    public void removeBrewery(Block block) {
        Position position = new Position(block);
        breweries.removeIf(b -> b.getPosition().equals(position));
    }

    @Override
    public boolean isBrewery(Block block) {
        Position position = new Position(block);
        return breweries.stream().anyMatch(b -> b.getPosition().equals(position));
    }

    @Override
    public boolean isOwner(Player player) {
        return breweries.stream().anyMatch(b -> b.getOwner().equals(player.getUniqueId()));
    }

    @Override
    public Brewery getBrewery(Block block) {
        Position position = new Position(block);
        return breweries.stream()
                .filter(b -> b.getPosition().equals(position))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Set<Brewery> getBreweries() {
        return Collections.unmodifiableSet(breweries);
    }
}