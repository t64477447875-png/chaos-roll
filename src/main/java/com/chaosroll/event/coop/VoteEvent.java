package com.chaosroll.event.coop;

import com.chaosroll.event.BaseEvent;
import com.chaosroll.event.EventContext;
import com.chaosroll.event.EventRarity;
import com.chaosroll.event.EventType;
import com.chaosroll.event.ScheduledTaskManager;
import com.chaosroll.util.EventNotifyUtil;
import com.chaosroll.util.InventoryUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class VoteEvent extends BaseEvent {

    @Override public String getId() { return "coop_vote"; }
    @Override public String getDisplayName() { return "Голосування долі"; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 20; }
    @Override public boolean isGlobal() { return true; }

    @Override
    public boolean canExecute(EventContext context) {
        return super.canExecute(context) && CoopUtil.hasOtherOnline(context.player());
    }

    @Override
    public void execute(EventContext context) {
        ServerPlayer initiator = context.player();
        for (ServerPlayer p : context.server().getPlayerList().getPlayers()) {
            p.sendSystemMessage(Component.literal("[Chaos Roll] ГОЛОСУВАННЯ! Сиди (Shift) = BAD, стій = GOOD. 10с..."));
        }

        ScheduledTaskManager.schedule(context.server(), 200, srv -> {
            int sneak = 0;
            int total = 0;
            for (ServerPlayer p : srv.getPlayerList().getPlayers()) {
                total++;
                if (p.isShiftKeyDown() || p.isCrouching()) sneak++;
            }
            if (total == 0) return;
            boolean bad = sneak * 2 > total;
            for (ServerPlayer p : srv.getPlayerList().getPlayers()) {
                if (bad) {
                    p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 600, 3));
                    p.addEffect(new MobEffectInstance(MobEffects.WITHER, 200, 1));
                    p.sendSystemMessage(Component.literal("[Chaos Roll] BAD виграв! Slow IV + Wither II"));
                } else {
                    p.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 1));
                    p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 600, 1));
                    InventoryUtil.giveOrDrop(p, new ItemStack(Items.GOLDEN_APPLE, 1));
                    p.sendSystemMessage(Component.literal("[Chaos Roll] GOOD виграв! Regen + Speed II + Golden Apple"));
                }
            }
        });
        EventNotifyUtil.notifyAll(initiator, this, "Голосування 10 секунд!");
    }
}
