package com.kumoe.SeasonShop.content.shop;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
public abstract class AbstractShopBlock<E extends ShopBlockEntity> extends BaseEntityBlock {

    protected final Supplier<BlockEntityType<? extends E>> blockEntityType;

    protected AbstractShopBlock(Properties pProperties, Supplier<BlockEntityType<? extends E>> pBlockEntityType) {
        super(pProperties);
        this.blockEntityType = pBlockEntityType;
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public ShopBlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return null;
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        return InteractionResult.PASS;
    }

}
