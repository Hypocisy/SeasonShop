package com.kumoe.SeasonShop.content.menu;

import com.kumoe.SeasonShop.api.ModUtils;
import com.kumoe.SeasonShop.content.block.entity.BuyBackBlockEntity;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class BuybackMenu extends AbstractContainerMenu {
    protected final int containerRows = 3;
    protected final Inventory playerInventory;
    private final BuyBackBlockEntity container;

    public BuybackMenu(MenuType<BuybackMenu> pType, int pContainerId, Inventory pPlayerInventory, BuyBackBlockEntity pContainer) {
        super(pType, pContainerId);
        this.container = pContainer;
        this.playerInventory = pPlayerInventory;
        this.container.startOpen(pPlayerInventory.player);
        int e = (this.containerRows - 4) * 18;
        int row;
        int slot;
        for (row = 0; row < this.containerRows; ++row) {
            for (slot = 0; slot < 9; ++slot) {
                this.addSlot(new CustomSlot(pContainer, slot + row * 9, 8 + slot * 18, -2 + row * 18));
            }
        }
        this.container.clearContent();
        NonNullList<ItemStack> byPage = ModUtils.getItemsByPage(container.getCurrentPage());
        this.container.setItems(byPage);
        this.container.setChanged();
        // add player inv
        for (row = 0; row < 3; ++row) {
            for (slot = 0; slot < 9; ++slot) {
                this.addSlot(new Slot(pPlayerInventory, slot + row * 9 + 9, 8 + slot * 18, -10+123 + row * 18 + e));
            }
        }

        for (row = 0; row < 9; ++row) {
            this.addSlot(new Slot(pPlayerInventory, row, 8 + row * 18, -10+179 + e));
        }

    }

    public BuybackMenu(MenuType<BuybackMenu> menu, int windowId, Inventory playerInventory, @Nullable FriendlyByteBuf data) {
        this(menu, windowId, playerInventory, getTileEntity(playerInventory, data));
    }


    protected static BuyBackBlockEntity getTileEntity(Inventory playerInventory, @Nullable FriendlyByteBuf data) {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null");
        Objects.requireNonNull(data, "data cannot be null");
        BlockEntity tileAtPos = playerInventory.player.level().getBlockEntity(data.readBlockPos());
        if (tileAtPos instanceof BuyBackBlockEntity bin) {
            bin.setCurrentPage(data.readInt());
            return bin;
        } else {
            throw new IllegalStateException("Tile entity is not correct! " + tileAtPos);
        }
    }

    /**
     * @param player 玩家
     * @param pIndex 快速移动到的slot的id
     * @return {@link ItemStack}如果拒绝移动，否则返回快速移动的物品
     */
    @Override
    public ItemStack quickMoveStack(Player player, int pIndex) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(pIndex);
        if (slot.hasItem()) {
            ItemStack slotItem = slot.getItem();
            itemStack = slotItem.copy();
            if (pIndex < this.containerRows * 9) {
                if (!this.moveItemStackTo(slotItem, this.containerRows * 9, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(slotItem, 0, this.containerRows * 9, false)) {
                return ItemStack.EMPTY;
            }

            if (slotItem.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemStack;
    }

    /**
     * @param player player who opened container
     * @return container is still Valid
     */
    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    @Override
    public void removed(Player pPlayer) {
        super.removed(pPlayer);
        this.container.stopOpen(pPlayer);
    }

    public BuyBackBlockEntity getContainer() {
        return container;
    }
}
