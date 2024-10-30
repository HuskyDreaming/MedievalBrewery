package com.huskydreaming.medieval.brewery.commands;

import com.huskydreaming.huskycore.HuskyPlugin;
import com.huskydreaming.huskycore.commands.annotations.CommandAnnotation;
import com.huskydreaming.huskycore.commands.providers.CommandProvider;
import com.huskydreaming.medieval.brewery.enumerations.Message;
import com.huskydreaming.medieval.brewery.handlers.interfaces.LocalizationHandler;
import com.huskydreaming.medieval.brewery.repositories.interfaces.QualityRepository;
import com.huskydreaming.medieval.brewery.repositories.interfaces.RecipeRepository;
import org.bukkit.command.CommandSender;

@CommandAnnotation(label = "reload")
public class ReloadCommand implements CommandProvider {

    private final HuskyPlugin plugin;

    private final LocalizationHandler localizationHandler;
    private final QualityRepository qualityRepository;
    private final RecipeRepository recipeRepository;

    public ReloadCommand(HuskyPlugin plugin) {
        this.plugin = plugin;

        this.localizationHandler = plugin.provide(LocalizationHandler.class);
        this.qualityRepository = plugin.provide(QualityRepository.class);
        this.recipeRepository = plugin.provide(RecipeRepository.class);
    }

    @Override
    public void onCommand(CommandSender commandSender, String[] strings) {
        if (!commandSender.hasPermission("brewery.reload")) {
            commandSender.sendMessage(Message.GENERAL_PERMISSION.prefix());
            return;
        }

        localizationHandler.initialize(plugin);
        qualityRepository.deserialize(plugin);
        recipeRepository.deserialize(plugin);

        plugin.reloadConfig();
        commandSender.sendMessage(Message.GENERAL_RELOAD.prefix());
    }
}
