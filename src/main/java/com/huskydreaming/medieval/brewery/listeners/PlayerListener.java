package com.huskydreaming.medieval.brewery.listeners;

import com.huskydreaming.medieval.brewery.MedievalBreweryPlugin;
import com.huskydreaming.medieval.brewery.data.Brewery;
import com.huskydreaming.medieval.brewery.data.Effect;
import com.huskydreaming.medieval.brewery.data.Recipe;
import com.huskydreaming.medieval.brewery.enumerations.BreweryStatus;
import com.huskydreaming.medieval.brewery.handlers.interfaces.BreweryHandler;
import com.huskydreaming.medieval.brewery.handlers.interfaces.ConfigHandler;
import com.huskydreaming.medieval.brewery.repositories.interfaces.BreweryRepository;
import com.huskydreaming.medieval.brewery.repositories.interfaces.RecipeRepository;
import com.huskydreaming.medieval.brewery.enumerations.Message;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.TripwireHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;

public class PlayerListener implements Listener {

    private final BreweryRepository breweryRepository;
    private final RecipeRepository recipeRepository;
    private final BreweryHandler breweryHandler;
    private final ConfigHandler configHandler;

    public PlayerListener(MedievalBreweryPlugin plugin) {
        this.breweryRepository = plugin.provide(BreweryRepository.class);
        this.recipeRepository = plugin.provide(RecipeRepository.class);
        this.breweryHandler = plugin.provide(BreweryHandler.class);
        this.configHandler = plugin.provide(ConfigHandler.class);
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        ItemStack itemStack = event.getItem();

        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return;

        NamespacedKey namespacedKey = MedievalBreweryPlugin.getNamespacedKey();
        PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
        if (!dataContainer.has(namespacedKey, PersistentDataType.STRING)) return;

        String data = dataContainer.get(namespacedKey, PersistentDataType.STRING);
        if (data == null) return;

        String recipeName = data;
        int multiplier = 1;

        if (configHandler.hasQualities()) {
            String[] splitData = data.split(":");
            recipeName = splitData[0];
            multiplier = Integer.parseInt(splitData[1]);
        }

        Recipe recipe = recipeRepository.getRecipe(recipeName);
        if (recipe == null) return;

        Player player = event.getPlayer();
        for (Effect effect : recipe.getEffects()) {
            PotionEffect potionEffect = effect.toPotionEffect(multiplier);
            if (potionEffect == null) continue;
            player.addPotionEffect(potionEffect);
        }
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block block = event.getClickedBlock();
        if (block == null) return;

        if (block.getType() == Material.BARREL) {
            if (!breweryRepository.isBrewery(block)) return;

            Player player = event.getPlayer();
            Brewery brewery = breweryRepository.getBrewery(block);

            if (brewery.getStatus() == BreweryStatus.BREWING) {
                player.sendMessage(Message.GENERAL_IN_PROGRESS.prefix());
                event.setCancelled(true);
                return;
            } else if (brewery.getStatus() == BreweryStatus.READY) {
                event.setCancelled(true);
            } else if (brewery.getStatus() == BreweryStatus.WATER) {
                breweryHandler.updateWater(brewery, event.getItem());
                event.setCancelled(true);
            }
        }

        if (block.getType() == Material.TRIPWIRE_HOOK) {

            ItemStack itemStack = event.getItem();
            if (itemStack == null || itemStack.getType() != Material.GLASS_BOTTLE) return;

            TripwireHook tripwireHook = (TripwireHook) block.getState().getBlockData();
            BlockFace blockFace = tripwireHook.getFacing().getOppositeFace();
            Block relativeBlock = block.getRelative(blockFace);

            if (!breweryRepository.isBrewery(relativeBlock)) return;
            breweryHandler.retrieveBottle(event.getPlayer(), itemStack, relativeBlock);

            event.setCancelled(true);
        }
    }
}