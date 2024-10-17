package com.huskydreaming.medieval.brewery.data;

import java.util.Objects;

public class Quality {

    private double probability;
    private int multiplier;

    public double getProbability() {
        return probability;
    }

    public void setProbability(float probability) {
        this.probability = probability;
    }

    public int getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(int multiplier) {
        this.multiplier = multiplier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quality quality = (Quality) o;
        return Double.compare(probability, quality.probability) == 0 &&
                multiplier == quality.multiplier;
    }

    @Override
    public int hashCode() {
        return Objects.hash(probability, multiplier);
    }
}
