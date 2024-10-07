package com.huskydreaming.medieval.brewery.data;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;

import java.util.*;

public class Recipe {

    private final Set<Ingredient> ingredients;
    private final Set<Effect> effects;

    private Material material;
    private ChatColor itemColor;
    private Color potionColor;

    private int data;
    private int seconds;
    private int uses;

    public Recipe() {
        ingredients = new HashSet<>();
        effects = new HashSet<>();
    }

    public void addIngredient(Ingredient ingredient) {
        ingredients.add(ingredient);
    }

    public Set<Ingredient> getIngredients() {
        return Collections.unmodifiableSet(ingredients);
    }


    public void addEffect(Effect effect) {
        effects.add(effect);
    }

    public Set<Effect> getEffects() {
        return Collections.unmodifiableSet(effects);
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public Color getColor() {
        return potionColor;
    }

    public void setColor(Color color) {
        this.potionColor = color;
    }

    public ChatColor getItemColor() {
        return itemColor;
    }

    public void setItemColor(ChatColor itemColor) {
        this.itemColor = itemColor;
    }

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public int getUses() {
        return uses;
    }

    public void setUses(int uses) {
        this.uses = uses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Recipe recipe = (Recipe) o;
        return data == recipe.data &&
                seconds == recipe.seconds &&
                uses == recipe.uses &&
                material == recipe.material &&
                itemColor == recipe.itemColor &&
                Objects.equals(potionColor, recipe.potionColor) &&
                Objects.equals(ingredients, recipe.ingredients) &&
                Objects.equals(effects, recipe.effects);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ingredients, effects, material, itemColor, potionColor, data, seconds, uses);
    }
}