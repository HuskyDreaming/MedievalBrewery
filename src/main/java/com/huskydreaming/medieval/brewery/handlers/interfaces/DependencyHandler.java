package com.huskydreaming.medieval.brewery.handlers.interfaces;

import com.huskydreaming.huskycore.handlers.interfaces.Handler;
import org.bukkit.block.Block;

public interface DependencyHandler extends Handler {
    boolean isBlockInsideRegion(Block block);

    boolean isWorldGuard();
}
