package com.kumoe.SeasonShop.network;

import com.kumoe.SeasonShop.api.ModUtils;
import com.kumoe.SeasonShop.content.block.entity.BuyBackBlockEntity;
import com.kumoe.SeasonShop.content.menu.BuybackMenu;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import sereneseasons.api.season.Season;

import java.util.function.Supplier;

public class UpdatePageSubSeasonMessage {
    private final int newPage;
    private final Season.SubSeason subSeason;

    public UpdatePageSubSeasonMessage(int newPage, Season.SubSeason subSeason) {
        this.newPage = newPage;
        this.subSeason = subSeason;
    }

    public static void encode(UpdatePageSubSeasonMessage message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.newPage);
        buffer.writeEnum(message.subSeason);
    }

    public static UpdatePageSubSeasonMessage decode(FriendlyByteBuf buffer) {
        return new UpdatePageSubSeasonMessage(buffer.readInt(), buffer.readEnum(Season.SubSeason.class));
    }

    public static void handle(UpdatePageSubSeasonMessage message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // 获取服务器端的容器并更新 currentPage
            Player player = ctx.get().getSender();
            if (player != null && player.containerMenu instanceof BuybackMenu menu) {
                BuyBackBlockEntity container = menu.getContainer();
                container.setCurrentPage(message.newPage);
                NonNullList<ItemStack> byPage = ModUtils.getItemsByPageAndSeason(message.subSeason, container.getCurrentPage());
                container.setItems(byPage);
                container.setChanged();
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
