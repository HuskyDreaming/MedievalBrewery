package com.huskydreaming.medieval.brewery.repositories.interfaces;

import com.huskydreaming.medieval.brewery.data.Brewery;
import com.huskydreaming.medieval.brewery.repositories.Repository;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Set;

public interface BreweryRepository extends Repository {

    void addBrewery(Player player, Block block);

    void removeBrewery(Block block);

    boolean isBrewery(Block block);

    boolean isOwner(Player player);

    Brewery getBrewery(Block block);

    Set<Brewery> getBreweries();
}
