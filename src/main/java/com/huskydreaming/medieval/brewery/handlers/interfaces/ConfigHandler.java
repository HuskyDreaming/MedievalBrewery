package com.huskydreaming.medieval.brewery.handlers.interfaces;

import com.huskydreaming.medieval.brewery.handlers.Handler;

public interface ConfigHandler extends Handler {
    int getLimit();

    boolean hasQualities();

    boolean hasNotifyPlayer();
}
