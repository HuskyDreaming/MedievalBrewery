package com.huskydreaming.medieval.brewery.repositories.interfaces;

import com.huskydreaming.medieval.brewery.MedievalBreweryPlugin;
import com.huskydreaming.medieval.brewery.data.Quality;
import com.huskydreaming.medieval.brewery.repositories.Repository;

public interface QualityRepository extends Repository {

    boolean load(MedievalBreweryPlugin plugin);

    boolean setup(MedievalBreweryPlugin plugin);

    Quality getQuality(String name);

    String getQuality();
}
