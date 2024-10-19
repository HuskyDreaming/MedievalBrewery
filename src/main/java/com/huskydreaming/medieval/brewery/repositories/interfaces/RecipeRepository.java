package com.huskydreaming.medieval.brewery.repositories.interfaces;

import com.huskydreaming.medieval.brewery.MedievalBreweryPlugin;
import com.huskydreaming.medieval.brewery.data.Brewery;
import com.huskydreaming.medieval.brewery.data.Recipe;
import com.huskydreaming.medieval.brewery.repositories.Repository;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public interface RecipeRepository extends Repository {

    boolean load(MedievalBreweryPlugin plugin);

    boolean setup(MedievalBreweryPlugin plugin);

    String getName(Recipe recipe);

    Recipe getRecipe(String recipeName);

    Recipe getRecipe(Inventory inventory);

    ItemStack getRecipeItem(Brewery brewery);
}
