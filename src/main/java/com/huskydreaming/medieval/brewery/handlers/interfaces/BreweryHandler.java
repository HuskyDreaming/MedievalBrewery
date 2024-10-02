package com.huskydreaming.medieval.brewery.handlers.interfaces;

import com.huskydreaming.medieval.brewery.data.Brewery;
import com.huskydreaming.medieval.brewery.handlers.Handler;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public interface BreweryHandler extends Handler {

    void addBrewery(Player player, Block block);

    void removeBrewery(Block block);

    boolean isBrewery(Block block);

    boolean isOwner(Player player);

    Brewery getBrewery(Block block);
}
