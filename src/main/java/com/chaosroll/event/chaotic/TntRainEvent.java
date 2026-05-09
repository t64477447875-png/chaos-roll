package com.chaosroll.event.chaotic;

import com.chaosroll.event.*;
import com.chaosroll.event.ScheduledTaskManager;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.PrimedTnt;

import java.util.UUID;

public class TntRainEvent extends BaseEvent {
    @Override public String getId() { return "tnt_rain"; }
    @Override public String getDisplayName() { return "Дощ з ТНТ"; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 12; }

    private static final int TOTAL_TNT = 25;
    private static final int WAVES = 5;

    @Override
    public void execute(EventContext context) {
        ServerPlayer player = context.player();
        UUID id = player.getUUID();
        for (int wave = 0; wave < WAVES; wave++) {
            int delay = wave * 10;
            ScheduledTaskManager.schedule(context.server(), delay, srv -> {
                ServerPlayer target = srv.getPlayerList().getPlayer(id);
                if (target == null) return;
                int perWave = TOTAL_TNT / WAVES;
                for (int i = 0; i < perWave; i++) {
                    PrimedTnt tnt = EntityType.TNT.create(target.serverLevel());
                    if (tnt == null) continue;
                    double dx = (target.getRandom().nextDouble() - 0.5) * 30;
                    double dz = (target.getRandom().nextDouble() - 0.5) * 30;
                    tnt.setPos(target.getX() + dx, target.getY() + 18, target.getZ() + dz);
                    tnt.setFuse(60 + target.getRandom().nextInt(40));
                    target.serverLevel().addFreshEntity(tnt);
                }
            });
        }
        EventNotifyUtil.notifyPlayer(player, this, "ТНТ ДОЩ — 25 шт радіус 30 блоків. Тікай!");
    }
}
