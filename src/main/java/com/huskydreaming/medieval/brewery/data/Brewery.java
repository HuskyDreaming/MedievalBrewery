package com.huskydreaming.medieval.brewery.data;

import com.huskydreaming.medieval.brewery.enumerations.BreweryStatus;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

public class Brewery {

    private Position position;
    private transient Hologram hologram;
    private BreweryStatus status;
    private String recipeName;
    private long timeStamp;
    private UUID uuid;
    private int remaining;

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Hologram getHologram() {
        return hologram;
    }

    public void setHologram(Hologram hologram) {
        this.hologram = hologram;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Brewery brewery = (Brewery) o;
        return timeStamp == brewery.timeStamp && remaining == brewery.remaining && Objects.equals(position, brewery.position) && Objects.equals(hologram, brewery.hologram) && status == brewery.status && Objects.equals(recipeName, brewery.recipeName) && Objects.equals(uuid, brewery.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, hologram, status, recipeName, timeStamp, uuid, remaining);
    }
}
