package com.chaosroll.client.gui;

import com.chaosroll.event.BaseEvent;
import com.chaosroll.event.EventRegistry;
import com.chaosroll.event.EventType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Browse all registered events with their descriptions. Click a row to toggle
 * personal blocklist via /chaosroll block <id>.
 */
public class EventListScreen extends Screen {

    private final Screen parent;
    private final List<BaseEvent> events;
    private int scroll;
    private int hoveredIndex = -1;

    public EventListScreen(Screen parent) {
        super(Component.literal("Chaos Roll — Список подій"));
        this.parent = parent;
        this.events = new ArrayList<>(EventRegistry.all());
        this.events.sort((a, b) -> {
            int c = a.getType().compareTo(b.getType());
            if (c != 0) return c;
            return a.getDisplayName().compareToIgnoreCase(b.getDisplayName());
        });
    }

    @Override
    protected void init() {
        addRenderableWidget(Button.builder(Component.literal("Назад"),
                b -> this.minecraft.setScreen(parent))
                .bounds(this.width / 2 - 50, this.height - 28, 100, 20).build());
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double dx, double dy) {
        scroll = Math.max(0, Math.min(events.size() * 16 - (this.height - 80),
                scroll - (int)(dy * 24)));
        return true;
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (super.mouseClicked(mx, my, button)) return true;
        if (button == 0 && hoveredIndex >= 0 && hoveredIndex < events.size()) {
            BaseEvent ev = events.get(hoveredIndex);
            if (this.minecraft != null && this.minecraft.player != null) {
                this.minecraft.player.connection.sendCommand("chaosroll block " + ev.getId());
            }
            return true;
        }
        return false;
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
        super.render(g, mouseX, mouseY, delta);
        g.drawCenteredString(this.font, this.title, this.width / 2, 8, 0xFFFFFFFF);
        g.drawCenteredString(this.font,
                Component.literal("§7Натисни на подію щоб додати/прибрати з блок-листа").getString(),
                this.width / 2, 22, 0xFFAAAAAA);

        int top = 40;
        int bottom = this.height - 40;
        int x = this.width / 2 - 200;
        int rowH = 16;
        hoveredIndex = -1;

        g.enableScissor(0, top, this.width, bottom);
        for (int i = 0; i < events.size(); i++) {
            int y = top + i * rowH - scroll;
            if (y + rowH < top || y > bottom) continue;
            BaseEvent ev = events.get(i);
            int color = switch (ev.getType()) {
                case POSITIVE -> 0xFF55FF55;
                case NEGATIVE -> 0xFFFF5555;
                case CHAOTIC -> 0xFFFFAA00;
            };
            String typeTag = switch (ev.getType()) {
                case POSITIVE -> "+";
                case NEGATIVE -> "-";
                case CHAOTIC -> "?";
            };
            boolean hover = mouseX >= x && mouseX <= x + 400 && mouseY >= y && mouseY <= y + rowH;
            if (hover) {
                hoveredIndex = i;
                g.fill(x, y, x + 400, y + rowH, 0x40FFFFFF);
            }
            g.drawString(this.font, "[" + typeTag + "]", x + 4, y + 4, color);
            g.drawString(this.font, ev.getDisplayName(), x + 24, y + 4, 0xFFFFFFFF);
            g.drawString(this.font, "§8" + ev.getId(), x + 200, y + 4, 0xFF888888);
        }
        g.disableScissor();

        if (hoveredIndex >= 0) {
            BaseEvent hov = events.get(hoveredIndex);
            String desc = hov.getDescription();
            if (desc == null || desc.isEmpty()) {
                desc = "§7Опис відсутній.";
            }
            List<Component> lines = new ArrayList<>();
            lines.add(Component.literal("§e" + hov.getDisplayName()));
            lines.add(Component.literal("§7Тип: " + hov.getType().name() + " §7| Рідкість: " + hov.getRarity().name()));
            if (hov.getDurationTicks() > 0) {
                lines.add(Component.literal("§7Тривалість: " + (hov.getDurationTicks() / 20) + "с"));
            }
            for (String line : desc.split("\n")) {
                lines.add(Component.literal("§f" + line));
            }
            g.renderComponentTooltip(this.font, lines, mouseX, mouseY);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
