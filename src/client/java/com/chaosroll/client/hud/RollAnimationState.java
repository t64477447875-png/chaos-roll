package com.chaosroll.client.hud;

import com.chaosroll.event.EventRarity;
import com.chaosroll.event.EventType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class RollAnimationState {

    public enum Phase {
        IDLE,
        FADE_IN,
        SPINNING,
        SLOWING,
        RESULT,
        FADE_OUT
    }

    public static final int FADE_IN_TICKS = 10;
    public static final int SPINNING_TICKS = 30;
    public static final int SLOWING_TICKS = 25;
    public static final int RESULT_TICKS = 40;
    public static final int FADE_OUT_TICKS = 10;

    private static final List<String> SPIN_POOL = new ArrayList<>(List.of(
            "Випадковий лут", "Алмазна броня", "Елітри!", "Демон швидкості",
            "Тіньовий режим", "Щасливий телепорт", "Вірний вовк", "Золотий бенкет",
            "Тотеми", "Зачаровані інструменти", "Карта скарбів", "Запас перлин",
            "Удар блискавки", "Вибух інвентаря", "Хаотичний телепорт", "Заряджені кріпери",
            "Сліпота", "Голодомор", "Запуск у небо", "Загін візер-скелетів",
            "Загадковий слот", "Інверсія гравітації", "Дощ з ТНТ", "Скляна броня",
            "Часовий стрибок", "Феєрверки", "Вибух кольору", "Дощ предметів",
            "Магніт-режим", "Дзеркало"
    ));

    private static Phase phase = Phase.IDLE;
    private static int phaseTicks = 0;
    private static int spinTicksUntilFlip = 0;
    private static int spinIndex = 0;
    private static String currentSpinName = "?";

    private static String resultEventId = "";
    private static String resultDisplayName = "";
    private static EventType resultType = EventType.CHAOTIC;
    private static EventRarity resultRarity = EventRarity.COMMON;

    private static final RandomSource RNG = RandomSource.create();

    private RollAnimationState() {}

    public static synchronized void start(String eventId,
                                          String displayName,
                                          int typeOrdinal,
                                          int rarityOrdinal) {
        resultEventId = eventId;
        resultDisplayName = displayName;
        resultType = safeType(typeOrdinal);
        resultRarity = safeRarity(rarityOrdinal);

        phase = Phase.FADE_IN;
        phaseTicks = 0;
        spinTicksUntilFlip = 0;
        spinIndex = RNG.nextInt(SPIN_POOL.size());
        currentSpinName = SPIN_POOL.get(spinIndex);

        playClickSound(1.0f);
    }

    public static synchronized void reset() {
        phase = Phase.IDLE;
        phaseTicks = 0;
    }

    public static synchronized void clientTick() {
        if (phase == Phase.IDLE) {
            return;
        }
        phaseTicks++;

        switch (phase) {
            case FADE_IN -> {
                if (phaseTicks >= FADE_IN_TICKS) {
                    advance(Phase.SPINNING);
                }
            }
            case SPINNING -> {
                advanceSpin(2);
                if (phaseTicks >= SPINNING_TICKS) {
                    advance(Phase.SLOWING);
                }
            }
            case SLOWING -> {
                int progress = phaseTicks;
                int interval = 2 + (progress * progress) / 12;
                advanceSpin(interval);
                if (phaseTicks >= SLOWING_TICKS) {
                    currentSpinName = resultDisplayName;
                    advance(Phase.RESULT);
                    playResultSound();
                }
            }
            case RESULT -> {
                if (phaseTicks >= RESULT_TICKS) {
                    advance(Phase.FADE_OUT);
                }
            }
            case FADE_OUT -> {
                if (phaseTicks >= FADE_OUT_TICKS) {
                    reset();
                }
            }
            default -> {}
        }
    }

    private static void advance(Phase next) {
        phase = next;
        phaseTicks = 0;
    }

    private static void advanceSpin(int intervalTicks) {
        spinTicksUntilFlip--;
        if (spinTicksUntilFlip <= 0) {
            spinTicksUntilFlip = Math.max(1, intervalTicks);
            int prev = spinIndex;
            int next = prev;
            int size = SPIN_POOL.size();
            while (next == prev && size > 1) {
                next = RNG.nextInt(size);
            }
            spinIndex = next;
            currentSpinName = SPIN_POOL.get(spinIndex);
            playClickSound(0.5f + RNG.nextFloat() * 0.4f);
        }
    }

    private static void playClickSound(float pitch) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.getSoundManager() == null) return;
        mc.getSoundManager().play(SimpleSoundInstance.forUI(
                SoundEvents.UI_BUTTON_CLICK.value(), pitch, 0.4f));
    }

    private static void playResultSound() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.getSoundManager() == null) return;
        switch (resultType) {
            case POSITIVE -> mc.getSoundManager().play(SimpleSoundInstance.forUI(
                    SoundEvents.PLAYER_LEVELUP, 1.0f, 0.7f));
            case NEGATIVE -> mc.getSoundManager().play(SimpleSoundInstance.forUI(
                    SoundEvents.WARDEN_HEARTBEAT, 0.7f, 0.7f));
            case CHAOTIC -> mc.getSoundManager().play(SimpleSoundInstance.forUI(
                    SoundEvents.FIREWORK_ROCKET_BLAST, 1.0f, 0.7f));
        }
    }

    private static EventType safeType(int ordinal) {
        EventType[] values = EventType.values();
        if (ordinal < 0 || ordinal >= values.length) return EventType.CHAOTIC;
        return values[ordinal];
    }

    private static EventRarity safeRarity(int ordinal) {
        EventRarity[] values = EventRarity.values();
        if (ordinal < 0 || ordinal >= values.length) return EventRarity.COMMON;
        return values[ordinal];
    }

    public static synchronized void registerSpinName(String name) {
        if (name == null || name.isEmpty()) return;
        if (!SPIN_POOL.contains(name)) {
            SPIN_POOL.add(name);
        }
    }

    public static List<String> spinPool() {
        return Collections.unmodifiableList(SPIN_POOL);
    }

    public static Phase getPhase() {
        return phase;
    }

    public static boolean isActive() {
        return phase != Phase.IDLE;
    }

    public static int getPhaseTicks() {
        return phaseTicks;
    }

    public static String getCurrentSpinName() {
        return currentSpinName;
    }

    public static String getResultEventId() {
        return resultEventId;
    }

    public static String getResultDisplayName() {
        return resultDisplayName;
    }

    public static EventType getResultType() {
        return resultType;
    }

    public static EventRarity getResultRarity() {
        return resultRarity;
    }
}
