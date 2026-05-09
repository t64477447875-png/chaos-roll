package com.chaosroll.event.chaotic;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import java.util.List;

public class MobMorphEvent extends BaseEvent {
    private static final List<Holder<MobEffect>> POOL = List.of(
            MobEffects.MOVEMENT_SPEED, MobEffects.JUMP, MobEffects.MOVEMENT_SLOWDOWN,
            MobEffects.WEAKNESS, MobEffects.NIGHT_VISION, MobEffects.WATER_BREATHING,
            MobEffects.INVISIBILITY, MobEffects.GLOWING
    );

    @Override public String getId() { return "mob_morph"; }
    @Override public String getDisplayName() { return "Морф"; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 8; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        for (int i = 0; i < 3; i++) {
            Holder<MobEffect> picked = POOL.get(context.random().nextInt(POOL.size()));
            player.addEffect(new MobEffectInstance(picked, 600, 1));
        }
        EventNotifyUtil.notifyPlayer(player, this, "3 випадкові ефекти на 30с");
    }
}