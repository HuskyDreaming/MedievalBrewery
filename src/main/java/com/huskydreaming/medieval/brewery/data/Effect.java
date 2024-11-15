package com.huskydreaming.medieval.brewery.data;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public record Effect(String type, int duration, int amplifier) {

    public static Effect create(String type, int duration, int amplifier) {
        return new Effect(type, duration, amplifier);
    }

    public PotionEffect toPotionEffect(int multiplier) {
        PotionEffectType potionEffectType = PotionEffectType.getByName(type);
        if (potionEffectType == null) return null;
        return new PotionEffect(potionEffectType, duration * multiplier, amplifier * multiplier);
    }
}