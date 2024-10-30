package com.huskydreaming.medieval.brewery.data;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

public class Effect {

    private final String type;
    private final int duration;
    private final int amplifier;

    public static Effect create(String type, int duration, int amplifier) {
        return new Effect(type, duration, amplifier);
    }

    public Effect(String type, int duration, int amplifier) {
        this.type = type;
        this.duration = duration;
        this.amplifier = amplifier;
    }

    public PotionEffect toPotionEffect(int multiplier) {
        PotionEffectType potionEffectType = PotionEffectType.getByName(type);
        if (potionEffectType == null) return null;
        return new PotionEffect(potionEffectType, duration * multiplier, amplifier * multiplier);
    }

    public String getType() {
        return type;
    }

    public int getDuration() {
        return duration;
    }

    public int getAmplifier() {
        return amplifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Effect effect = (Effect) o;
        return duration == effect.duration && amplifier == effect.amplifier && Objects.equals(type, effect.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, duration, amplifier);
    }
}