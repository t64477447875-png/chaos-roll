package com.chaosroll.event.chaotic;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.world.item.ItemStack;

/**
 * Applies a 5-second cooldown to every item currently in the player's hotbar at activation.
 * Repeats every 8s for 60s, so the player has to constantly switch slots.
 */
public class ItemCooldownEvent extends BaseEvent {
    @Override public String getId() { return "item_cooldown"; }
    @Override public String getDisplayName() { return "Закляття кулдауну"; }
    @Override public String getDescription() { return "60с — кожні 8 сек предмети у hotbar отримують 5-сек кулдаун."; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.COMMON; }
    @Override public int getWeight() { return 16; }
    @Override public int getDurationTicks() { return 1200; }

    @Override
    public void execute(EventContext context) {
        var p = context.player();
        java.util.UUID id = p.getUUID();
        for (int wave = 0; wave < 8; wave++) {
            int delay = wave * 160; // every 8 seconds
            ScheduledTaskManager.schedule(context.server(), delay, srv -> {
                var target = srv.getPlayerList().getPlayer(id);
                if (target == null) return;
                java.util.Set<net.minecraft.world.item.Item> seen = new java.util.HashSet<>();
                for (int slot = 0; slot < 9; slot++) {
                    ItemStack stack = target.getInventory().getItem(slot);
                    if (stack.isEmpty()) continue;
                    if (seen.add(stack.getItem())) {
                        target.getCooldowns().addCooldown(stack.getItem(), 100);
                    }
                }
            });
        }
        EventNotifyUtil.notifyPlayer(p, this, "60с — кулдауни на hotbar кожні 8 сек!");
    }
}
