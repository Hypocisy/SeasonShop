package com.kumoe.SeasonShop.content.shipping;

import com.kumoe.SeasonShop.init.SeasonShop;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class SeasonSlot extends Slot {
    public SeasonSlot(Container pContainer, int pSlot, int pX, int pY) {
        super(pContainer, pSlot, pX, pY);
    }

    @Override
    public void onTake(Player pPlayer, ItemStack pStack) {

        SeasonShop.LOGGER.debug("test");
        super.onTake(pPlayer, pStack);
    }

    @Override
    public boolean mayPlace(ItemStack pStack) {
        return pStack.is(ItemTags.VILLAGER_PLANTABLE_SEEDS);
    }
}
