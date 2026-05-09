package com.chaosroll.event.chaotic;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import java.util.List;

public class RandomPotionEvent extends BaseEvent {
    private static final List<Holder<MobEffect>> POOL = List.of(
            MobEffects.MOVEMENT_SPEED, MobEffects.JUMP, MobEffects.REGENERATION,
            MobEffects.MOVEMENT_SLOWDOWN, MobEffects.WEAKNESS, MobEffects.NIGHT_VISION,
            MobEffects.FIRE_RESISTANCE, MobEffects.WATER_BREATHING, MobEffects.HUNGER
    );

    @Override public String getId() { return "random_potion"; }
    @Override public String getDisplayName() { return "Випадкове зілля"; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.COMMON; }
    @Override public int getWeight() { return 15; }

    @Override
    public void execute(EventContext context) {
        Holder<MobEffect> picked = POOL.get(context.random().nextInt(POOL.size()));
        context.player().addEffect(new MobEffectInstance(picked, 600, 1));
        EventNotifyUtil.notifyPlayer(context.player(), this, "Випадковий ефект на 30с");
    }
}