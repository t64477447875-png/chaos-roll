package com.chaosroll.event.negative;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public class ItemCorruptionEvent extends BaseEvent {
    @Override public String getId() { return "item_corruption"; }
    @Override public String getDisplayName() { return "Корозія предмета"; }
    @Override public EventType getType() { return EventType.NEGATIVE; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 15; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        ItemStack held = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (held.isEmpty() || !held.isDamageableItem()) {
            EventNotifyUtil.notifyPlayer(player, this, "Нічого ламати");
            return;
        }
        int newDamage = held.getDamageValue() + held.getMaxDamage() / 2;
        held.setDamageValue(Math.min(newDamage, held.getMaxDamage() - 1));
        EventNotifyUtil.notifyPlayer(player, this, "Предмет в руці пошкоджено на 50%");
    }
}