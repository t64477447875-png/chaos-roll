package com.chaosroll.event.chaotic;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Vex;

/**
 * Spawns a "phantom echo" Vex that hovers next to the player for 60s. Disorienting visual.
 */
public class EchoCloneEvent extends BaseEvent {
    @Override public String getId() { return "echo_clone"; }
    @Override public String getDisplayName() { return "Ехо-клон"; }
    @Override public String getDescription() { return "60с — поряд з тобою літає ехо-клон з твоїм нікнеймом."; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.COMMON; }
    @Override public int getWeight() { return 16; }
    @Override public int getDurationTicks() { return 1200; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        var world = context.world();

        Vex vex = EntityType.VEX.create(world);
        if (vex == null) {
            EventNotifyUtil.notifyPlayer(player, this, "Ехо-клон не з'явився.");
            return;
        }
        vex.setPos(player.getX() + 1.5, player.getY() + 2, player.getZ() + 1.5);
        vex.setInvulnerable(true);
        vex.setSilent(true);
        vex.setCustomName(net.minecraft.network.chat.Component.literal("§7Ехо " + player.getName().getString()));
        vex.setCustomNameVisible(true);
        // Limited charge time so it auto-despawns after a while if our scheduler misses it.
        vex.setLimitedLife(1200);
        world.addFreshEntity(vex);

        java.util.UUID vexUuid = vex.getUUID();
        ScheduledTaskManager.schedule(context.server(), 1200, srv -> {
            var lvl = player.serverLevel();
            var ent = lvl.getEntity(vexUuid);
            if (ent != null) ent.discard();
        });
        EventNotifyUtil.notifyPlayer(player, this, "60с — твій ехо-клон ширяє поруч!");
    }
}
