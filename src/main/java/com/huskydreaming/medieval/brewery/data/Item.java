package com.huskydreaming.medieval.brewery.data;

import org.bukkit.Color;
import org.bukkit.Material;

import java.util.Objects;

public class Item {

    private String material;
    private String displayName;
    private String description;
    private Color potionColor;
    private int customModelData;


    public Material getMaterial() {
        return Material.valueOf(material);
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Color getPotionColor() {
        return potionColor;
    }

    public void setPotionColor(Color potionColor) {
        this.potionColor = potionColor;
    }

    public int getCustomModelData() {
        return customModelData;
    }

    public void setCustomModelData(int customModelData) {
        this.customModelData = customModelData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item item)) return false;
        return customModelData == item.customModelData &&
                Objects.equals(material, item.material) &&
                Objects.equals(displayName, item.displayName) &&
                Objects.equals(description, item.description) &&
                Objects.equals(potionColor, item.potionColor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(material, displayName, description, potionColor, customModelData);
    }
}