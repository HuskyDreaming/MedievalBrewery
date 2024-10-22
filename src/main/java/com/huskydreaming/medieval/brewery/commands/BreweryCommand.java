package com.huskydreaming.medieval.brewery.commands;

import com.huskydreaming.medieval.brewery.MedievalBreweryPlugin;
import com.huskydreaming.medieval.brewery.data.Brewery;
import com.huskydreaming.medieval.brewery.data.Hologram;
import com.huskydreaming.medieval.brewery.handlers.interfaces.LocalizationHandler;
import com.huskydreaming.medieval.brewery.repositories.interfaces.BreweryRepository;
import com.huskydreaming.medieval.brewery.repositories.interfaces.QualityRepository;
import com.huskydreaming.medieval.brewery.repositories.interfaces.RecipeRepository;
import com.huskydreaming.medieval.brewery.storage.Message;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BreweryCommand implements CommandExecutor, TabCompleter {

    private final MedievalBreweryPlugin plugin;
    private final LocalizationHandler localizationHandler;
    private final BreweryRepository breweryRepository;
    private final QualityRepository qualityRepository;
    private final RecipeRepository recipeRepository;

    public BreweryCommand(MedievalBreweryPlugin plugin) {
        this.plugin = plugin;
        this.localizationHandler = plugin.getLocalizationHandler();
        this.breweryRepository = plugin.getBreweryRepository();
        this.qualityRepository = plugin.getQualityRepository();
        this.recipeRepository = plugin.getRecipeRepository();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length == 1) {
            String string = strings[0];

            if (string.equalsIgnoreCase("reload")) {
                if(!commandSender.hasPermission("brewery.reload")) {
                    commandSender.sendMessage(Message.GENERAL_PERMISSION.prefix());
                    return true;
                }
                localizationHandler.initialize(plugin);
                qualityRepository.deserialize(plugin);
                recipeRepository.deserialize(plugin);

                plugin.reloadConfig();
                commandSender.sendMessage(Message.GENERAL_RELOAD.prefix());
                return true;
            }

            if (commandSender instanceof Player player) {
                if (string.equalsIgnoreCase("remove")) {
                    if(!commandSender.hasPermission("brewery.remove")) {
                        commandSender.sendMessage(Message.GENERAL_PERMISSION.prefix());
                        return true;
                    }

                    Block block = player.getTargetBlock(null, 3);
                    if (block.getType() != Material.BARREL) {
                        player.sendMessage(Message.GENERAL_BLOCK.prefix());
                        return true;
                    }

                    Brewery brewery = breweryRepository.getBrewery(block);
                    if (brewery == null) {
                        player.sendMessage(Message.GENERAL_BLOCK.prefix());
                        return true;
                    }

                    Hologram hologram = brewery.getHologram();
                    hologram.delete();

                    breweryRepository.removeBrewery(block);
                    player.sendMessage(Message.GENERAL_REMOVE.prefix());
                }
            } else {
                commandSender.sendMessage("You must be a player to run that command!");
            }
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return List.of("reload", "remove");
    }
}