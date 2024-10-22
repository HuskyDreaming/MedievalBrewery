package com.huskydreaming.medieval.brewery.handlers.interfaces;

import com.huskydreaming.medieval.brewery.MedievalBreweryPlugin;
import com.huskydreaming.medieval.brewery.data.Brewery;
import com.huskydreaming.medieval.brewery.handlers.Handler;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface BreweryHandler extends Handler {

    void run(MedievalBreweryPlugin plugin);

    void retrieveBottle(Player player, ItemStack itemStack, Block block);

    void updateWater(Brewery brewery, ItemStack itemStack);
}
