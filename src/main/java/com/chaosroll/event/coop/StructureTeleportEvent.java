package com.chaosroll.event.coop;

import com.chaosroll.event.BaseEvent;
import com.chaosroll.event.EventContext;
import com.chaosroll.event.EventRarity;
import com.chaosroll.event.EventType;
import com.chaosroll.util.EventNotifyUtil;
import com.chaosroll.util.SafeTeleportUtil;
import com.chaosroll.util.SafetyUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.StructureTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.List;

public class StructureTeleportEvent extends BaseEvent {

    private static final List<TagKey<Structure>> STRUCTURE_POOL = List.of(
            StructureTags.VILLAGE,
            StructureTags.OCEAN_RUIN,
            StructureTags.MINESHAFT,
            StructureTags.ON_TREASURE_MAPS,
            StructureTags.EYE_OF_ENDER_LOCATED
    );

    @Override public String getId() { return "coop_structure_teleport"; }
    @Override public String getDisplayName() { return "Спільна пригода"; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 10; }
    @Override public boolean isGlobal() { return true; }

    @Override
    public boolean canExecute(EventContext context) {
        return super.canExecute(context) && CoopUtil.hasOtherOnline(context.player());
    }

    @Override
    public void execute(EventContext context) {
        ServerPlayer initiator = context.player();
        ServerLevel world = context.world();

        TagKey<Structure> chosenTag = STRUCTURE_POOL.get(
                context.random().nextInt(STRUCTURE_POOL.size()));
        BlockPos found = null;
        try {
            found = world.findNearestMapStructure(chosenTag, initiator.blockPosition(), 200, false);
        } catch (Throwable ignored) {}

        if (found == null) {
            for (ServerPlayer p : context.server().getPlayerList().getPlayers()) {
                SafeTeleportUtil.teleportRandom(p, 200);
            }
            EventNotifyUtil.notifyAll(initiator, this,
                    "Структура не знайдена — усіх розкидало.");
            return;
        }

        BlockPos safe = SafetyUtil.findSafeY(world, found.getX() + 4, found.getZ() + 4);
        if (safe == null) safe = found.above();

        for (ServerPlayer p : context.server().getPlayerList().getPlayers()) {
            double angle = context.random().nextDouble() * Math.PI * 2.0;
            double dx = Math.cos(angle) * 3.0;
            double dz = Math.sin(angle) * 3.0;
            p.teleportTo(safe.getX() + 0.5 + dx, safe.getY(), safe.getZ() + 0.5 + dz);
        }

        EventNotifyUtil.notifyAll(initiator, this,
                "Усі гравці телепортовані до структури на " + safe.getX() + ", "
                        + safe.getY() + ", " + safe.getZ() + "!");
    }
}
