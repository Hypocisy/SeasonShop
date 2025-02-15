package com.kumoe.SeasonShop.network;

import com.kumoe.SeasonShop.api.ModUtils;
import com.kumoe.SeasonShop.content.screen.KanBanGirlOverlay;
import com.kumoe.SeasonShop.data.SSLangData;
import com.kumoe.SeasonShop.data.config.SeasonShopConfig;
import com.kumoe.SeasonShop.init.SeasonShop;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Style;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record S2CTotalValue(double totalValue) {

    public static S2CTotalValue decode(FriendlyByteBuf buffer) {
        return new S2CTotalValue(buffer.readDouble());
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeDouble(totalValue);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            if (ctx.getDirection().getReceptionSide().isClient()) {
                handleClient();
            }
        });
        ctx.setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private void handleClient() {
        // render gui
        KanBanGirlOverlay.setMessage(ModUtils.getLangComponent(SSLangData.SHIPPING_BIN_TOOLTIP_2, totalValue).withStyle(Style.EMPTY.withBold(true)));
        if (SeasonShopConfig.enableDebug) {
            SeasonShop.logger().debug("total price: {}", totalValue);
        }
        if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.closeContainer();
        }
        KanBanGirlOverlay.switchRendering();
    }
}
