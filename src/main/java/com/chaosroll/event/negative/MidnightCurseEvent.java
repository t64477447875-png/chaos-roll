package com.chaosroll.event.negative;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Phantom;

public class MidnightCurseEvent extends BaseEvent {
    @Override public String getId() { return "midnight_curse"; }
    @Override public String getDisplayName() { return "Опівнічне прокляття"; }
    @Override public EventType getType() { return EventType.NEGATIVE; }
    @Override public EventRarity getRarity() { return EventRarity.LEGENDARY; }
    @Override public int getWeight() { return 4; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        context.world().setDayTime(18000);
        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 600, 0));
        player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 600, 1));
        for (int i = 0; i < 6; i++) {
            Phantom p = EntityType.PHANTOM.create(context.world());
            if (p == null) continue;
            double dx = (context.random().nextDouble() - 0.5) * 6;
            double dz = (context.random().nextDouble() - 0.5) * 6;
            p.setPos(player.getX() + dx, player.getY() + 15, player.getZ() + dz);
            p.setPersistenceRequired();
            context.world().addFreshEntity(p);
        }
        EventNotifyUtil.notifyAll(player, this, "Опівніч + 6 фантомів + Blindness + Weakness II 30с");
    }
}
