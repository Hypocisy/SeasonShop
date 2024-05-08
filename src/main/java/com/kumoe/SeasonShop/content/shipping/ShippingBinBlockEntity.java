package com.kumoe.SeasonShop.content.shipping;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ShippingBinBlockEntity extends AbstractShippingBinBlockEntity{
    public ShippingBinBlockEntity(BlockEntityType<? extends AbstractShippingBinBlockEntity> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }
}
