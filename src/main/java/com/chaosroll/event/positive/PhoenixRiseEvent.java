package com.chaosroll.event.positive;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import com.chaosroll.util.InventoryUtil;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class PhoenixRiseEvent extends BaseEvent {
    @Override public String getId() { return "phoenix_rise"; }
    @Override public String getDisplayName() { return "Воскресіння Фенікса"; }
    @Override public EventType getType() { return EventType.POSITIVE; }
    @Override public EventRarity getRarity() { return EventRarity.LEGENDARY; }
    @Override public int getWeight() { return 6; }
    @Override public int getDurationTicks() { return 1200; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 1200, 0));
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 600, 1));
        for (int i = 0; i < 4; i++) {
            InventoryUtil.giveOrDrop(player, new ItemStack(Items.TOTEM_OF_UNDYING));
        }
        EventNotifyUtil.notifyPlayer(player, this, "Fire Resistance 60с + Regen II 30с + 4 тотеми");
    }
}
