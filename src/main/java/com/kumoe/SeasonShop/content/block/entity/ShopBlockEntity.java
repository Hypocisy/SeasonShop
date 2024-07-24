package com.kumoe.SeasonShop.content.block.entity;

import com.kumoe.SeasonShop.content.menu.ShopMenu;
import com.kumoe.SeasonShop.init.SeasonShopBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import org.jetbrains.annotations.Nullable;

public class ShopBlockEntity extends ChestBlockEntity {
    private final ChestLidController chestLidController;
    private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
        protected void onOpen(Level level, BlockPos blockPos, BlockState state) {
            ShopBlockEntity.playSound(level, blockPos, state, SoundEvents.CHEST_OPEN);
        }

        protected void onClose(Level level, BlockPos blockPos, BlockState state) {
            ShopBlockEntity.playSound(level, blockPos, state, SoundEvents.CHEST_CLOSE);
        }

        protected void openerCountChanged(Level level, BlockPos blockPos, BlockState state, int pEventId, int pEventParam) {
            signalOpenCount(level, blockPos, state, pEventId, pEventParam);
        }

        @Override
        protected boolean isOwnContainer(Player pPlayer) {
            return pPlayer.containerMenu instanceof ShopMenu;
        }
    };

    public ShopBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
        this.chestLidController = new ChestLidController();
    }

    static void playSound(Level pLevel, BlockPos pPos, BlockState pState, SoundEvent pSound) {
        ChestType chesttype = pState.getValue(ChestBlock.TYPE);
        double d0 = (double) pPos.getX() + 0.5;
        double d1 = (double) pPos.getY() + 0.5;
        double d2 = (double) pPos.getZ() + 0.5;
        if (chesttype == ChestType.RIGHT) {
            Direction direction = ChestBlock.getConnectedDirection(pState);
            d0 += (double) direction.getStepX() * 0.5;
            d2 += (double) direction.getStepZ() * 0.5;
        }
        pLevel.playSound(null, d0, d1, d2, pSound, SoundSource.BLOCKS, 0.5F, pLevel.random.nextFloat() * 0.1F + 0.9F);
    }

    protected static void lidAnimateTick(Level pLevel, BlockPos pPos, BlockState pState, ShopBlockEntity pBlockEntity) {
        pBlockEntity.getChestLidController().tickLid();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return this.canOpen(pPlayer) ? new ShopMenu(SeasonShopBlocks.SHOP_BLOCK_MENU.get(), pContainerId, pPlayerInventory, this) : null;
    }

    @Override
    public void startOpen(Player pPlayer) {
        if (!this.remove && !pPlayer.isSpectator()) {
            this.openersCounter.incrementOpeners(pPlayer, this.getLevel(), this.getBlockPos(), this.getBlockState());
        }

    }

    @Override
    public void stopOpen(Player pPlayer) {
        if (!this.remove && !pPlayer.isSpectator()) {
            this.openersCounter.decrementOpeners(pPlayer, this.getLevel(), this.getBlockPos(), this.getBlockState());
        }

    }

    public ChestLidController getChestLidController() {
        return this.chestLidController;
    }

    @Override
    public float getOpenNess(float pPartialTicks) {
        return this.chestLidController.getOpenness(pPartialTicks);
    }

    @Override
    public boolean triggerEvent(int pId, int pType) {
        if (pId == 1) {
            this.chestLidController.shouldBeOpen(pType > 0);
            return true;
        } else {
            return super.triggerEvent(pId, pType);
        }
    }
}