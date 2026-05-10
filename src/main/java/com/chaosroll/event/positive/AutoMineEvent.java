package com.chaosroll.event.positive;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

/**
 * Strong mining buff: Haste III + Strength I + Saturation for 90 seconds. Best paired with a
 * netherite pickaxe. Less impactful than full god-mode but actually useful.
 */
public class AutoMineEvent extends BaseEvent {
    @Override public String getId() { return "auto_mine"; }
    @Override public String getDisplayName() { return "Майнер бог"; }
    @Override public String getDescription() { return "90с — Haste III + Strength I + Saturation. Йди добувати."; }
    @Override public EventType getType() { return EventType.POSITIVE; }
    @Override public EventRarity getRarity() { return EventRarity.COMMON; }
    @Override public int getWeight() { return 20; }
    @Override public int getDurationTicks() { return 1800; }

    @Override
    public void execute(EventContext context) {
        var p = context.player();
        p.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 1800, 2, false, true));
        p.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 1800, 0, false, true));
        p.addEffect(new MobEffectInstance(MobEffects.SATURATION, 200, 0, false, true));
        EventNotifyUtil.notifyPlayer(p, this, "90с — Haste III + Strength I, копай камінь!");
    }
}
