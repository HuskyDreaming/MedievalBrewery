package com.huskydreaming.medieval.brewery.listeners;

import com.huskydreaming.medieval.brewery.MedievalBreweryPlugin;
import com.huskydreaming.medieval.brewery.data.Recipe;
import com.huskydreaming.medieval.brewery.repositories.interfaces.RecipeRepository;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerListener implements Listener {

    private final RecipeRepository recipeRepository;

    public PlayerListener(MedievalBreweryPlugin plugin) {
        this.recipeRepository = plugin.getRecipeRepository();
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        ItemStack itemStack = event.getItem();
        if(itemStack.getType() != Material.HONEY_BOTTLE) return;

        ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta == null) return;

        NamespacedKey namespacedKey = MedievalBreweryPlugin.getNamespacedKey();
        PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
        if(!dataContainer.has(namespacedKey)) return;

        String recipeName = dataContainer.get(namespacedKey, PersistentDataType.STRING);
        Recipe recipe = recipeRepository.getRecipe(recipeName);
        if(recipe == null) return;

        Player player = event.getPlayer();
        for(String effect : recipe.getEffects()) {
            PotionEffectType potionEffectType = PotionEffectType.getByName(effect);
            if(potionEffectType == null) continue;

            player.addPotionEffect(new PotionEffect(potionEffectType, 600, 0));
        }
    }
}
