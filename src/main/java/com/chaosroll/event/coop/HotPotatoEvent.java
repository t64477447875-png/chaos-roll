package com.chaosroll.event.coop;

import com.chaosroll.event.BaseEvent;
import com.chaosroll.event.EventContext;
import com.chaosroll.event.EventRarity;
import com.chaosroll.event.EventType;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class HotPotatoEvent extends BaseEvent {

    @Override public String getId() { return "coop_hot_potato"; }
    @Override public String getDisplayName() { return "Курка-бомба"; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 12; }
    @Override public int getDurationTicks() { return 600; }
    @Override public boolean isGlobal() { return true; }

    @Override
    public boolean canExecute(EventContext context) {
        return super.canExecute(context) && CoopUtil.hasOtherOnline(context.player());
    }

    @Override
    public void execute(EventContext context) {
        ServerPlayer initiator = context.player();
        List<ServerPlayer> all = CoopUtil.allPlayers(initiator);
        ServerPlayer chosen = all.get(context.random().nextInt(all.size()));

        CoopTickHandler.stripHotPotato(context.server());

        ItemStack stack = new ItemStack(Items.CHICKEN);
        stack.set(DataComponents.CUSTOM_NAME,
                Component.literal("✦ Курка-бомба ✦").withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
        CoopTickHandler.applyMarker(stack, CoopTickHandler.NBT_HOT_POTATO);
        chosen.getInventory().add(stack);

        int endTick = context.server().getTickCount() + getDurationTicks();
        CoopState.HOT_POTATO.put(chosen.getUUID(),
                new CoopState.HotPotatoSession(chosen.getUUID(), endTick, stack));

        EventNotifyUtil.notifyAll(initiator, this,
                chosen.getName().getString()
                        + " отримав КУРКУ-БОМБУ! 30с щоб скинути її іншому (Q)!");
    }
}
