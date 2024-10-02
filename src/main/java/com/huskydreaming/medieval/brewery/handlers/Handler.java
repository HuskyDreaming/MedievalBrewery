package com.huskydreaming.medieval.brewery.handlers;

import com.huskydreaming.medieval.brewery.MedievalBreweryPlugin;

public interface Handler {

    default void initialize(MedievalBreweryPlugin plugin) {

    }

    default void finalize(MedievalBreweryPlugin plugin) {

    }
}
