package com.huskydreaming.medieval.brewery.data;
import org.bukkit.ChatColor;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class Recipe {

    private final Set<Ingredient> ingredients;
    private final Set<String> effects;
    private ChatColor color;
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


    public void addEffect(PotionEffectType effect) {
        effects.add(effect.getKey().getKey());
    }

    public Set<String> getEffects() {
        return Collections.unmodifiableSet(effects);
    }

    public ChatColor getColor() {
        return color;
    }

    public void setColor(ChatColor color) {
        this.color = color;
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
                color == recipe.color &&
                Objects.equals(ingredients, recipe.ingredients) &&
                Objects.equals(effects, recipe.effects);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ingredients, effects, color, data, seconds, uses);
    }
}