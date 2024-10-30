package com.huskydreaming.medieval.brewery.repositories.implementations;

import com.huskydreaming.huskycore.HuskyPlugin;
import com.huskydreaming.huskycore.storage.Yaml;
import com.huskydreaming.huskycore.utilities.Util;
import com.huskydreaming.medieval.brewery.MedievalBreweryPlugin;
import com.huskydreaming.medieval.brewery.data.*;
import com.huskydreaming.medieval.brewery.handlers.interfaces.ConfigHandler;
import com.huskydreaming.medieval.brewery.repositories.interfaces.QualityRepository;
import com.huskydreaming.medieval.brewery.repositories.interfaces.RecipeRepository;
import com.huskydreaming.medieval.brewery.enumerations.Message;
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

import java.util.*;

public class RecipeRepositoryImpl implements RecipeRepository {

    private final Map<String, Recipe> recipes = new HashMap<>();
    private Yaml yaml;

    private QualityRepository qualityRepository;
    private ConfigHandler configHandler;

    @Override
    public void postDeserialize(HuskyPlugin plugin) {
        recipes.clear();

        qualityRepository = plugin.provide(QualityRepository.class);
        configHandler = plugin.provide(ConfigHandler.class);

        if (yaml == null) yaml = new Yaml("recipes");
        yaml.load(plugin);

        if (load(plugin)) {
            plugin.getLogger().info("[Storage] Successfully loaded " + recipes.size() + " recipe(s)");
        } else {
            if (setup(plugin)) {
                plugin.getLogger().info("[Storage] Successfully setup default recipes");
            }
        }
    }

    @Override
    public boolean load(HuskyPlugin plugin) {
        FileConfiguration configuration = yaml.getConfiguration();
        ConfigurationSection configurationSection = configuration.getConfigurationSection("");
        if (configurationSection == null) return false;

        for (String key : configurationSection.getKeys(false)) {
            Recipe recipe = new Recipe();

            List<String> ingredients = configurationSection.getStringList(key + ".ingredients");
            for (String ingredientString : ingredients) {
                String[] strings = ingredientString.split(",");

                Material material = null;

                int amount = 1;
                if (strings.length >= 2) {
                    material = Material.valueOf(strings[0]);
                    amount = Integer.parseInt(strings[1]);
                }

                int customModelData = 0;
                if (strings.length == 3) {
                    customModelData = Integer.parseInt(strings[2]);
                }
                if (material == null) continue;

                recipe.addIngredient(Ingredient.create(material, amount, customModelData));
            }

            List<String> effects = configurationSection.getStringList(key + ".effects");
            for (String effectString : effects) {
                String[] strings = effectString.split(",");
                if (strings.length < 3) continue;

                String type = strings[0];
                int duration = Integer.parseInt(strings[1]);
                int amplifier = Integer.parseInt(strings[2]);

                recipe.addEffect(Effect.create(type, duration, amplifier));
            }

            Item item = new Item();

            String material = configurationSection.getString(key + ".item.material");
            if (material != null) item.setMaterial(material);

            String displayName = configuration.getString(key + ".item.displayName");
            if (displayName != null) item.setDisplayName(displayName);

            String potionColor = configuration.getString(key + ".item.potionColor");
            if (potionColor != null) {
                String[] strings = potionColor.split(",");
                if (strings.length == 3) {
                    int r = Integer.parseInt(strings[0]);
                    int g = Integer.parseInt(strings[1]);
                    int b = Integer.parseInt(strings[2]);
                    item.setPotionColor(Color.fromRGB(r, g, b));
                }
            }

            String description = configuration.getString(key + ".item.description");
            if (description != null) item.setDescription(description);

            int customModelData = configuration.getInt(key + ".item.customModelData");
            item.setCustomModelData(customModelData);

            recipe.setItem(item);

            int seconds = configuration.getInt(key + ".options.seconds");
            recipe.setSeconds(seconds);

            int uses = configuration.getInt(key + ".options.uses");
            recipe.setUses(uses);

            int water = configuration.getInt(key + ".options.water");
            recipe.setWater(water);

            String permission = configuration.getString(key + ".permission");
            if (permission != null) recipe.setPermission(permission);

            recipes.put(key, recipe);
        }

        return !recipes.isEmpty();
    }

    @Override
    public boolean setup(HuskyPlugin plugin) {
        FileConfiguration configuration = yaml.getConfiguration();

        List<String> ingredients = new ArrayList<>();
        ingredients.add("WHEAT_SEEDS,4");
        ingredients.add("WHEAT,16");
        configuration.set("beer.ingredients", ingredients);

        List<String> effects = new ArrayList<>();
        effects.add("luck,600,1");
        effects.add("nausea,200,0");
        configuration.set("beer.effects", effects);
        configuration.set("beer.item.material", Material.POTION.name());
        configuration.set("beer.item.displayName", "&eBeer");
        configuration.set("beer.item.potionColor", "185,166,94");
        configuration.set("beer.item.description", "Perfect thirst quencher");
        configuration.set("beer.item.customModelData", 0);
        configuration.set("beer.options.seconds", 120);
        configuration.set("beer.options.uses", 12);
        configuration.set("beer.options.water", 2);
        configuration.set("beer.permission", "brewery.brew.beer");

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
        Item item = recipe.getItem();

        if (item.getMaterial() == null) return null;
        ItemStack itemStack = new ItemStack(item.getMaterial());

        Quality quality = null;
        String qualityName = brewery.getQualityName();
        boolean qualities = configHandler.hasQualities();
        if (qualities) {
            quality = qualityRepository.getQuality(qualityName);
        }

        if (item.getMaterial() == Material.POTION) {
            PotionMeta potionMeta = (PotionMeta) itemStack.getItemMeta();
            if (potionMeta != null && item.getPotionColor() != null) {

                potionMeta.setColor(item.getPotionColor());
                potionMeta.setDisplayName(Util.hex(item.getDisplayName()));
                potionMeta.setCustomModelData(item.getCustomModelData());
                potionMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

                String data;
                List<String> lore;

                if (qualities && quality != null) {
                    lore = Message.ITEM_LORE_QUALITY.parameterizeList(quality.getDisplayName(), item.getDescription());
                    data = recipeName + ":" + quality.getMultiplier();
                } else {
                    lore = Message.ITEM_LORE_DEFAULT.parameterizeList(item.getDescription());
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
            if (itemMeta != null) {

                itemMeta.setDisplayName(Util.hex(item.getDisplayName()));
                itemMeta.setCustomModelData(item.getCustomModelData());
                itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

                String data;
                List<String> lore;

                if (qualities && quality != null) {
                    lore = Message.ITEM_LORE_QUALITY.parameterizeList(quality.getDisplayName(), item.getDescription());
                    data = recipeName + ":" + quality.getMultiplier();
                } else {
                    lore = Message.ITEM_LORE_DEFAULT.parameterizeList(item.getDescription());
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

    @Override
    public Collection<Recipe> getRecipes() {
        return recipes.values();
    }
}