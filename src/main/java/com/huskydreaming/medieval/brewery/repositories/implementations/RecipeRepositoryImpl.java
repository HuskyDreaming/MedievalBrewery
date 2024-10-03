package com.huskydreaming.medieval.brewery.repositories.implementations;

import com.google.common.reflect.TypeToken;
import com.huskydreaming.medieval.brewery.MedievalBreweryPlugin;
import com.huskydreaming.medieval.brewery.data.Ingredient;
import com.huskydreaming.medieval.brewery.data.Recipe;
import com.huskydreaming.medieval.brewery.repositories.interfaces.RecipeRepository;
import com.huskydreaming.medieval.brewery.utils.Json;
import com.huskydreaming.medieval.brewery.utils.TextUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RecipeRepositoryImpl implements RecipeRepository {

    private Map<String, Recipe> recipes = new ConcurrentHashMap<>();

    @Override
    public void deserialize(MedievalBreweryPlugin plugin) {
        Type type = new TypeToken<Map<String, Recipe>>() {}.getType();
        recipes = Json.read(plugin, "recipes", type);
        if (recipes == null) {
            recipes = new ConcurrentHashMap<>();
            Recipe wineRecipe = new Recipe();
            wineRecipe.setMaterial(Material.HONEY_BOTTLE);
            wineRecipe.setItemColor(ChatColor.RED);
            wineRecipe.setSeconds(60);
            wineRecipe.setUses(6);
            wineRecipe.setData(310);
            wineRecipe.addEffect(PotionEffectType.NAUSEA);
            wineRecipe.addEffect(PotionEffectType.REGENERATION);
            wineRecipe.addIngredient(new Ingredient(Material.SWEET_BERRIES, 12));
            wineRecipe.addIngredient(new Ingredient(Material.SUGAR, 4));
            recipes.put("Wine", wineRecipe);

            Recipe aleRecipe = new Recipe();
            aleRecipe.setMaterial(Material.HONEY_BOTTLE);
            aleRecipe.setItemColor(ChatColor.YELLOW);
            aleRecipe.setSeconds(60);
            aleRecipe.setData(312);
            aleRecipe.setUses(8);
            aleRecipe.addEffect(PotionEffectType.NAUSEA);
            aleRecipe.addEffect(PotionEffectType.LUCK);
            aleRecipe.addIngredient(new Ingredient(Material.HONEY_BOTTLE, 1));
            aleRecipe.addIngredient(new Ingredient(Material.WHEAT, 4));
            recipes.put("Ale", aleRecipe);

            Recipe meadRecipe = new Recipe();
            meadRecipe.setMaterial(Material.HONEY_BOTTLE);
            meadRecipe.setItemColor(ChatColor.GREEN);
            meadRecipe.setSeconds(60);
            meadRecipe.setData(311);
            meadRecipe.setUses(8);
            meadRecipe.addEffect(PotionEffectType.NAUSEA);
            meadRecipe.addEffect(PotionEffectType.STRENGTH);
            meadRecipe.addIngredient(new Ingredient(Material.WHEAT_SEEDS, 4));
            meadRecipe.addIngredient(new Ingredient(Material.BONE_MEAL, 8));
            recipes.put("Mead", meadRecipe);

            Json.write(plugin, "recipes", recipes);
            plugin.getLogger().info("Successfully loaded setup default recipes.");
        } else {
            plugin.getLogger().info("Successfully loaded " + recipes.size());
        }
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

            for (Ingredient ingredient : recipe.getIngredients()) {
                validIngredients.put(ingredient, false);
            }

            for (ItemStack itemStack : inventory.getContents()) {
                if (itemStack == null) continue;
                Ingredient ingredient = new Ingredient(itemStack);

                if (validIngredients.containsKey(ingredient)) {
                    validIngredients.put(ingredient, true);
                }
            }

            if (!validIngredients.containsValue(false)) {
                return recipe;
            }
        }
        return null;
    }

    @Override
    public ItemStack getRecipeItem(String recipeName) {
        Recipe recipe = recipes.get(recipeName);

        if(recipe.getMaterial() == null) return null;
        ItemStack itemStack = new ItemStack(recipe.getMaterial());

        ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta == null) return null;

        itemMeta.setDisplayName(TextUtils.hex(recipeName));
        itemMeta.setCustomModelData(recipe.getData());

        NamespacedKey namespacedKey = MedievalBreweryPlugin.getNamespacedKey();
        PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
        persistentDataContainer.set(namespacedKey, PersistentDataType.STRING, recipeName);

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}