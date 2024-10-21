package com.huskydreaming.medieval.brewery.repositories.interfaces;

import com.huskydreaming.medieval.brewery.data.Quality;
import com.huskydreaming.medieval.brewery.repositories.Repository;

public interface QualityRepository extends Repository {

    Quality getQuality(String name);

    String getQuality();
}
