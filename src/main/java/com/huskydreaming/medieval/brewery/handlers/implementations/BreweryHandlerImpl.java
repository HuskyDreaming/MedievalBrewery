package com.huskydreaming.medieval.brewery.handlers.implementations;

import com.huskydreaming.huskycore.HuskyPlugin;
import com.huskydreaming.huskycore.utilities.Util;
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
import com.huskydreaming.medieval.brewery.enumerations.Message;
import com.huskydreaming.medieval.brewery.utils.TimeUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class BreweryHandlerImpl implements BreweryHandler {

    private BreweryRepository breweryRepository;
    private QualityRepository qualityRepository;
    private RecipeRepository recipeRepository;

    private ConfigHandler configHandler;

    @Override
    public void postInitialize(HuskyPlugin plugin) {
        breweryRepository = plugin.provide(BreweryRepository.class);
        qualityRepository = plugin.provide(QualityRepository.class);
        recipeRepository = plugin.provide(RecipeRepository.class);
        configHandler = plugin.provide(ConfigHandler.class);

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

                        header = Util.hex(recipe.getItem().getDisplayName());
                        footer = Message.TITLE_READY_FOOTER.parameterize(remaining, uses);
                    }
                }
                case WATER -> {
                    String recipeName = brewery.getRecipeName();
                    Recipe recipe = recipeRepository.getRecipe(recipeName);

                    if(recipe != null) {
                        int waterLevel = brewery.getWaterLevel();
                        int water = recipe.getWater();

                        header = Util.hex(recipe.getItem().getDisplayName());
                        footer = Message.TITLE_WATER_FOOTER.parameterize(waterLevel, water);
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

        run(plugin);
    }

    @Override
    public void finalize(HuskyPlugin plugin) {
        for(Brewery brewery : breweryRepository.getBreweries()) {
            Hologram hologram = brewery.getHologram();
            if(hologram != null) hologram.delete();
        }
    }

    @Override
    public void run(HuskyPlugin plugin) {
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

                        header = Util.hex(recipe.getItem().getDisplayName());
                        footer = Message.TITLE_READY_FOOTER.parameterize(remaining, uses);

                        brewery.setStatus(BreweryStatus.READY);
                        brewery.setTimeStamp(0L);

                        if(configHandler.hasQualities()) {
                            String quality = qualityRepository.getQuality();
                            if (quality != null) brewery.setQualityName(quality);
                        }

                        if(configHandler.hasNotifyPlayer()) {
                            Player player = Bukkit.getPlayer(brewery.getOwner());
                            if(player != null) player.sendMessage(Message.GENERAL_NOTIFY.prefix(recipeName));
                        }
                    } else {
                        String timeString = TimeUtil.convertTimeStamp(timeDifference);

                        header = Util.hex(recipe.getItem().getDisplayName());
                        footer = Message.TITLE_TIME_FOOTER.parameterize(timeString);
                    }

                    hologram.update(header, footer);
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    @Override
    public void retrieveBottle(Player player, ItemStack itemStack, Block block) {
        Brewery brewery = breweryRepository.getBrewery(block);
        if (brewery.getStatus() == BreweryStatus.READY) {
            Hologram hologram = brewery.getHologram();
            String recipeName = brewery.getRecipeName();
            int remaining = brewery.getRemaining();

            if (remaining <= 1) {
                hologram.update(Message.TITLE_IDLE_HEADER.parse(), Message.TITLE_IDLE_FOOTER.parse());
                brewery.setStatus(BreweryStatus.IDLE);
            } else {
                Recipe recipe = recipeRepository.getRecipe(recipeName);
                int uses = recipe.getUses();
                brewery.setRemaining(remaining -= 1);
                String header = Util.hex(recipe.getItem().getDisplayName());
                String footer = Message.TITLE_READY_FOOTER.parameterize(remaining, uses);
                hologram.update(header, footer);
            }

            itemStack.setAmount(itemStack.getAmount() - 1);
            ItemStack recipeItem = recipeRepository.getRecipeItem(brewery);
            player.getInventory().addItem(recipeItem);

            Location location = block.getLocation();
            World world = location.getWorld();
            if (world != null) {
                world.playSound(block.getLocation(), Sound.ENTITY_WANDERING_TRADER_DRINK_MILK, 1, 1);
            }
        }
    }

    @Override
    public void updateWater(Brewery brewery, ItemStack itemStack) {
        if(itemStack == null || itemStack.getType() != Material.WATER_BUCKET) return;
        String recipeName = brewery.getRecipeName();
        Recipe recipe = recipeRepository.getRecipe(recipeName);

        itemStack.setType(Material.BUCKET);

        brewery.addWaterLevel();
        int water = recipe.getWater();
        int waterLevel = brewery.getWaterLevel();
        if(waterLevel >= water) {
            brewery.setRemaining(recipe.getUses());
            brewery.setStatus(BreweryStatus.BREWING);
            brewery.setWaterLevel(0);

            long systemTime = System.currentTimeMillis() + (recipe.getSeconds() * 1000L);
            brewery.setTimeStamp(systemTime);
        } else {
            Hologram hologram = brewery.getHologram();
            String header = Util.hex(recipe.getItem().getDisplayName());
            String footer = Message.TITLE_WATER_FOOTER.parameterize(waterLevel, water);
            hologram.update(header, footer);
        }
    }
}