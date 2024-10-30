package com.huskydreaming.medieval.brewery.handlers.interfaces;

import com.huskydreaming.huskycore.HuskyPlugin;
import com.huskydreaming.huskycore.handlers.interfaces.Handler;
import com.huskydreaming.medieval.brewery.data.Brewery;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface BreweryHandler extends Handler {


    void run(HuskyPlugin plugin);

    void retrieveBottle(Player player, ItemStack itemStack, Block block);

    void updateWater(Brewery brewery, ItemStack itemStack);
}
