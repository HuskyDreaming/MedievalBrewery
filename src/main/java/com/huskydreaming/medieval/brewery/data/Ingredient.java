package com.huskydreaming.medieval.brewery.data;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class Ingredient {

    private final String material;
    private final int amount;

    public Ingredient(Material material, int amount) {
        this.material = material.name();
        this.amount = amount;
    }

    public Ingredient(ItemStack itemStack) {
        this.material = itemStack.getType().name();
        this.amount = itemStack.getAmount();
    }

    public Material toMaterial() {
        return Material.getMaterial(material);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ingredient that = (Ingredient) o;
        return amount == that.amount && Objects.equals(material, that.material);
    }

    @Override
    public int hashCode() {
        return Objects.hash(material, amount);
    }
}
