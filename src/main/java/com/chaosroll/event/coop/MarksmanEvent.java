package com.chaosroll.event.coop;

import com.chaosroll.event.BaseEvent;
import com.chaosroll.event.EventContext;
import com.chaosroll.event.EventRarity;
import com.chaosroll.event.EventType;
import com.chaosroll.util.EventEnchantUtil;
import com.chaosroll.util.EventNotifyUtil;
import com.chaosroll.util.InventoryUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;

public class MarksmanEvent extends BaseEvent {

    @Override public String getId() { return "coop_marksman"; }
    @Override public String getDisplayName() { return "Стрільці"; }
    @Override public EventType getType() { return EventType.POSITIVE; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 14; }
    @Override public int getDurationTicks() { return 1200; }
    @Override public boolean isGlobal() { return true; }

    @Override
    public boolean canExecute(EventContext context) {
        return super.canExecute(context) && CoopUtil.hasOtherOnline(context.player());
    }

    @Override
    public void execute(EventContext context) {
        ServerPlayer initiator = context.player();
        for (ServerPlayer p : context.server().getPlayerList().getPlayers()) {
            ItemStack bow = new ItemStack(Items.BOW);
            try {
                EventEnchantUtil.enchant(p.serverLevel(), bow, Enchantments.POWER, 3);
                EventEnchantUtil.enchant(p.serverLevel(), bow, Enchantments.UNBREAKING, 3);
            } catch (Throwable ignored) {
            }
            InventoryUtil.giveOrDrop(p, bow);
            InventoryUtil.giveOrDrop(p, new ItemStack(Items.ARROW, 32));
            p.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, getDurationTicks(), 1));
            p.addEffect(new MobEffectInstance(MobEffects.GLOWING, getDurationTicks(), 0, true, false, true));
        }
        EventNotifyUtil.notifyAll(initiator, this,
                "Усі гравці отримали лук Power III + 32 стріли + Strength II + Glowing на 1 хв!");
    }
}
