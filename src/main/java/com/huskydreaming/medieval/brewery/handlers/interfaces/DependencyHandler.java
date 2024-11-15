package com.huskydreaming.medieval.brewery.handlers.interfaces;

import com.huskydreaming.huskycore.handlers.interfaces.Handler;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public interface DependencyHandler extends Handler {

    boolean isBlockInsideRegion(Player player, Block block);

    boolean isWorldGuard();
}
