package com.chaosroll.client.gui;

import com.chaosroll.config.ChaosRollConfig;
import com.chaosroll.config.ConfigManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class ChaosRollConfigScreen extends Screen {

    private final Screen parent;
    private final ChaosRollConfig working;

    public ChaosRollConfigScreen(Screen parent) {
        super(Component.literal("Chaos Roll — налаштування"));
        this.parent = parent;
        this.working = ConfigManager.get().copy();
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        int leftCol = cx - 155;
        int rightCol = cx + 5;
        int rowH = 24;
        int top = 36;

        addRenderableWidget(new IntSlider(leftCol, top, 150, 20,
                "Інтервал ролу", 5, 300, working.rollIntervalSeconds,
                v -> working.rollIntervalSeconds = v));

        addRenderableWidget(new IntSlider(rightCol, top, 150, 20,
                "Позитивна вага", 0, 100, working.positiveWeight,
                v -> working.positiveWeight = v));

        addRenderableWidget(new IntSlider(leftCol, top + rowH, 150, 20,
                "Негативна вага", 0, 100, working.negativeWeight,
                v -> working.negativeWeight = v));

        addRenderableWidget(new IntSlider(rightCol, top + rowH, 150, 20,
                "Хаотична вага", 0, 100, working.chaoticWeight,
                v -> working.chaoticWeight = v));

        addRenderableWidget(new IntSlider(leftCol, top + rowH * 2, 150, 20,
                "Гарантія + після - стріку", 0, 10, working.guaranteePositiveAfterNegativeStreak,
                v -> working.guaranteePositiveAfterNegativeStreak = v));

        addRenderableWidget(new IntSlider(rightCol, top + rowH * 2, 150, 20,
                "Гарантія не-+ після + стріку", 0, 10, working.guaranteeNonPositiveAfterPositiveStreak,
                v -> working.guaranteeNonPositiveAfterPositiveStreak = v));

        addRenderableWidget(toggle(leftCol, top + rowH * 3, 150, 20,
                "Позитивні події", working.enablePositiveEvents,
                v -> working.enablePositiveEvents = v));
        addRenderableWidget(toggle(rightCol, top + rowH * 3, 150, 20,
                "Негативні події", working.enableNegativeEvents,
                v -> working.enableNegativeEvents = v));
        addRenderableWidget(toggle(leftCol, top + rowH * 4, 150, 20,
                "Хаотичні події", working.enableChaoticEvents,
                v -> working.enableChaoticEvents = v));
        addRenderableWidget(toggle(rightCol, top + rowH * 4, 150, 20,
                "Глобальні події", working.globalEventsEnabled,
                v -> working.globalEventsEnabled = v));

        addRenderableWidget(toggle(leftCol, top + rowH * 5, 150, 20,
                "Складність по вимірам", working.enableDifficultyScaling,
                v -> working.enableDifficultyScaling = v));
        addRenderableWidget(toggle(rightCol, top + rowH * 5, 150, 20,
                "Анімація рулетки", working.enableRollAnimation,
                v -> working.enableRollAnimation = v));

        addRenderableWidget(toggle(leftCol, top + rowH * 6, 150, 20,
                "Звуки", working.enableSounds,
                v -> working.enableSounds = v));
        addRenderableWidget(toggle(rightCol, top + rowH * 6, 150, 20,
                "Частинки", working.enableParticles,
                v -> working.enableParticles = v));

        addRenderableWidget(toggle(leftCol, top + rowH * 7, 150, 20,
                "Шкала таймера", working.showTimerBar,
                v -> working.showTimerBar = v));
        addRenderableWidget(toggle(rightCol, top + rowH * 7, 150, 20,
                "Панель ефектів", working.showActiveEffectsPanel,
                v -> working.showActiveEffectsPanel = v));

        addRenderableWidget(toggle(leftCol, top + rowH * 8, 150, 20,
                "Авто-ролл", working.autoRoll,
                v -> working.autoRoll = v));

        int btnY = this.height - 30;
        addRenderableWidget(Button.builder(Component.literal("Скинути"), b -> {
            ChaosRollConfig defaults = new ChaosRollConfig();
            applyToConfig(defaults);
            ConfigManager.save();
            this.minecraft.setScreen(new ChaosRollConfigScreen(parent));
        }).bounds(cx - 200, btnY, 100, 20).build());

        addRenderableWidget(Button.builder(Component.literal("Зберегти"), b -> {
            applyToConfig(working);
            ConfigManager.save();
            sendUpdateToServer(working);
            this.minecraft.setScreen(parent);
        }).bounds(cx - 50, btnY, 100, 20).build());

        addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL,
                b -> this.minecraft.setScreen(parent))
                .bounds(cx + 100, btnY, 100, 20).build());
    }

    private static void applyToConfig(ChaosRollConfig src) {
        ChaosRollConfig live = ConfigManager.get();
        live.rollIntervalSeconds = src.rollIntervalSeconds;
        live.positiveWeight = src.positiveWeight;
        live.negativeWeight = src.negativeWeight;
        live.chaoticWeight = src.chaoticWeight;
        live.enablePositiveEvents = src.enablePositiveEvents;
        live.enableNegativeEvents = src.enableNegativeEvents;
        live.enableChaoticEvents = src.enableChaoticEvents;
        live.globalEventsEnabled = src.globalEventsEnabled;
        live.enableDifficultyScaling = src.enableDifficultyScaling;
        live.enableRollAnimation = src.enableRollAnimation;
        live.enableSounds = src.enableSounds;
        live.enableParticles = src.enableParticles;
        live.showTimerBar = src.showTimerBar;
        live.showActiveEffectsPanel = src.showActiveEffectsPanel;
        live.guaranteePositiveAfterNegativeStreak = src.guaranteePositiveAfterNegativeStreak;
        live.guaranteeNonPositiveAfterPositiveStreak = src.guaranteeNonPositiveAfterPositiveStreak;
        live.autoRoll = src.autoRoll;
        live.validate();
    }

    private static void sendUpdateToServer(ChaosRollConfig cfg) {
        try {
            String json = new com.google.gson.Gson().toJson(cfg);
            net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
                    .send(new com.chaosroll.network.ConfigUpdatePacket(json));
        } catch (Exception ignored) {}
    }

    private static CycleButton<Boolean> toggle(int x, int y, int w, int h,
                                                String label, boolean initial,
                                                java.util.function.Consumer<Boolean> setter) {
        return CycleButton.onOffBuilder(initial)
                .create(x, y, w, h, Component.literal(label),
                        (btn, val) -> setter.accept(val));
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
        super.render(g, mouseX, mouseY, delta);
        g.drawCenteredString(this.font, this.title, this.width / 2, 12, 0xFFFFFFFF);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private static final class IntSlider extends AbstractSliderButton {
        private final int min;
        private final int max;
        private final String label;
        private final java.util.function.IntConsumer setter;

        IntSlider(int x, int y, int w, int h, String label, int min, int max, int initial,
                  java.util.function.IntConsumer setter) {
            super(x, y, w, h, Component.literal(label + ": " + initial),
                    max == min ? 0.0 : (initial - min) / (double) (max - min));
            this.label = label;
            this.min = min;
            this.max = max;
            this.setter = setter;
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            int v = (int) Math.round(min + value * (max - min));
            setMessage(Component.literal(label + ": " + v));
        }

        @Override
        protected void applyValue() {
            int v = (int) Math.round(min + value * (max - min));
            setter.accept(v);
        }
    }
}
