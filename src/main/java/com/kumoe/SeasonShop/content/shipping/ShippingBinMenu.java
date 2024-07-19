package com.kumoe.SeasonShop.content.shipping;

import com.kumoe.SeasonShop.init.SeasonShop;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Objects;
import java.util.UUID;

public class ShippingBinMenu extends AbstractContainerMenu {
    protected final ShippingBinBlockEntity container;
    protected final Inventory playerInventory;
    protected int containerRows = 3;
    protected int containerColumns = 6;


    public ShippingBinMenu(MenuType<ShippingBinMenu> menu, int windowId, Inventory pPlayerInventory, ShippingBinBlockEntity pContainer) {
        super(menu, windowId);
        this.container = pContainer;
        this.playerInventory = pPlayerInventory;
        this.addSlot();
        this.container.startOpen(pPlayerInventory.player);
    }

    public ShippingBinMenu(MenuType<ShippingBinMenu> menu, int windowId, Inventory pPlayerInventory, FriendlyByteBuf data) {
        this(menu, windowId, pPlayerInventory, getTileEntity(pPlayerInventory, data));
    }

    private static ShippingBinBlockEntity getTileEntity(Inventory playerInventory, FriendlyByteBuf data) {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null");
        Objects.requireNonNull(data, "data cannot be null");
        BlockEntity tileAtPos = playerInventory.player.level().getBlockEntity(data.readBlockPos());
        if (tileAtPos instanceof ShippingBinBlockEntity bin) {
            bin.setOwner(data.readUUID());
            return bin;
        } else {
            throw new IllegalStateException("Tile entity is not correct! " + tileAtPos);
        }
    }

    public static ShippingBinMenu factory(MenuType<ShippingBinMenu> shippingBinMenuMenuType, int i, Inventory inventory, FriendlyByteBuf byteBuf) {
        return new ShippingBinMenu(shippingBinMenuMenuType, i, inventory, byteBuf);
    }

    void addSlot() {
        int e = (this.containerRows - 4) * 18;
        int row;
        int slot;
        for (row = 0; row < this.containerRows; ++row) {
            for (slot = 0; slot < 6; ++slot) {
                this.addSlot(new SeasonSlot(this.container, slot + row * 6, 8 + slot * 18, 18 + row * 18));
            }
        }

        // add player inv
        for (row = 0; row < 3; ++row) {
            for (slot = 0; slot < 9; ++slot) {
                this.addSlot(new Slot(this.playerInventory, slot + row * 9 + 9, 8 + slot * 18, 123 + row * 18 + e));
            }
        }

        for (row = 0; row < 9; ++row) {
            this.addSlot(new Slot(this.playerInventory, row, 8 + row * 18, 179 + e));
        }
    }

    /**
     * @param player 玩家
     * @param pIndex 快速移动到的slot的id
     * @return {@link net.minecraft.world.item.ItemStack}如果拒绝移动，否则返回快速移动的物品
     */
    @Override
    public ItemStack quickMoveStack(Player player, int pIndex) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(pIndex);
        if (slot.hasItem()) {
            ItemStack slotItem = slot.getItem();
            itemStack = slotItem.copy();
            if (pIndex < this.containerRows * this.containerColumns) {
                if (!this.moveItemStackTo(slotItem, this.containerRows * this.containerColumns, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(slotItem, 0, this.containerRows * this.containerColumns, false)) {
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

    @Override
    public boolean clickMenuButton(Player pPlayer, int pId) {
        return super.clickMenuButton(pPlayer, pId);
    }
}
