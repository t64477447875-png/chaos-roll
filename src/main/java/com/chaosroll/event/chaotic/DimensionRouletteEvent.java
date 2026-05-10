package com.chaosroll.event.chaotic;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public class DimensionRouletteEvent extends BaseEvent {
    @Override public String getId() { return "dimension_roulette"; }
    @Override public String getDisplayName() { return "Рулетка вимірів"; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.LEGENDARY; }
    @Override public int getWeight() { return 8; }

    @Override
    public void execute(EventContext context) {
        ServerPlayer player = context.player();
        ResourceKey<Level> currentDim = player.serverLevel().dimension();
        @SuppressWarnings("unchecked")
        ResourceKey<Level>[] dims = new ResourceKey[]{Level.OVERWORLD, Level.NETHER, Level.END};
        ResourceKey<Level> target;
        do {
            target = dims[player.getRandom().nextInt(dims.length)];
        } while (target == currentDim);

        ServerLevel targetLevel = player.getServer().getLevel(target);
        if (targetLevel == null) {
            EventNotifyUtil.notifyPlayer(player, this, "Вимір недоступний.");
            return;
        }

        if (target == Level.END) {
            player.teleportTo(targetLevel,
                    100 + player.getRandom().nextInt(50), 70,
                    player.getRandom().nextInt(50),
                    0, 0);
            EventNotifyUtil.notifyPlayer(player, this, "Welcome to the End.");
            return;
        }

        double scale;
        if (currentDim == Level.OVERWORLD && target == Level.NETHER) scale = 0.125;
        else if (currentDim == Level.NETHER && target == Level.OVERWORLD) scale = 8.0;
        else scale = 1.0;

        double newX = player.getX() * scale + player.getRandom().nextInt(40) - 20;
        double newZ = player.getZ() * scale + player.getRandom().nextInt(40) - 20;
        int newY;
        if (target == Level.NETHER) {
            newY = 80;
        } else {
            newY = targetLevel.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE,
                    (int) newX, (int) newZ) + 2;
        }

        player.teleportTo(targetLevel, newX, newY, newZ, player.getYRot(), player.getXRot());
        String name = target == Level.NETHER ? "Nether" : "Overworld";
        EventNotifyUtil.notifyPlayer(player, this, "Телепорт у " + name + ".");
    }
}
