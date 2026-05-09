package com.chaosroll.client.hud;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public final class GlobalBannerRenderer {

    private GlobalBannerRenderer() {}

    public static void render(GuiGraphics ctx) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui || mc.player == null) return;
        if (!GlobalBannerState.isVisible()) return;
        if (RollAnimationState.isActive()) return;

        int ticksLeft = GlobalBannerState.getTicksLeft();
        float fadeIn = Math.min(1.0f, (GlobalBannerState.LIFETIME_TICKS - ticksLeft) / 8.0f);
        float fadeOut = Math.min(1.0f, ticksLeft / 12.0f);
        float alpha = Math.min(fadeIn, fadeOut);
        if (alpha <= 0.01f) return;

        int alphaByte = (int) (alpha * 255) & 0xFF;
        int textColor = (alphaByte << 24) | typeColor(GlobalBannerState.getTypeOrdinal());
        int subColor = (alphaByte << 24) | 0xCCCCCC;
        int bgColor = ((int) (alpha * 180) << 24);

        String initiator = GlobalBannerState.getInitiatorName();
        String name = GlobalBannerState.getDisplayName();
        MutableComponent line = Component.literal("✦ " + name + " ✦")
                .withStyle(ChatFormatting.BOLD);
        Component subline = Component.literal(initiator + " крутить рулетку для всіх");

        int screenW = ctx.guiWidth();
        int lineW = mc.font.width(line);
        int subW = mc.font.width(subline);
        int boxW = Math.max(lineW, subW) + 24;
        int boxH = 28;
        int x = (screenW - boxW) / 2;
        int y = 36;

        ctx.fill(x, y, x + boxW, y + boxH, bgColor);
        ctx.fill(x, y, x + boxW, y + 1, textColor);
        ctx.fill(x, y + boxH - 1, x + boxW, y + boxH, textColor);

        ctx.drawString(mc.font, line,
                x + (boxW - lineW) / 2, y + 5,
                textColor, false);
        ctx.drawString(mc.font, subline,
                x + (boxW - subW) / 2, y + 17,
                subColor, false);
    }

    private static int typeColor(int typeOrdinal) {
        return switch (typeOrdinal) {
            case 0 -> 0x55FF55; // POSITIVE
            case 1 -> 0xFF5555; // NEGATIVE
            case 2 -> 0xFFD700; // CHAOTIC
            default -> 0xFFFFFF;
        };
    }
}
