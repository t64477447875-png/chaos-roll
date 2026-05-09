package com.chaosroll.event.chaotic;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import java.util.List;

public class SoundChaosEvent extends BaseEvent {
    private static final List<SoundEvent> POOL = List.of(
            SoundEvents.WITHER_SPAWN, SoundEvents.GHAST_SCREAM, SoundEvents.ENDER_DRAGON_GROWL,
            SoundEvents.ANVIL_LAND, SoundEvents.LIGHTNING_BOLT_THUNDER, SoundEvents.GOAT_SCREAMING_AMBIENT
    );

    @Override public String getId() { return "sound_chaos"; }
    @Override public String getDisplayName() { return "Звуковий хаос"; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.COMMON; }
    @Override public int getWeight() { return 15; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        for (int i = 0; i < 3; i++) {
            SoundEvent snd = POOL.get(context.random().nextInt(POOL.size()));
            context.world().playSound(null, player.blockPosition(), snd, SoundSource.MASTER, 1.0f, 1.0f);
        }
        EventNotifyUtil.notifyPlayer(player, this, "Що це за звуки?!");
    }
}