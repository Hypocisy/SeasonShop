package com.kumoe.SeasonShop.api;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;

public interface IOwnBlockManager {

    String getOwner(BlockPos pos);

    void setOwner(BlockPos pos, ServerPlayer player);

    void removeOwner(BlockPos pos);

}
