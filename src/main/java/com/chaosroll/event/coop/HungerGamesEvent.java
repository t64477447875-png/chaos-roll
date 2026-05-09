package com.chaosroll.event.coop;

import com.chaosroll.event.BaseEvent;
import com.chaosroll.event.EventContext;
import com.chaosroll.event.EventRarity;
import com.chaosroll.event.EventType;
import com.chaosroll.util.EventNotifyUtil;
import com.chaosroll.util.InventoryUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class HungerGamesEvent extends BaseEvent {

    @Override public String getId() { return "coop_hunger_games"; }
    @Override public String getDisplayName() { return "Hunger Games"; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.LEGENDARY; }
    @Override public int getWeight() { return 8; }
    @Override public boolean isGlobal() { return true; }
    @Override public int getDurationTicks() { return 1200; }

    @Override
    public boolean canExecute(EventContext context) {
        return super.canExecute(context) && CoopUtil.hasOtherOnline(context.player());
    }

    @Override
    public void execute(EventContext context) {
        ServerPlayer initiator = context.player();
        if (CoopState.HUNGER_GAMES != null) return;

        int endTick = context.server().getTickCount() + 1200;
        CoopState.HUNGER_GAMES = new CoopState.HungerGamesSession(endTick);

        for (ServerPlayer p : context.server().getPlayerList().getPlayers()) {
            p.setHealth(2.0f);
            p.getFoodData().setFoodLevel(20);
            InventoryUtil.giveOrDrop(p, new ItemStack(Items.WOODEN_SWORD));
            InventoryUtil.giveOrDrop(p, new ItemStack(Items.COOKED_BEEF, 4));
            p.addEffect(new MobEffectInstance(MobEffects.GLOWING, 1200, 0));
        }

        EventNotifyUtil.notifyAll(initiator, this,
                "HUNGER GAMES! 1 HP всім, 60с — останній виживший отримує алмаз!");
    }
}
