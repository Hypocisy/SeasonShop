package com.kumoe.SeasonShop.content.screen;


import com.kumoe.SeasonShop.api.ModUtils;
import com.kumoe.SeasonShop.content.block.entity.ShippingBinBlockEntity;
import com.kumoe.SeasonShop.content.menu.ShippingBinMenu;
import com.kumoe.SeasonShop.data.SSLangData;
import com.kumoe.SeasonShop.init.SeasonShop;
import com.kumoe.SeasonShop.network.C2SBinPricesPacket;
import com.kumoe.SeasonShop.network.NetworkHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.File;

@OnlyIn(Dist.CLIENT)
public class ShippingBinScreen extends AbstractContainerScreen<ShippingBinMenu> {
    static final ResourceLocation SHIPPING_BIN_GUI = new ResourceLocation(SeasonShop.MODID, "textures/gui/shipping_bin.png");
    protected final ShippingBinMenu menu;
    protected final Container container;
    protected final Player player;
    final Inventory playerInventory;

    public ShippingBinScreen(ShippingBinMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageWidth = 187;
        this.imageHeight = 188;
        this.playerInventory = pPlayerInventory;
        this.player = pPlayerInventory.player;
        this.menu = pMenu;
        this.container = pMenu.getContainer();
    }

    @Override
    protected void init() {
        super.init();
        ImageButton button = new ImageButton((this.width - this.imageWidth - 10) / 2 + 143, (this.height - this.imageHeight - 2) / 2 + 50, 16, 16, this.imageWidth, 0, 16, SHIPPING_BIN_GUI, this::onPress);
        button.setTooltip(Tooltip.create(ModUtils.getLangComponent(SSLangData.SHIPPING_BIN_TOOLTIP_4)));
        this.addRenderableWidget(button);
    }

    private void onPress(Button button) {
        if (container instanceof ShippingBinBlockEntity shippingBinBlockEntity) {
            // send sell request
            var packet = C2SBinPricesPacket.create(shippingBinBlockEntity.getOwner(), shippingBinBlockEntity.getBlockPos());
            NetworkHandler.sendToServer(packet);
        } else {
            KanBanGirlOverlay.setMessage(ModUtils.getLangComponent(SSLangData.SHIPPING_BIN_TOOLTIP_3));
        }
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pGuiGraphics);
        this.inventoryLabelY = this.imageHeight - 98;
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(pGuiGraphics, pMouseX, pMouseY);

    }

    @Override
    protected void renderTooltip(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        super.renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        pGuiGraphics.drawString(this.font, this.title, this.titleLabelX + this.imageWidth - 60, this.titleLabelY, 0x404040, true);
        pGuiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0x404040, true);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        int x = (this.width - this.imageWidth - 10) / 2;
        int y = (this.height - this.imageHeight - 2) / 2;
        guiGraphics.blit(SHIPPING_BIN_GUI, x, y, 0, 0, this.imageWidth, this.imageHeight);
        if (container instanceof ShippingBinBlockEntity shippingBinBlockEntity) {
            File avatarFile = ModUtils.getAvatarFile(shippingBinBlockEntity.getOwner());
            if (avatarFile.exists()) {
                // render player avatar
                ResourceLocation avatarLocation = ModUtils.loadPlayerAvatar(avatarFile, shippingBinBlockEntity.getOwner());
                if (avatarLocation != null) {
                    guiGraphics.blit(avatarLocation, x + 143, y + 27, 0, 0, 16, 16, 16, 16);
                }
            }
        }
    }
}
