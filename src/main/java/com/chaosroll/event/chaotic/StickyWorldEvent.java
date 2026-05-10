package com.chaosroll.event.chaotic;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

/**
 * For 60s the player walks/jumps as if everything is sticky honey: extreme slowness + jump boost
 * partial cancel. Looks chaotic — no real damage but movement becomes wobbly.
 */
public class StickyWorldEvent extends BaseEvent {
    @Override public String getId() { return "sticky_world"; }
    @Override public String getDisplayName() { return "Липкий світ"; }
    @Override public String getDescription() { return "60с — світ стає липким як медовий блок: рух уповільнено, стрибки коротші."; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.COMMON; }
    @Override public int getWeight() { return 18; }
    @Override public int getDurationTicks() { return 1200; }

    @Override
    public void execute(EventContext context) {
        var p = context.player();
        p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 1200, 2, false, true));
        p.addEffect(new MobEffectInstance(MobEffects.JUMP, 1200, 128, false, true)); // negative jump = no jump height
        EventNotifyUtil.notifyPlayer(p, this, "60с — все липке як мед!");
    }
}
