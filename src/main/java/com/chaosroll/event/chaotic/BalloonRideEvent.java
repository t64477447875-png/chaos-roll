package com.chaosroll.event.chaotic;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

/**
 * For 30s the player slowly drifts upward (Levitation I, no Slow Falling). Sneak still works to
 * descend partially. Without elytra/water buffer = lethal at the end.
 */
public class BalloonRideEvent extends BaseEvent {
    @Override public String getId() { return "balloon_ride"; }
    @Override public String getDisplayName() { return "Повітряна куля"; }
    @Override public String getDescription() { return "30с — тебе плавно піднімає в небо. Шукай куди впасти."; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 10; }
    @Override public int getDurationTicks() { return 600; }

    @Override
    public void execute(EventContext context) {
        var p = context.player();
        // Levitation I (amplifier 0) for 600 ticks → very gentle ascent.
        p.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 600, 0, false, true));
        EventNotifyUtil.notifyPlayer(p, this, "30с — ти повільно піднімаєшся. Без Slow Falling — пильнуй приземлення!");
    }
}
