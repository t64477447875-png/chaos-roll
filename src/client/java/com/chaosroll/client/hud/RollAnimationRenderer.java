package com.chaosroll.client.hud;

import com.chaosroll.event.EventRarity;
import com.chaosroll.event.EventType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public final class RollAnimationRenderer {

    private static final int CARD_WIDTH = 360;
    private static final int CARD_HEIGHT = 90;
    private static final int CARD_BORDER = 3;

    private static final int OVERLAY_COLOR = 0x000000;
    private static final float OVERLAY_MAX_ALPHA = 0.72f;

    private RollAnimationRenderer() {}

    public static void render(GuiGraphics ctx) {
        if (!RollAnimationState.isActive()) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui) return;

        int screenW = ctx.guiWidth();
        int screenH = ctx.guiHeight();

        float overlayAlpha = computeOverlayAlpha();
        if (overlayAlpha > 0.001f) {
            int alphaByte = (int) (overlayAlpha * 255) & 0xFF;
            int color = (alphaByte << 24) | OVERLAY_COLOR;
            ctx.fill(0, 0, screenW, screenH, color);
        }

        if (RollAnimationState.getResultType() == EventType.NEGATIVE
                && (RollAnimationState.getPhase() == RollAnimationState.Phase.RESULT
                || RollAnimationState.getPhase() == RollAnimationState.Phase.FADE_OUT)) {
            drawNegativeVignette(ctx, screenW, screenH, overlayAlpha);
        }

        switch (RollAnimationState.getPhase()) {
            case FADE_IN, SPINNING, SLOWING -> drawSlotCard(ctx, mc, screenW, screenH);
            case RESULT -> {
                drawSlotCard(ctx, mc, screenW, screenH);
                drawResultBanner(ctx, mc, screenW, screenH);
                drawSparkles(ctx, screenW, screenH);
            }
            case FADE_OUT -> {
                drawSlotCard(ctx, mc, screenW, screenH);
                drawResultBanner(ctx, mc, screenW, screenH);
            }
            default -> {}
        }
    }

    private static float computeOverlayAlpha() {
        RollAnimationState.Phase phase = RollAnimationState.getPhase();
        int ticks = RollAnimationState.getPhaseTicks();
        return switch (phase) {
            case FADE_IN -> OVERLAY_MAX_ALPHA * Math.min(1.0f, ticks / (float) RollAnimationState.FADE_IN_TICKS);
            case SPINNING, SLOWING, RESULT -> OVERLAY_MAX_ALPHA;
            case FADE_OUT -> OVERLAY_MAX_ALPHA
                    * Math.max(0.0f, 1.0f - (ticks / (float) RollAnimationState.FADE_OUT_TICKS));
            default -> 0.0f;
        };
    }

    private static void drawNegativeVignette(GuiGraphics ctx, int w, int h, float alpha) {
        int strength = (int) (alpha * 200);
        int color = ((strength & 0xFF) << 24) | 0x550000;
        int margin = 80;
        ctx.fill(0, 0, w, margin, color);
        ctx.fill(0, h - margin, w, h, color);
        ctx.fill(0, margin, margin, h - margin, color);
        ctx.fill(w - margin, margin, w, h - margin, color);
    }

    private static void drawSlotCard(GuiGraphics ctx, Minecraft mc, int screenW, int screenH) {
        int x = (screenW - CARD_WIDTH) / 2;
        int y = (screenH - CARD_HEIGHT) / 2 - 20;

        int glow = pickAccentColor(0x80);
        ctx.fill(x - 8, y - 8, x + CARD_WIDTH + 8, y + CARD_HEIGHT + 8, glow);

        ctx.fill(x - CARD_BORDER, y - CARD_BORDER,
                x + CARD_WIDTH + CARD_BORDER, y + CARD_HEIGHT + CARD_BORDER,
                0xFF1A1A1A);

        ctx.fill(x, y, x + CARD_WIDTH, y + CARD_HEIGHT, 0xFF111111);

        int top = y + 4;
        int bot = y + CARD_HEIGHT - 4;
        ctx.fill(x + 4, top, x + CARD_WIDTH - 4, top + 1, 0x40FFFFFF);
        ctx.fill(x + 4, bot - 1, x + CARD_WIDTH - 4, bot, 0x40FFFFFF);

        Font font = mc.font;
        String name = RollAnimationState.getCurrentSpinName();
        if (name == null || name.isEmpty()) name = "?";

        int color = colorForCurrentName();
        int textW = font.width(name);
        int maxTextW = CARD_WIDTH - 24;
        float scale = textW > maxTextW ? maxTextW / (float) textW : 1.0f;

        if (scale < 1.0f) {
            ctx.pose().pushPose();
            int cx = x + CARD_WIDTH / 2;
            int cy = y + CARD_HEIGHT / 2;
            ctx.pose().translate((float) cx, (float) cy, 0.0f);
            ctx.pose().scale(scale, scale, 1.0f);
            ctx.pose().translate((float) -cx, (float) -cy, 0.0f);
            ctx.drawString(font, Component.literal(name),
                    cx - textW / 2,
                    cy - font.lineHeight / 2,
                    color, true);
            ctx.pose().popPose();
        } else {
            ctx.drawString(font, Component.literal(name),
                    x + (CARD_WIDTH - textW) / 2,
                    y + (CARD_HEIGHT - font.lineHeight) / 2,
                    color, true);
        }

        String header = "\u2726 CHAOS ROLL \u2726";
        int headerW = font.width(header);
        ctx.drawString(font, Component.literal(header),
                x + (CARD_WIDTH - headerW) / 2,
                y - 14,
                0xFFFFFFFF, true);
    }

    private static int colorForCurrentName() {
        RollAnimationState.Phase phase = RollAnimationState.getPhase();
        if (phase == RollAnimationState.Phase.RESULT || phase == RollAnimationState.Phase.FADE_OUT) {
            return 0xFF000000 | accentRgb();
        }
        long now = System.currentTimeMillis();
        int r = 200 + (int) (40 * Math.sin(now / 90.0));
        int g = 200 + (int) (40 * Math.sin(now / 90.0 + 2.0));
        int b = 200 + (int) (40 * Math.sin(now / 90.0 + 4.0));
        return 0xFF000000 | (clamp(r) << 16) | (clamp(g) << 8) | clamp(b);
    }

    private static int clamp(int v) {
        return Math.max(0, Math.min(255, v));
    }

    private static int accentRgb() {
        return switch (RollAnimationState.getResultType()) {
            case POSITIVE -> 0x55FF77;
            case NEGATIVE -> 0xFF4444;
            case CHAOTIC -> chaoticRainbow();
        };
    }

    private static int chaoticRainbow() {
        long now = System.currentTimeMillis();
        float t = (float) ((now / 12) % 360) / 60.0f;
        int phase = (int) t;
        float frac = t - phase;
        int r, g, b;
        switch (phase) {
            case 0 -> { r = 255; g = (int) (frac * 255); b = 0; }
            case 1 -> { r = (int) ((1 - frac) * 255); g = 255; b = 0; }
            case 2 -> { r = 0; g = 255; b = (int) (frac * 255); }
            case 3 -> { r = 0; g = (int) ((1 - frac) * 255); b = 255; }
            case 4 -> { r = (int) (frac * 255); g = 0; b = 255; }
            default -> { r = 255; g = 0; b = (int) ((1 - frac) * 255); }
        }
        return (clamp(r) << 16) | (clamp(g) << 8) | clamp(b);
    }

    private static int pickAccentColor(int alpha) {
        RollAnimationState.Phase phase = RollAnimationState.getPhase();
        int rgb;
        if (phase == RollAnimationState.Phase.RESULT || phase == RollAnimationState.Phase.FADE_OUT) {
            rgb = accentRgb();
            float pulse = 0.5f + 0.5f * (float) Math.sin(System.currentTimeMillis() / 120.0);
            int a = (int) (alpha * (0.6f + 0.4f * pulse)) & 0xFF;
            return (a << 24) | rgb;
        } else {
            rgb = 0x6688FF;
            return ((alpha & 0xFF) << 24) | rgb;
        }
    }

    private static void drawResultBanner(GuiGraphics ctx, Minecraft mc, int screenW, int screenH) {
        Font font = mc.font;
        String label = switch (RollAnimationState.getResultType()) {
            case POSITIVE -> "+ ПОЗИТИВНА ПОДIЯ";
            case NEGATIVE -> "\u2716 НЕГАТИВНА ПОДIЯ";
            case CHAOTIC -> "\u2737 ХАОТИЧНА ПОДIЯ";
        };
        if (RollAnimationState.getResultRarity() == EventRarity.LEGENDARY) {
            label += "  \u2726 ЛЕГЕНДАРНА \u2726";
        } else if (RollAnimationState.getResultRarity() == EventRarity.RARE) {
            label += "  [РIДКIСНА]";
        }

        int color = 0xFF000000 | accentRgb();
        int labelW = font.width(label);
        int labelY = (screenH / 2) + 50;

        ctx.fill((screenW - labelW) / 2 - 8, labelY - 4,
                (screenW + labelW) / 2 + 8, labelY + font.lineHeight + 4,
                0xC0000000);

        ctx.drawString(font, Component.literal(label),
                (screenW - labelW) / 2, labelY,
                color, true);

        String name = RollAnimationState.getResultDisplayName();
        if (name != null && !name.isEmpty()) {
            int titleY = labelY + font.lineHeight + 10;
            int nameW = font.width(name);
            ctx.pose().pushPose();
            int cx = screenW / 2;
            int cy = titleY + font.lineHeight / 2;
            ctx.pose().translate((float) cx, (float) cy, 0.0f);
            ctx.pose().scale(1.5f, 1.5f, 1.0f);
            ctx.pose().translate((float) -cx, (float) -cy, 0.0f);
            ctx.drawString(font, Component.literal(name),
                    cx - nameW / 2,
                    titleY,
                    color, true);
            ctx.pose().popPose();
        }
    }

    private static void drawSparkles(GuiGraphics ctx, int screenW, int screenH) {
        long now = System.currentTimeMillis();
        int count = switch (RollAnimationState.getResultType()) {
            case POSITIVE -> 24;
            case NEGATIVE -> 12;
            case CHAOTIC -> 30;
        };
        int color = 0xFF000000 | accentRgb();
        for (int i = 0; i < count; i++) {
            double angle = (now / 18.0 + i * 360.0 / count) % 360.0;
            double rad = Math.toRadians(angle);
            int radius = 140 + (i * 11) % 70;
            int cx = screenW / 2 + (int) (Math.cos(rad) * radius);
            int cy = screenH / 2 + (int) (Math.sin(rad) * radius * 0.6) - 10;
            int size = 2 + (int) (2 * Math.abs(Math.sin(now / 200.0 + i)));
            ctx.fill(cx - size, cy - size, cx + size, cy + size, color);
        }
    }
}
