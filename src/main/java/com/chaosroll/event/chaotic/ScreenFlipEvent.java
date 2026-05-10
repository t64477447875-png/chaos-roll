package com.chaosroll.event.chaotic;

import com.chaosroll.event.*;
import com.chaosroll.network.ScreenFlipPacket;
import com.chaosroll.util.EventNotifyUtil;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class ScreenFlipEvent extends BaseEvent {
    @Override public String getId() { return "screen_flip"; }
    @Override public String getDisplayName() { return "Перевернутий екран"; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 14; }
    @Override public int getDurationTicks() { return 600; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        ServerPlayNetworking.send(player, new ScreenFlipPacket(600));
        player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 600, 0));
        EventNotifyUtil.notifyPlayer(player, this, "30с — світ перевернувся!");
    }
}
