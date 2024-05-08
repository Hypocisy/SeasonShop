package com.kumoe.SeasonShop.content.shipping;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ShippingBinMenu extends AbstractContainerMenu {
    private final AbstractShippingBinBlockEntity container;
    protected final Inventory playerInventory;
    protected int containerRows = 3;

    public ShippingBinMenu(MenuType<ShippingBinMenu> menu, int windowId, Inventory pPlayerInventory, AbstractShippingBinBlockEntity pContainer) {
        super(menu, windowId);
        this.container = pContainer;
        this.playerInventory = pPlayerInventory;
        addSlot();
    }

    public ShippingBinMenu(MenuType<ShippingBinMenu> menu, int windowId, Inventory pPlayerInventory, @Nullable FriendlyByteBuf data) {
        this(menu, windowId, pPlayerInventory, getTileEntity(pPlayerInventory, data));
    }

    static AbstractShippingBinBlockEntity getTileEntity(Inventory playerInventory, @Nullable FriendlyByteBuf data) {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null");
        Objects.requireNonNull(data, "data cannot be null");
        BlockEntity tileAtPos = playerInventory.player.level().getBlockEntity(data.readBlockPos());
        if (tileAtPos instanceof AbstractShippingBinBlockEntity bin) {
            return bin;
        } else {
            throw new IllegalStateException("Tile entity is not correct! " + tileAtPos);
        }
    }

    void addSlot(){
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

//    private void addShopSlot(){
//        int e = (this.containerRows - 4) * 18;
//        int row;
//        int slot;
//        for (row = 0; row < this.containerRows; ++row) {
//            for (slot = 0; slot < 9; ++slot) {
//                this.addSlot(new SeasonSlot(this.container, slot + row * 6, 8 + slot * 18, 18 + row * 18));
//            }
//        }
//    }

    @Override
    public ItemStack quickMoveStack(Player player, int pIndex) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(pIndex);
        if (slot.hasItem() && slot.getItem().is(ItemTags.VILLAGER_PLANTABLE_SEEDS)) {
            ItemStack slotItem = slot.getItem();
            itemStack = slotItem.copy();
            if (pIndex < this.containerRows * 9) {
                if (!this.moveItemStackTo(slotItem, this.containerRows * 6, this.slots.size(), true)) {
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
     * @param player
     * @return is still Valid
     */
    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    public AbstractShippingBinBlockEntity getContainer() {
        return this.container;
    }
}
