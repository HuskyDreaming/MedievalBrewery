package com.huskydreaming.medieval.brewery.repositories;

import com.huskydreaming.medieval.brewery.MedievalBreweryPlugin;

public interface Repository {

    default void deserialize(MedievalBreweryPlugin plugin) {

    }

    default void serialize(MedievalBreweryPlugin plugin) {

    }
}