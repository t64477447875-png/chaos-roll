package com.chaosroll.client.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public final class RollButtonRenderer {

    private RollButtonRenderer() {}

    public static void render(GuiGraphics ctx) {
        if (!TimerState.isRollReady()) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui) return;
        if (mc.player == null) return;

        int screenW = ctx.guiWidth();
        int screenH = ctx.guiHeight();

        float pulse = (float) (1.0f + 0.08f * Math.sin(System.currentTimeMillis() / 250.0));
        int width = (int) (160 * pulse);
        int height = (int) (44 * pulse);
        int x = (screenW - width) / 2;
        int y = (screenH - height) / 2 - 30;

        int glowAlpha = (int) (90 + 60 * Math.abs(Math.sin(System.currentTimeMillis() / 250.0)));
        int glowColor = (glowAlpha << 24) | 0xFFD700;
        ctx.fill(x - 6, y - 6, x + width + 6, y + height + 6, glowColor);

        ctx.fill(x, y, x + width, y + height, 0xFFFFE066);
        ctx.fill(x + 2, y + 2, x + width - 2, y + height - 2, 0xFFFFD700);

        String label = "\u2726 ROLL \u2726  [R]";
        int textW = mc.font.width(label);
        int textH = mc.font.lineHeight;
        ctx.drawString(mc.font, Component.literal(label),
                x + (width - textW) / 2,
                y + (height - textH) / 2,
                0xFF000000, false);
    }
}