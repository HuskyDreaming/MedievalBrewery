package com.huskydreaming.medieval.brewery.repositories.implementations;

import com.google.common.reflect.TypeToken;
import com.huskydreaming.medieval.brewery.MedievalBreweryPlugin;
import com.huskydreaming.medieval.brewery.data.*;
import com.huskydreaming.medieval.brewery.handlers.interfaces.ConfigHandler;
import com.huskydreaming.medieval.brewery.repositories.interfaces.QualityRepository;
import com.huskydreaming.medieval.brewery.repositories.interfaces.RecipeRepository;
import com.huskydreaming.medieval.brewery.storage.Json;
import com.huskydreaming.medieval.brewery.storage.Message;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RecipeRepositoryImpl implements RecipeRepository {

    private Map<String, Recipe> recipes = new ConcurrentHashMap<>();

    private QualityRepository qualityRepository;
    private ConfigHandler configHandler;

    @Override
    public void deserialize(MedievalBreweryPlugin plugin) {
        qualityRepository = plugin.getQualityRepository();
        configHandler = plugin.getConfigHandler();

        Type type = new TypeToken<Map<String, Recipe>>() {}.getType();
        recipes = Json.read(plugin, "data/recipes", type);
        if (recipes == null) {
            recipes = new ConcurrentHashMap<>();
            recipes.put("Beer", getBeerRecipe());
            recipes.put("Wine", getWineRecipe());
            recipes.put("Spirits", getSpiritsRecipe());

            Json.write(plugin, "data/recipes", recipes);
            plugin.getLogger().info("Successfully setup default recipes.");
        } else {
            plugin.getLogger().info("Successfully loaded " + recipes.size()  + " recipe(s)");
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
    public ItemStack getRecipeItem(Brewery brewery) {
        String recipeName = brewery.getRecipeName();
        Recipe recipe = recipes.get(recipeName);

        if(recipe.getMaterial() == null) return null;
        ItemStack itemStack = new ItemStack(recipe.getMaterial());

        Quality quality = null;
        String qualityName = brewery.getQualityName();
        boolean qualities = configHandler.hasQualities();
        if(qualities) {
            quality = qualityRepository.getQuality(qualityName);
        }

        if(recipe.getMaterial() == Material.POTION) {
            PotionMeta potionMeta = (PotionMeta) itemStack.getItemMeta();
            if(potionMeta != null && recipe.getColor() != null) {

                potionMeta.setColor(recipe.getColor());
                potionMeta.setDisplayName(Message.ITEM_NAME.parameterize(recipe.getItemColor(), recipeName));
                potionMeta.setCustomModelData(recipe.getData());
                potionMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);

                String data;
                List<String> lore;

                if(qualities && quality != null) {
                    lore = Message.ITEM_LORE_QUALITY.parameterizeList(qualityName, recipe.getDescription());
                    data = recipeName + ":" + quality.getMultiplier();
                } else {
                    lore = Message.ITEM_LORE_DEFAULT.parameterizeList(recipe.getDescription());
                    data = recipeName;
                }

                potionMeta.setLore(lore);

                NamespacedKey namespacedKey = MedievalBreweryPlugin.getNamespacedKey();
                PersistentDataContainer persistentDataContainer = potionMeta.getPersistentDataContainer();
                persistentDataContainer.set(namespacedKey, PersistentDataType.STRING, data);

                itemStack.setItemMeta(potionMeta);
            }
        } else {
            ItemMeta itemMeta = itemStack.getItemMeta();
            if(itemMeta != null) {

                itemMeta.setDisplayName(Message.ITEM_NAME.prefix(recipe.getItemColor(), recipeName));
                itemMeta.setCustomModelData(recipe.getData());
                itemMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);

                String data;
                List<String> lore;

                if(qualities && quality != null) {
                    lore = Message.ITEM_LORE_QUALITY.parameterizeList(qualityName, recipe.getDescription());
                    data = recipeName + ":" + quality.getMultiplier();
                } else {
                    lore = Message.ITEM_LORE_DEFAULT.parameterizeList(recipe.getDescription());
                    data = recipeName;
                }

                itemMeta.setLore(lore);

                NamespacedKey namespacedKey = MedievalBreweryPlugin.getNamespacedKey();
                PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
                persistentDataContainer.set(namespacedKey, PersistentDataType.STRING, data);

                itemStack.setItemMeta(itemMeta);
            }
        }
        return itemStack;
    }

    private Recipe getBeerRecipe() {
        Recipe beerRecipe = new Recipe();
        beerRecipe.setDescription("Perfect thirst quencher");

        Effect nauseaEffect = new Effect();
        nauseaEffect.setType(PotionEffectType.NAUSEA);
        nauseaEffect.setDuration(200);
        nauseaEffect.setAmplifier(0);

        Effect luckEffect = new Effect();
        luckEffect.setType(PotionEffectType.LUCK);
        luckEffect.setDuration(600);
        luckEffect.setAmplifier(1);

        beerRecipe.setMaterial(Material.POTION);
        beerRecipe.setItemColor(ChatColor.YELLOW);
        beerRecipe.setColor(Color.YELLOW);
        beerRecipe.setSeconds(100);
        beerRecipe.setUses(24);

        beerRecipe.addEffect(nauseaEffect);
        beerRecipe.addEffect(luckEffect);

        beerRecipe.addIngredient(new Ingredient(Material.WATER_BUCKET, 1));
        beerRecipe.addIngredient(new Ingredient(Material.WHEAT, 16));
        beerRecipe.addIngredient(new Ingredient(Material.WHEAT_SEEDS, 4));
        return beerRecipe;
    }

    private Recipe getWineRecipe() {
        Recipe wineRecipe = new Recipe();
        wineRecipe.setDescription("Wine and dine!");

        Effect nauseaEffect = new Effect();
        nauseaEffect.setType(PotionEffectType.NAUSEA);
        nauseaEffect.setDuration(200);
        nauseaEffect.setAmplifier(0);

        Effect regenerationEffect = new Effect();
        regenerationEffect.setType(PotionEffectType.REGENERATION);
        regenerationEffect.setDuration(400);
        regenerationEffect.setAmplifier(1);

        wineRecipe.setMaterial(Material.POTION);
        wineRecipe.setItemColor(ChatColor.RED);
        wineRecipe.setColor(Color.MAROON);
        wineRecipe.setSeconds(120);
        wineRecipe.setUses(16);

        wineRecipe.addEffect(nauseaEffect);
        wineRecipe.addEffect(regenerationEffect);
        wineRecipe.addIngredient(new Ingredient(Material.WATER_BUCKET, 1));
        wineRecipe.addIngredient(new Ingredient(Material.SWEET_BERRIES, 12));
        wineRecipe.addIngredient(new Ingredient(Material.SUGAR, 4));
        return wineRecipe;
    }

    private Recipe getSpiritsRecipe() {
        Recipe spiritsRecipe = new Recipe();
        spiritsRecipe.setDescription("The good stuff");

        Effect nauseaEffect = new Effect();
        nauseaEffect.setType(PotionEffectType.NAUSEA);
        nauseaEffect.setDuration(200);
        nauseaEffect.setAmplifier(0);

        Effect regenerationEffect = new Effect();
        regenerationEffect.setType(PotionEffectType.REGENERATION);
        regenerationEffect.setDuration(600);
        regenerationEffect.setAmplifier(1);

        Effect absorptionEffect = new Effect();
        absorptionEffect.setType(PotionEffectType.ABSORPTION);
        absorptionEffect.setDuration(400);
        absorptionEffect.setAmplifier(1);

        Effect hasteEffect = new Effect();
        hasteEffect.setType(PotionEffectType.HASTE);
        hasteEffect.setDuration(400);
        hasteEffect.setAmplifier(1);

        spiritsRecipe.setMaterial(Material.POTION);
        spiritsRecipe.setItemColor(ChatColor.AQUA);
        spiritsRecipe.setColor(Color.AQUA);
        spiritsRecipe.setSeconds(240);
        spiritsRecipe.setUses(2);

        spiritsRecipe.addEffect(nauseaEffect);
        spiritsRecipe.addEffect(regenerationEffect);
        spiritsRecipe.addEffect(absorptionEffect);
        spiritsRecipe.addEffect(hasteEffect);

        spiritsRecipe.addIngredient(new Ingredient(Material.WATER_BUCKET, 1));
        spiritsRecipe.addIngredient(new Ingredient(Material.SUGAR, 16));
        spiritsRecipe.addIngredient(new Ingredient(Material.QUARTZ, 8));
        spiritsRecipe.addIngredient(new Ingredient(Material.GHAST_TEAR, 4));
        return spiritsRecipe;
    }
}