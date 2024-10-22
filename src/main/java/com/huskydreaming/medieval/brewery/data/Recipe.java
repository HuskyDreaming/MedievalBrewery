package com.huskydreaming.medieval.brewery.data;

import java.util.*;

public class Recipe {

    private final Set<Ingredient> ingredients;
    private final Set<Effect> effects;

    private Item item;
    private String permission;

    private int seconds;
    private int uses;
    private int water;

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

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
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

    public int getWater() {
        return water;
    }

    public void setWater(int water) {
        this.water = water;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Recipe recipe)) return false;
        return seconds == recipe.seconds &&
                uses == recipe.uses &&
                water == recipe.water &&
                Objects.equals(ingredients, recipe.ingredients) &&
                Objects.equals(effects, recipe.effects) &&
                Objects.equals(item, recipe.item);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ingredients, effects, item, seconds, uses, water);
    }
}