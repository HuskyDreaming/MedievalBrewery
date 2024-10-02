package com.huskydreaming.medieval.brewery.handlers.implementations;


import com.google.common.reflect.TypeToken;
import com.huskydreaming.medieval.brewery.MedievalBreweryPlugin;
import com.huskydreaming.medieval.brewery.data.Ingredient;
import com.huskydreaming.medieval.brewery.data.Recipe;
import com.huskydreaming.medieval.brewery.handlers.interfaces.RecipeHandler;
import com.huskydreaming.medieval.brewery.utils.Json;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RecipeHandlerImpl implements RecipeHandler {

    private Map<String, Recipe> recipes = new ConcurrentHashMap<>();

    @Override
    public void initialize(MedievalBreweryPlugin plugin) {
        Type type = new TypeToken<Map<String, Recipe>>(){}.getType();
        recipes = Json.read(plugin, "recipes", type);
        if(recipes == null) {
            recipes = new ConcurrentHashMap<>();
            Recipe wineRecipe = new Recipe();
            wineRecipe.setColor(ChatColor.RED);
            wineRecipe.setSeconds(86400);
            wineRecipe.setUses(6);
            wineRecipe.setData(1);
            wineRecipe.addEffect(PotionEffectType.SLOW_DIGGING);
            wineRecipe.addEffect(PotionEffectType.CONFUSION);
            wineRecipe.addIngredient(new Ingredient(Material.SWEET_BERRIES, 12));
            wineRecipe.addIngredient(new Ingredient(Material.WHEAT, 4));
            wineRecipe.addIngredient(new Ingredient(Material.SUGAR, 5));
            recipes.put("Wine", wineRecipe);

            Recipe beerRecipe = new Recipe();
            beerRecipe.setColor(ChatColor.YELLOW);
            beerRecipe.setSeconds(86400);
            beerRecipe.setData(2);
            beerRecipe.setUses(8);
            beerRecipe.addEffect(PotionEffectType.SATURATION);
            beerRecipe.addEffect(PotionEffectType.CONFUSION);
            beerRecipe.addIngredient(new Ingredient(Material.WHEAT_SEEDS, 4));
            beerRecipe.addIngredient(new Ingredient(Material.BONE_MEAL, 8));
            recipes.put("Mead", beerRecipe);
        }
    }

    @Override
    public void finalize(MedievalBreweryPlugin plugin) {
        Json.write(plugin, "recipes", recipes);
    }

    @Override
    public String getName(Recipe recipe) {
        return recipes.entrySet().stream()
                .filter(e -> e.getValue() == recipe)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    @Override
    public Recipe getRecipe(String recipeName) {
        return recipes.get(recipeName);
    }

    @Override
    public Recipe getRecipe(Inventory inventory) {
        Map<Ingredient, Boolean> validIngredients = new HashMap<>();
        for (Recipe recipe : recipes.values()) {
            validIngredients.clear();

            for(Ingredient ingredient : recipe.getIngredients()) {
                validIngredients.put(ingredient, false);
            }

            for(ItemStack itemStack : inventory.getContents()) {
                if(itemStack == null) continue;
                Ingredient ingredient = new Ingredient(itemStack);

                if(validIngredients.containsKey(ingredient)) {
                    validIngredients.put(ingredient, true);
                }
            }

            if(!validIngredients.containsValue(false)) {
                return recipe;
            }
        }
        return null;
    }

    @Override
    public ItemStack getRecipeItem(String recipeName) {
        Recipe recipe = recipes.get(recipeName);
        ItemStack itemStack = new ItemStack(Material.POTION);
        PotionMeta potionMeta = (PotionMeta) itemStack.getItemMeta();
        if(potionMeta != null) {
            potionMeta.setDisplayName(recipeName);
            potionMeta.setColor(Color.RED);
            potionMeta.setCustomModelData(recipe.getData());
            for (String potionEffectTypeString : recipe.getEffects()) {
                PotionEffectType potionEffectType = PotionEffectType.getByName(potionEffectTypeString);
                if(potionEffectType == null) continue;
                potionMeta.addCustomEffect(new PotionEffect(potionEffectType, 600, 1), false);
            }
            itemStack.setItemMeta(potionMeta);
        }
        return itemStack;
    }

    @Override
    public Map<String, Recipe> getRecipes() {
        return Collections.unmodifiableMap(recipes);
    }
}