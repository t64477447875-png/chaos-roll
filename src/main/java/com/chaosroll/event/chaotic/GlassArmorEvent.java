package com.chaosroll.event.chaotic;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import com.chaosroll.util.InventoryUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class GlassArmorEvent extends BaseEvent {
    @Override public String getId() { return "glass_armor"; }
    @Override public String getDisplayName() { return "Скляна броня"; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 8; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        ItemStack[] pieces = { new ItemStack(Items.LEATHER_HELMET), new ItemStack(Items.LEATHER_CHESTPLATE),
                new ItemStack(Items.LEATHER_LEGGINGS), new ItemStack(Items.LEATHER_BOOTS) };
        for (ItemStack p : pieces) {
            p.set(DataComponents.CUSTOM_NAME, Component.literal("Скляна броня").withStyle(ChatFormatting.AQUA));
            InventoryUtil.giveOrDrop(player, p);
        }
        player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 600, 0));
        EventNotifyUtil.notifyPlayer(player, this, "Шкіра з Slow Falling 30с");
    }
}