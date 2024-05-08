package com.kumoe.SeasonShop.content.shop;

import com.kumoe.SeasonShop.init.SSBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

public class ShopBlock extends AbstractShopBlock<ShopBlockEntity> {
    public ShopBlock(Properties pProperties) {
        super(pProperties, SSBlock.SHOP_BE::get);
    }

    @Override
    public ShopBlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return SSBlock.SHOP_BE.create(pPos, pState);
    }

    @Override
    public @NotNull InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide()) {
            BlockEntity entity = pLevel.getBlockEntity(pPos);
            if (entity instanceof ShopBlockEntity blockEntity) {
                NetworkHooks.openScreen((ServerPlayer) pPlayer, blockEntity, pPos);
            }
        }

        return InteractionResult.sidedSuccess(pLevel.isClientSide());
    }
}
