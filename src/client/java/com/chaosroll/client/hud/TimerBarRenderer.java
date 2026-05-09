package com.chaosroll.client.hud;

import com.chaosroll.config.ConfigManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public final class TimerBarRenderer {

    private static final int BAR_WIDTH = 180;
    private static final int BAR_HEIGHT = 8;
    private static final int BAR_TOP_Y = 8;

    private TimerBarRenderer() {}

    public static void render(GuiGraphics ctx) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui) return;

        int screenW = ctx.guiWidth();
        int x = (screenW - BAR_WIDTH) / 2;
        int y = BAR_TOP_Y;

        int seconds = TimerState.getSecondsRemaining();
        boolean ready = TimerState.isRollReady();

        int interval = Math.max(1, ConfigManager.get().rollIntervalSeconds);
        float progress = ready ? 1.0f
                : Math.min(1.0f, Math.max(0.0f, (interval - seconds) / (float) interval));
        int filled = (int) (BAR_WIDTH * progress);

        ctx.fill(x - 1, y - 1, x + BAR_WIDTH + 1, y + BAR_HEIGHT + 1, 0xFF000000);
        ctx.fill(x, y, x + BAR_WIDTH, y + BAR_HEIGHT, 0xFF222222);

        int color;
        if (ready) {
            float pulse = (float) (0.6f + 0.4f * Math.sin(System.currentTimeMillis() / 200.0));
            int g = (int) (255 * pulse);
            color = 0xFF000000 | (g << 8);
        } else {
            color = colorForSeconds(seconds);
        }
        ctx.fill(x, y, x + filled, y + BAR_HEIGHT, color);

        String label = ready ? "ROLL READY!" : (seconds + "s");
        int textW = mc.font.width(label);
        ctx.drawString(mc.font, Component.literal(label),
                x + BAR_WIDTH / 2 - textW / 2,
                y + BAR_HEIGHT + 2,
                0xFFFFFFFF, true);
    }

    private static int colorForSeconds(int seconds) {
        if (seconds >= 30) {
            return 0xFF3399FF;
        } else if (seconds >= 10) {
            float t = (30 - seconds) / 20.0f;
            return interpolate(0xFF3399FF, 0xFFFFCC00, t);
        } else {
            float t = (10 - seconds) / 10.0f;
            return interpolate(0xFFFFCC00, 0xFFFF3333, t);
        }
    }

    private static int interpolate(int colorA, int colorB, float t) {
        int rA = (colorA >> 16) & 0xFF;
        int gA = (colorA >> 8) & 0xFF;
        int bA = colorA & 0xFF;
        int rB = (colorB >> 16) & 0xFF;
        int gB = (colorB >> 8) & 0xFF;
        int bB = colorB & 0xFF;
        int r = (int) (rA + (rB - rA) * t);
        int g = (int) (gA + (gB - gA) * t);
        int b = (int) (bA + (bB - bA) * t);
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }
}