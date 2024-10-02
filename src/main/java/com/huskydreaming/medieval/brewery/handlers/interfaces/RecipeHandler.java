package com.huskydreaming.medieval.brewery.handlers.interfaces;

import com.huskydreaming.medieval.brewery.data.Recipe;
import com.huskydreaming.medieval.brewery.handlers.Handler;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public interface RecipeHandler extends Handler {

    String getName(Recipe recipe);

    Recipe getRecipe(String recipeName);

    Recipe getRecipe(Inventory inventory);

    ItemStack getRecipeItem(String recipeName);

    Map<String, Recipe> getRecipes();
}
