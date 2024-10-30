package com.huskydreaming.medieval.brewery.data;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;

public class Ingredient {

    private final String material;
    private final int amount;
    private int customModelData;

    public static Ingredient create(Material material, int amount, int customModelData) {
        return new Ingredient(material, amount, customModelData);
    }

    public Ingredient(Material material, int amount, int customModelData) {
        this.material = material.name();
        this.amount = amount;
        this.customModelData = customModelData;
    }

    public Ingredient(ItemStack itemStack) {
        this.material = itemStack.getType().name();
        this.amount = itemStack.getAmount();

        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null && itemMeta.hasCustomModelData()) {
            this.customModelData = itemMeta.getCustomModelData();
        }
    }

    public Material getMaterial() {
        return Material.valueOf(material);
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ingredient that)) return false;
        return amount == that.amount &&
                customModelData == that.customModelData &&
                Objects.equals(material, that.material);
    }

    @Override
    public int hashCode() {
        return Objects.hash(material, amount, customModelData);
    }
}