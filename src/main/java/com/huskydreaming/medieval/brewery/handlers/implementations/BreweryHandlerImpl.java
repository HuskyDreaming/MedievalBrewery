package com.huskydreaming.medieval.brewery.handlers.implementations;

import com.huskydreaming.medieval.brewery.MedievalBreweryPlugin;
import com.huskydreaming.medieval.brewery.enumerations.BreweryStatus;
import com.huskydreaming.medieval.brewery.handlers.interfaces.ConfigHandler;
import com.huskydreaming.medieval.brewery.repositories.interfaces.BreweryRepository;
import com.huskydreaming.medieval.brewery.repositories.interfaces.QualityRepository;
import com.huskydreaming.medieval.brewery.repositories.interfaces.RecipeRepository;
import com.huskydreaming.medieval.brewery.data.Brewery;
import com.huskydreaming.medieval.brewery.data.Hologram;
import com.huskydreaming.medieval.brewery.data.Recipe;
import com.huskydreaming.medieval.brewery.handlers.interfaces.BreweryHandler;
import com.huskydreaming.medieval.brewery.storage.Message;
import com.huskydreaming.medieval.brewery.utils.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class BreweryHandlerImpl implements BreweryHandler {

    private final BreweryRepository breweryRepository;
    private final QualityRepository qualityRepository;
    private final RecipeRepository recipeRepository;

    private final ConfigHandler configHandler;

    public BreweryHandlerImpl(MedievalBreweryPlugin plugin) {
        this.breweryRepository = plugin.getBreweryRepository();
        this.qualityRepository = plugin.getQualityRepository();
        this.recipeRepository = plugin.getRecipeRepository();

        this.configHandler = plugin.getConfigHandler();
    }

    @Override
    public void initialize(MedievalBreweryPlugin plugin) {
        NamespacedKey namespacedKey = MedievalBreweryPlugin.getNamespacedKey();

        for(Brewery brewery : breweryRepository.getBreweries()) {
            Block block = brewery.getPosition().toBlock();
            if (block == null) continue;

            String header = null;
            String footer = null;

            switch (brewery.getStatus()) {
                case READY -> {
                    String recipeName = brewery.getRecipeName();
                    Recipe recipe = recipeRepository.getRecipe(recipeName);

                    if(recipe != null) {
                        int remaining = brewery.getRemaining();
                        int uses = recipe.getUses();

                        header = Message.TITLE_READY_HEADER.parameterize(recipe.getChatColor(), recipeName);
                        footer = Message.TITLE_READY_FOOTER.parameterize(remaining, uses);
                    }
                }
                case IDLE -> {
                    header = Message.TITLE_IDLE_HEADER.parse();
                    footer = Message.TITLE_IDLE_FOOTER.parse();
                }
            }

            Hologram hologram;
            if(header != null && footer != null) {
                hologram = Hologram.create(namespacedKey, block, header, footer);
            } else {
                hologram = Hologram.create(namespacedKey, block);
            }

            brewery.setHologram(hologram);
        }
    }

    @Override
    public void finalize(MedievalBreweryPlugin plugin) {
        for(Brewery brewery : breweryRepository.getBreweries()) {
            Hologram hologram = brewery.getHologram();
            if(hologram != null) hologram.delete();
        }
    }

    @Override
    public void run(MedievalBreweryPlugin plugin) {
        new BukkitRunnable() {
            @Override
            public void run() {
                for(Brewery brewery : breweryRepository.getBreweries()) {
                    if(brewery.getStatus() != BreweryStatus.BREWING) continue;
                    if(brewery.getTimeStamp() <= 0) continue;

                    Hologram hologram = brewery.getHologram();
                    if(hologram == null) continue;

                    String recipeName = brewery.getRecipeName();
                    if(recipeName == null) continue;

                    Recipe recipe = recipeRepository.getRecipe(recipeName);
                    if(recipe == null) continue;

                    long timeDifference = TimeUtil.timeDifference(brewery);

                    String header;
                    String footer;

                    if((timeDifference / 1000L) < 1) {
                        int remaining = brewery.getRemaining();
                        int uses = recipe.getUses();

                        header = Message.TITLE_READY_HEADER.parameterize(recipe.getChatColor(), recipeName);
                        footer = Message.TITLE_READY_FOOTER.parameterize(remaining, uses);

                        brewery.setStatus(BreweryStatus.READY);
                        brewery.setTimeStamp(0L);

                        if(configHandler.hasQualities()) {
                            String quality = qualityRepository.getQuality();
                            if (quality != null) brewery.setQualityName(quality);
                        }

                        if(configHandler.hasNotifyPlayer()) {
                            Player player = Bukkit.getPlayer(brewery.getOwner());
                            if(player != null) player.sendMessage(Message.GENERAL_NOTIFY.prefix(recipe.getChatColor(), recipeName));
                        }
                    } else {
                        String timeString = TimeUtil.convertTimeStamp(timeDifference);

                        header = Message.TITLE_TIME_HEADER.parameterize(recipe.getChatColor(), recipeName);
                        footer = Message.TITLE_TIME_FOOTER.parameterize(timeString);
                    }

                    hologram.update(header, footer);
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }
}