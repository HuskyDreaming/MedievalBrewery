package com.huskydreaming.medieval.brewery.data;

import java.util.Objects;

public class Quality {

    private String displayName;
    private double probability;
    private int multiplier;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public double getProbability() {
        return probability;
    }

    public void setProbability(double probability) {
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
        if (!(o instanceof Quality quality)) return false;
        return multiplier == quality.multiplier &&
                Double.compare(probability, quality.probability) == 0 &&
                Objects.equals(displayName, quality.displayName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(displayName, probability, multiplier);
    }
}