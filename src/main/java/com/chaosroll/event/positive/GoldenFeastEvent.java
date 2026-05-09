package com.chaosroll.event.positive;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import com.chaosroll.util.InventoryUtil;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class GoldenFeastEvent extends BaseEvent {
    @Override public String getId() { return "golden_feast"; }
    @Override public String getDisplayName() { return "Золотий бенкет"; }
    @Override public EventType getType() { return EventType.POSITIVE; }
    @Override public EventRarity getRarity() { return EventRarity.COMMON; }
    @Override public int getWeight() { return 55; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        InventoryUtil.giveOrDrop(player, new ItemStack(Items.GOLDEN_APPLE, 8));
        InventoryUtil.giveOrDrop(player, new ItemStack(Items.COOKED_BEEF, 32));
        player.getFoodData().eat(20, 1.0f);
        player.addEffect(new MobEffectInstance(MobEffects.SATURATION, 200, 2));
        EventNotifyUtil.notifyPlayer(player, this, "Їжа + повний голод + Saturation");
    }
}