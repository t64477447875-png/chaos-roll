package com.chaosroll.event.chaotic;

import com.chaosroll.event.*;
import com.chaosroll.event.ScheduledTaskManager;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.UUID;

public class TallPlayerEvent extends BaseEvent {
    private static final ResourceLocation MOD_ID = ResourceLocation.fromNamespaceAndPath("chaosroll", "tall_player");

    @Override public String getId() { return "tall_player"; }
    @Override public String getDisplayName() { return "Зміна розміру"; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.COMMON; }
    @Override public int getWeight() { return 28; }
    @Override public int getDurationTicks() { return 600; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        UUID id = player.getUUID();
        boolean tall = context.random().nextBoolean();
        double mult = tall ? 0.6 : -0.5;
        AttributeInstance scale = player.getAttribute(Attributes.SCALE);
        if (scale == null) {
            EventNotifyUtil.notifyPlayer(player, this, "Scale не підтримується");
            return;
        }
        scale.removeModifier(MOD_ID);
        scale.addTransientModifier(new AttributeModifier(MOD_ID, mult, AttributeModifier.Operation.ADD_VALUE));
        ScheduledTaskManager.schedule(context.server(), getDurationTicks(), srv -> {
            ServerPlayer p = srv.getPlayerList().getPlayer(id);
            if (p == null) return;
            AttributeInstance s = p.getAttribute(Attributes.SCALE);
            if (s != null) s.removeModifier(MOD_ID);
        });
        EventNotifyUtil.notifyPlayer(player, this, tall ? "Великий 1.6x на 30с" : "Малий 0.5x на 30с");
    }
}