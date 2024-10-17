package com.huskydreaming.medieval.brewery.data;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

public class Effect {

    private String type;
    private int duration;
    private int amplifier;

    public void setType(PotionEffectType type) {
        this.type = type.getKey().getKey();
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setAmplifier(int amplifier) {
        this.amplifier = amplifier;
    }

    public PotionEffect toPotionEffect(int multiplier) {
        PotionEffectType potionEffectType = PotionEffectType.getByName(type);
        if (potionEffectType == null) return null;
        return new PotionEffect(potionEffectType, duration * multiplier, amplifier * multiplier);
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
