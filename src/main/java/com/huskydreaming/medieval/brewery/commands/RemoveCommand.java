package com.huskydreaming.medieval.brewery.commands;

import com.huskydreaming.huskycore.HuskyPlugin;
import com.huskydreaming.huskycore.commands.annotations.CommandAnnotation;
import com.huskydreaming.huskycore.commands.providers.PlayerCommandProvider;
import com.huskydreaming.medieval.brewery.data.Brewery;
import com.huskydreaming.medieval.brewery.data.Hologram;
import com.huskydreaming.medieval.brewery.enumerations.Message;
import com.huskydreaming.medieval.brewery.repositories.interfaces.BreweryRepository;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

@CommandAnnotation(label = "remove")
public class RemoveCommand implements PlayerCommandProvider {

    private final BreweryRepository breweryRepository;

    public RemoveCommand(HuskyPlugin plugin) {
        this.breweryRepository = plugin.provide(BreweryRepository.class);
    }

    @Override
    public void onCommand(Player player, String[] strings) {
        Block block = player.getTargetBlock(null, 3);
        if (block.getType() != Material.BARREL) {
            player.sendMessage(Message.GENERAL_BLOCK.prefix());
            return;
        }

        Brewery brewery = breweryRepository.getBrewery(block);
        if (brewery == null) {
            player.sendMessage(Message.GENERAL_BLOCK.prefix());
            return;
        }

        Hologram hologram = brewery.getHologram();
        hologram.delete();

        breweryRepository.removeBrewery(block);
        player.sendMessage(Message.GENERAL_REMOVE.prefix());
    }
}