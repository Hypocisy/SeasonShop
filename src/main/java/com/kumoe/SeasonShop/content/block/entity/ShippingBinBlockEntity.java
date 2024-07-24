package com.kumoe.SeasonShop.content.block.entity;

import com.kumoe.SeasonShop.api.ModUtils;
import com.kumoe.SeasonShop.content.menu.ShippingBinMenu;
import com.kumoe.SeasonShop.init.SeasonShop;
import com.kumoe.SeasonShop.init.SeasonShopBlocks;
import com.kumoe.SeasonShop.network.NetworkHandler;
import com.kumoe.SeasonShop.network.PricesPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.ChestLidController;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ShippingBinBlockEntity extends ChestBlockEntity {

    protected static final int containerSize = 18;
    private final ChestLidController chestLidController;
    private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
        protected void onOpen(Level level, BlockPos blockPos, BlockState state) {
            ShippingBinBlockEntity.playSound(level, blockPos, state, SoundEvents.CHEST_OPEN);
        }

        protected void onClose(Level level, BlockPos blockPos, BlockState state) {
            ShippingBinBlockEntity.playSound(level, blockPos, state, SoundEvents.CHEST_CLOSE);
        }

        protected void openerCountChanged(Level level, BlockPos blockPos, BlockState state, int pEventId, int pEventParam) {
            signalOpenCount(level, blockPos, state, pEventId, pEventParam);
        }

        @Override
        protected boolean isOwnContainer(Player pPlayer) {
            return pPlayer.containerMenu instanceof ShippingBinMenu;
        }
    };
    private final NonNullList<ItemStack> items = NonNullList.withSize(18, ItemStack.EMPTY);
    private UUID uuid;

    public ShippingBinBlockEntity(BlockEntityType<? extends ShippingBinBlockEntity> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
        this.chestLidController = new ChestLidController();
    }

    public static void playSound(Level pLevel, BlockPos pPos, BlockState pState, SoundEvent pSound) {
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

    public static void lidAnimateTick(Level pLevel, BlockPos pPos, BlockState pState, ShippingBinBlockEntity pBlockEntity) {
        pBlockEntity.getChestLidController().tickLid();
//        SeasonShop.getLogger().debug("Current game time: {}", pLevel.getGameTime());
        if (pLevel.getServer() != null && pLevel.getServer().getTickCount() % 18000 == 0) {
            // todo: send a packet to sell item
            // todo: render how much player sold
            SeasonShop.logger().debug("Now sell items");
            var totalPrice = 0d;
            for (ItemStack itemStack : pBlockEntity.items) {
                totalPrice += ModUtils.getTotalItemPrice(itemStack);
            }
            // remove sold items
            pBlockEntity.items.clear();
            NetworkHandler.sendToServer(PricesPacket.create(pBlockEntity.getOwner(), totalPrice, pPos));
        }
    }

    @Override
    public int getContainerSize() {
        return containerSize;
    }

    @Override
    public void startOpen(Player pPlayer) {
        if (!this.remove && !pPlayer.isSpectator()&& this.getLevel()!=null) {
            this.openersCounter.incrementOpeners(pPlayer, this.getLevel(), this.getBlockPos(), this.getBlockState());
        }

    }

    @Override
    public void stopOpen(Player pPlayer) {
        if (!this.remove && !pPlayer.isSpectator()&& this.getLevel()!=null) {
            this.openersCounter.decrementOpeners(pPlayer, this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }

    @Nullable
    @Override
    public ShippingBinMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return this.canOpen(pPlayer) ? new ShippingBinMenu(SeasonShopBlocks.SHIPPING_BIN_BLOCK_MENU.get(), pContainerId, pPlayerInventory, this) : null;
    }

    @Override
    public float getOpenNess(float pPartialTicks) {
        return this.chestLidController.getOpenness(pPartialTicks);
    }

    @Override
    public Component getDisplayName() {
        return getCustomName() == null ? Component.translatable("block.season_shop.shipping_bin") : getCustomName();
    }

    public ChestLidController getChestLidController() {
        return chestLidController;
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

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        if (!pTag.isEmpty() && pTag.contains("ownEntity.playerUuid")) {
            uuid = pTag.getUUID("ownEntity.playerUuid");
        }

    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        if (!pTag.isEmpty() && uuid != null) {
            pTag.putUUID("ownEntity.playerUuid", uuid);
        }
    }

    public UUID getOwner() {
        return this.uuid;
    }

    public void setOwner(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return this.items;
    }
}
