package com.chaosroll.client.hud;

import com.chaosroll.network.ActiveEffectsPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.List;

public final class ActiveEffectsRenderer {

    private static final int ROW_HEIGHT = 12;
    private static final int LEFT_MARGIN = 4;
    private static final int TOP_MARGIN = 32;
    private static final int MAX_ROWS = 6;

    private ActiveEffectsRenderer() {}

    public static void render(GuiGraphics ctx) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui) return;
        if (RollAnimationState.isActive()) return;

        List<ActiveEffectsPacket.Entry> entries = ActiveEffectsState.getEntries();
        if (entries.isEmpty()) return;

        int rows = Math.min(entries.size(), MAX_ROWS);
        int boxW = 0;
        for (int i = 0; i < rows; i++) {
            ActiveEffectsPacket.Entry e = entries.get(i);
            String text = formatRow(e);
            int w = mc.font.width(text);
            if (w > boxW) boxW = w;
        }
        boxW += 8;
        int boxH = rows * ROW_HEIGHT + 4;

        int x = LEFT_MARGIN;
        int y = TOP_MARGIN;

        ctx.fill(x, y, x + boxW, y + boxH, 0x80000000);
        ctx.fill(x, y, x + boxW, y + 1, 0xFFFFFFFF);

        for (int i = 0; i < rows; i++) {
            ActiveEffectsPacket.Entry e = entries.get(i);
            String text = formatRow(e);
            int color = typeColor(e.typeOrdinal());
            ctx.drawString(mc.font, Component.literal(text),
                    x + 4, y + 3 + i * ROW_HEIGHT,
                    color, false);
        }
    }

    private static String formatRow(ActiveEffectsPacket.Entry e) {
        return e.displayName() + " — " + e.secondsRemaining() + "с";
    }

    private static int typeColor(int typeOrdinal) {
        return switch (typeOrdinal) {
            case 0 -> 0xFF55FF55;
            case 1 -> 0xFFFF5555;
            case 2 -> 0xFFFFD700;
            default -> 0xFFFFFFFF;
        };
    }
}
