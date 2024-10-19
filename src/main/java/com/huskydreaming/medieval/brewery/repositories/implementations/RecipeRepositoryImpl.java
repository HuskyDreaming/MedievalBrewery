package com.huskydreaming.medieval.brewery.repositories.implementations;

import com.huskydreaming.medieval.brewery.MedievalBreweryPlugin;
import com.huskydreaming.medieval.brewery.data.*;
import com.huskydreaming.medieval.brewery.handlers.interfaces.ConfigHandler;
import com.huskydreaming.medieval.brewery.repositories.interfaces.QualityRepository;
import com.huskydreaming.medieval.brewery.repositories.interfaces.RecipeRepository;
import com.huskydreaming.medieval.brewery.storage.Message;
import com.huskydreaming.medieval.brewery.storage.Yaml;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeRepositoryImpl implements RecipeRepository {

    private final Map<String, Recipe> recipes = new HashMap<>();
    private Yaml yaml;

    private QualityRepository qualityRepository;
    private ConfigHandler configHandler;

    @Override
    public void deserialize(MedievalBreweryPlugin plugin) {
        qualityRepository = plugin.getQualityRepository();
        configHandler = plugin.getConfigHandler();

        yaml = new Yaml("recipes");
        yaml.load(plugin);

        if(load(plugin)) {
            plugin.getLogger().info("Successfully loaded " + recipes.size() + " recipe(s)");
        } else {
            if(setup(plugin)) {
                plugin.getLogger().info("Successfully setup default recipes");
            }
        }
    }

    @Override
    public boolean load(MedievalBreweryPlugin plugin) {
        FileConfiguration configuration = yaml.getConfiguration();
        ConfigurationSection configurationSection = configuration.getConfigurationSection("");
        if(configurationSection == null) return false;

        for(String key : configurationSection.getKeys(false)) {
            Recipe recipe = new Recipe();

            List<String> ingredients = configurationSection.getStringList(key + ".ingredients");
            for(String ingredientString : ingredients) {
                String[] strings = ingredientString.split(",");
                if(strings.length < 2) continue;

                Material material = Material.valueOf(strings[0]);
                int amount = Integer.parseInt(strings[1]);

                recipe.addIngredient(Ingredient.create(material, amount));
            }

            List<String> effects = configurationSection.getStringList(key + ".effects");
            for(String effectString : effects) {
                String[] strings = effectString.split(",");
                if(strings.length < 3) continue;

                String type = strings[0];
                int duration = Integer.parseInt(strings[1]);
                int amplifier = Integer.parseInt(strings[2]);

                recipe.addEffect(Effect.create(type, duration, amplifier));
            }

            String material = configurationSection.getString(key + ".material");
            if(material != null) recipe.setMaterial(Material.valueOf(material));

            String chatColor = configuration.getString(key + ".chatColor");
            if(chatColor != null) recipe.setChatColor(ChatColor.valueOf(chatColor));

            String potionColor = configuration.getString(key + ".potionColor");
            if(potionColor != null) {
                String[] strings = potionColor.split(",");
                if(strings.length == 3) {
                    int r = Integer.parseInt(strings[0]);
                    int g = Integer.parseInt(strings[1]);
                    int b = Integer.parseInt(strings[2]);
                    recipe.setPotionColor(Color.fromRGB(r, g, b));
                }
            }

            String description = configuration.getString(key + ".description");
            if(description != null) recipe.setDescription(description);

            int customModelData = configuration.getInt(key + ".customModelData");
            recipe.setCustomModelData(customModelData);

            int seconds = configuration.getInt(key + ".seconds");
            recipe.setSeconds(seconds);

            int uses = configuration.getInt(key + ".uses");
            recipe.setUses(uses);

            recipes.put(key, recipe);
        }

        return !recipes.isEmpty();
    }

    @Override
    public boolean setup(MedievalBreweryPlugin plugin) {
        FileConfiguration configuration = yaml.getConfiguration();

        List<String> ingredients = new ArrayList<>();
        ingredients.add("WHEAT_SEEDS,4");
        ingredients.add("WHEAT,16");
        ingredients.add("WATER_BUCKET,1");
        configuration.set("beer.ingredients", ingredients);

        List<String> effects = new ArrayList<>();
        effects.add("luck,600,1");
        effects.add("nausea,200,0");
        configuration.set("beer.effects", effects);
        configuration.set("beer.material", Material.POTION.name());
        configuration.set("beer.chatColor", ChatColor.YELLOW.name());
        configuration.set("beer.potionColor", "255,0,0");
        configuration.set("beer.description", "Perfect thirst quencher");
        configuration.set("beer.customModelData", 0);
        configuration.set("beer.seconds", 120);
        configuration.set("beer.uses", 12);

        yaml.save();
        return load(plugin);
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
            if(potionMeta != null && recipe.getPotionColor() != null) {

                potionMeta.setColor(recipe.getPotionColor());
                potionMeta.setDisplayName(Message.ITEM_NAME.parameterize(recipe.getChatColor(), recipeName));
                potionMeta.setCustomModelData(recipe.getCustomModelData());
                potionMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

                String data;
                List<String> lore;

                if(qualities && quality != null) {
                    lore = Message.ITEM_LORE_QUALITY.parameterizeList(quality.getDisplayName(), recipe.getDescription());
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

                itemMeta.setDisplayName(Message.ITEM_NAME.prefix(recipe.getChatColor(), recipeName));
                itemMeta.setCustomModelData(recipe.getCustomModelData());
                itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

                String data;
                List<String> lore;

                if(qualities && quality != null) {
                    lore = Message.ITEM_LORE_QUALITY.parameterizeList(quality.getDisplayName(), recipe.getDescription());
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
}