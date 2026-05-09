package com.chaosroll.event.positive;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class ServerHealthBoostEvent extends BaseEvent {
    private static final ResourceLocation MOD_ID = ResourceLocation.fromNamespaceAndPath("chaosroll", "server_health_boost");

    @Override public String getId() { return "server_health_boost"; }
    @Override public String getDisplayName() { return "Глобальний приріст HP"; }
    @Override public EventType getType() { return EventType.POSITIVE; }
    @Override public EventRarity getRarity() { return EventRarity.LEGENDARY; }
    @Override public int getWeight() { return 5; }
    @Override public boolean isGlobal() { return true; }
    @Override public int getDurationTicks() { return 6000; }

    @Override
    public void execute(EventContext context) {
        var server = context.server();
        AttributeModifier mod = new AttributeModifier(MOD_ID, 10.0, AttributeModifier.Operation.ADD_VALUE);

        for (ServerPlayer p : server.getPlayerList().getPlayers()) {
            AttributeInstance attr = p.getAttribute(Attributes.MAX_HEALTH);
            if (attr == null) continue;
            attr.removeModifier(MOD_ID);
            attr.addTransientModifier(mod);
            p.setHealth(p.getMaxHealth());
        }
        EventNotifyUtil.notifyAll(context.player(), this, "Усі гравці +10 HP на 5 хв!");

        com.chaosroll.event.ScheduledTaskManager.schedule(server, getDurationTicks(), srv -> {
            for (ServerPlayer p : srv.getPlayerList().getPlayers()) {
                AttributeInstance attr = p.getAttribute(Attributes.MAX_HEALTH);
                if (attr != null) attr.removeModifier(MOD_ID);
            }
        });
    }
}