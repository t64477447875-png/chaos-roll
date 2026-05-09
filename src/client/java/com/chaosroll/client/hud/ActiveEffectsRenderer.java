package com.chaosroll.client.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public final class ActiveEffectsRenderer {

    private ActiveEffectsRenderer() {}

    public static void render(GuiGraphics ctx) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui) return;
    }
}