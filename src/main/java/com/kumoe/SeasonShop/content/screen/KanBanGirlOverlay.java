package com.kumoe.SeasonShop.content.screen;

import com.kumoe.SeasonShop.init.SeasonShop;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class KanBanGirlOverlay implements IGuiOverlay {

    public static final ResourceLocation girl = new ResourceLocation(SeasonShop.MODID, "textures/gui/girl.png");
    public static int startX = 0;
    private static boolean isRendering = false;
    private static Component message;

    public static void switchRendering() {
        isRendering = !isRendering;
    }

    public static void setMessage(Component msg) {
        message = msg;
    }

    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int width,
                       int height) {
        // render girl
        if (isRendering) {
            guiGraphics.blit(girl, startX, height - 128, 0, 0, 128, 128, 128, 128);
            guiGraphics.drawString(gui.getFont(), message, 50, height - 128 + 5, message.getStyle().getColor().getValue());
            if (gui.getGuiTicks() % 120 == 0) {
                switchRendering();
            }
        }
    }
}
