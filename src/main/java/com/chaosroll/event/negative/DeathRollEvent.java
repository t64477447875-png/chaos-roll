package com.chaosroll.event.negative;

import com.chaosroll.config.ConfigManager;
import com.chaosroll.event.*;
import com.chaosroll.event.ScheduledTaskManager;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import java.util.UUID;

public class DeathRollEvent extends BaseEvent {
    @Override public String getId() { return "death_roll"; }
    @Override public String getDisplayName() { return "СМЕРТЬ"; }
    @Override public EventType getType() { return EventType.NEGATIVE; }
    @Override public EventRarity getRarity() { return EventRarity.LEGENDARY; }
    @Override public int getWeight() { return 3; }

    @Override
    public boolean canExecute(EventContext context) {
        if (ConfigManager.get().preventDirectDeath) return false;
        return super.canExecute(context);
    }

    @Override
    public void execute(EventContext context) {
        ServerPlayer player = context.player();
        UUID id = player.getUUID();
        player.serverLevel().sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                player.getX(), player.getY() + 1, player.getZ(),
                40, 0.6, 0.8, 0.6, 0.05);
        player.serverLevel().playSound(null, player.blockPosition(),
                SoundEvents.WITHER_DEATH, SoundSource.HOSTILE, 1.0f, 0.7f);
        EventNotifyUtil.notifyAll(player, this,
                "✦ ЧОРНА КАРТА ✦ — " + player.getName().getString() + " помирає за 3с");

        ScheduledTaskManager.schedule(context.server(), 60, srv -> {
            ServerPlayer target = srv.getPlayerList().getPlayer(id);
            if (target == null) return;
            srv.getPlayerList().broadcastSystemMessage(
                    Component.literal("[Chaos Roll] " + target.getName().getString() + " — RIP")
                            .withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD), false);
            target.kill();
        });
    }
}
