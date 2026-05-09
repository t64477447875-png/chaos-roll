package com.chaosroll.event.chaotic;

import com.chaosroll.event.*;
import com.chaosroll.event.coop.CoopState;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class BerserkerEvent extends BaseEvent {
    @Override public String getId() { return "berserker"; }
    @Override public String getDisplayName() { return "Берсерк"; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 18; }
    @Override public int getDurationTicks() { return 600; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 600, 3));
        player.addEffect(new MobEffectInstance(MobEffects.GLOWING, 600, 0));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 600, 0));
        int endTick = context.server().getTickCount() + 600;
        CoopState.BERSERKER.put(player.getUUID(), endTick);
        EventNotifyUtil.notifyPlayer(player, this,
                "Strength IV + Glowing 30с — х3 дамаг але кровотеча 0.5HP/2с");
    }
}
