package com.chaosroll.event.chaotic;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import com.chaosroll.util.InventoryUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class SlotMachineEvent extends BaseEvent {
    @Override public String getId() { return "slot_machine"; }
    @Override public String getDisplayName() { return "Ігровий автомат"; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 8; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        int roll = context.random().nextInt(100);
        if (roll < 10) {
            InventoryUtil.giveOrDrop(player, new ItemStack(Items.NETHERITE_INGOT, 3));
            EventNotifyUtil.notifyPlayer(player, this, "JACKPOT! +3 незерита");
        } else if (roll < 40) {
            InventoryUtil.giveOrDrop(player, new ItemStack(Items.GOLD_INGOT, 16));
            EventNotifyUtil.notifyPlayer(player, this, "Виграш: +16 золота");
        } else if (roll < 70) {
            EventNotifyUtil.notifyPlayer(player, this, "Нічого. Спробуй ще");
        } else {
            int loss = Math.min(3, player.experienceLevel);
            if (loss > 0) player.giveExperienceLevels(-loss);
            EventNotifyUtil.notifyPlayer(player, this, "Програш: -" + loss + " рівнів");
        }
    }
}