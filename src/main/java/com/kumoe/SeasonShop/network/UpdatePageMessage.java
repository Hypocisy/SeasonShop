package com.kumoe.SeasonShop.network;

import com.kumoe.SeasonShop.api.ModUtils;
import com.kumoe.SeasonShop.content.block.entity.BuyBackBlockEntity;
import com.kumoe.SeasonShop.content.block.entity.ShopBlockEntity;
import com.kumoe.SeasonShop.content.menu.BuybackMenu;
import com.kumoe.SeasonShop.content.menu.ShopMenu;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdatePageMessage {
    private final int newPage;

    public UpdatePageMessage(int newPage) {
        this.newPage = newPage;
    }

    public static void encode(UpdatePageMessage message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.newPage);
    }

    public static UpdatePageMessage decode(FriendlyByteBuf buffer) {
        return new UpdatePageMessage(buffer.readInt());
    }

    public static void handle(UpdatePageMessage message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // 获取服务器端的容器并更新 currentPage
            Player player = ctx.get().getSender();
            if (player != null && player.containerMenu instanceof ShopMenu menu) {
                ShopBlockEntity container = menu.getContainer();
                container.setCurrentPage(message.newPage);
                NonNullList<ItemStack> byPage = ModUtils.getItemsByPage(container.getCurrentPage());
                container.setItems(byPage);
                container.setChanged();
            }
            if (player != null && player.containerMenu instanceof BuybackMenu menu) {
                BuyBackBlockEntity container = menu.getContainer();
                container.setCurrentPage(message.newPage);
                NonNullList<ItemStack> byPage = ModUtils.getItemsByPage(container.getCurrentPage());
                container.setItems(byPage);
                container.setChanged();
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
