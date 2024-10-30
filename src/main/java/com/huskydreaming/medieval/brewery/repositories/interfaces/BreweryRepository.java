package com.huskydreaming.medieval.brewery.repositories.interfaces;

import com.huskydreaming.huskycore.repositories.Repository;
import com.huskydreaming.medieval.brewery.data.Brewery;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

public interface BreweryRepository extends Repository {

    void addBrewery(Player player, Block block);

    void removeBrewery(Block block);

    boolean isBrewery(Block block);

    Brewery getBrewery(Block block);

    Set<Brewery> getBreweries(Player player);

    List<Brewery> getBreweries();
}
