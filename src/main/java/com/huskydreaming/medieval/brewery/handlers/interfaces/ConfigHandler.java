package com.huskydreaming.medieval.brewery.handlers.interfaces;

import com.huskydreaming.huskycore.handlers.interfaces.Handler;

public interface ConfigHandler extends Handler {

    int getLimit();

    boolean hasQualities();

    boolean hasNotifyPlayer();

    String getLanguage();
}
