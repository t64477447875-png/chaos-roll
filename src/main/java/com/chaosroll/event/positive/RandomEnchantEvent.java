package com.chaosroll.event.positive;

import com.chaosroll.event.*;
import com.chaosroll.util.EventEnchantUtil;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.List;

public class RandomEnchantEvent extends BaseEvent {
    private static final List<ResourceKey<Enchantment>> POOL = List.of(
            Enchantments.SHARPNESS, Enchantments.EFFICIENCY, Enchantments.UNBREAKING,
            Enchantments.FORTUNE, Enchantments.MENDING, Enchantments.PROTECTION,
            Enchantments.LOOTING, Enchantments.SILK_TOUCH, Enchantments.POWER
    );

    @Override public String getId() { return "random_enchant"; }
    @Override public String getDisplayName() { return "Випадкова зачарованість"; }
    @Override public EventType getType() { return EventType.POSITIVE; }
    @Override public EventRarity getRarity() { return EventRarity.COMMON; }
    @Override public int getWeight() { return 50; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        ItemStack held = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (held.isEmpty()) {
            EventNotifyUtil.notifyPlayer(player, this, "Тримай предмет в руці для зачарування");
            return;
        }
        ResourceKey<Enchantment> picked = POOL.get(context.random().nextInt(POOL.size()));
        try {
            EventEnchantUtil.enchant(context.world(), held, picked, 3);
            EventNotifyUtil.notifyPlayer(player, this, "Випадкове зачарування додано!");
        } catch (Throwable err) {
            EventNotifyUtil.notifyPlayer(player, this, "Не вдалось додати зачарування");
        }
    }
}