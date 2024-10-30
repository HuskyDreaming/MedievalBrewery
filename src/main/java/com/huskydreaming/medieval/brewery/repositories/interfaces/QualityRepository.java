package com.huskydreaming.medieval.brewery.repositories.interfaces;

import com.huskydreaming.huskycore.HuskyPlugin;
import com.huskydreaming.huskycore.repositories.Repository;
import com.huskydreaming.medieval.brewery.data.Quality;

public interface QualityRepository extends Repository {

    boolean load(HuskyPlugin plugin);

    boolean setup(HuskyPlugin plugin);

    Quality getQuality(String name);

    String getQuality();
}
