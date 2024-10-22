package com.huskydreaming.medieval.brewery.data;

import com.huskydreaming.medieval.brewery.enumerations.BreweryStatus;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

public class Brewery {

    private Position position;
    private BreweryStatus status;
    private String recipeName;
    private String qualityName;
    private long timeStamp;
    private UUID uuid;
    private int remaining;
    private int waterLevel;

    private transient Hologram hologram;

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public BreweryStatus getStatus() {
        return status;
    }

    public void setStatus(BreweryStatus status) {
        this.status = status;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public String getQualityName() {
        return qualityName;
    }

    public void setQualityName(String qualityName) {
        this.qualityName = qualityName;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public UUID getOwner() {
        return uuid;
    }

    public void setOwner(Player player) {
        this.uuid = player.getUniqueId();
    }

    public int getRemaining() {
        return remaining;
    }

    public void setRemaining(int remaining) {
        this.remaining = remaining;
    }

    public int getWaterLevel() {
        return waterLevel;
    }

    public void setWaterLevel(int waterLevel) {
        this.waterLevel = waterLevel;
    }

    public void addWaterLevel() {
        this.waterLevel += 1;
    }

    public Hologram getHologram() {
        return hologram;
    }

    public void setHologram(Hologram hologram) {
        this.hologram = hologram;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Brewery brewery)) return false;
        return timeStamp == brewery.timeStamp &&
                remaining == brewery.remaining &&
                waterLevel == brewery.waterLevel &&
                status == brewery.status &&
                Objects.equals(position, brewery.position) &&
                Objects.equals(recipeName, brewery.recipeName) &&
                Objects.equals(qualityName, brewery.qualityName) &&
                Objects.equals(uuid, brewery.uuid) &&
                Objects.equals(hologram, brewery.hologram);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, status, recipeName, qualityName, timeStamp, uuid, remaining, waterLevel, hologram);
    }
}