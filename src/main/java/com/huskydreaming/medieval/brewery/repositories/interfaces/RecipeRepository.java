package com.huskydreaming.medieval.brewery.repositories.interfaces;

import com.huskydreaming.huskycore.HuskyPlugin;
import com.huskydreaming.huskycore.repositories.Repository;
import com.huskydreaming.medieval.brewery.data.Brewery;
import com.huskydreaming.medieval.brewery.data.Recipe;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public interface RecipeRepository extends Repository {

    boolean load(HuskyPlugin plugin);

    boolean setup(HuskyPlugin plugin);

    String getName(Recipe recipe);

    Recipe getRecipe(String recipeName);

    Recipe getRecipe(Inventory inventory);

    ItemStack getRecipeItem(Brewery brewery);

    Collection<Recipe> getRecipes();
}
