package com.chaosroll.event.positive;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import com.chaosroll.util.InventoryUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.StructureTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;

public class TreasureMapEvent extends BaseEvent {
    @Override public String getId() { return "treasure_map"; }
    @Override public String getDisplayName() { return "Карта скарбів"; }
    @Override public EventType getType() { return EventType.POSITIVE; }
    @Override public EventRarity getRarity() { return EventRarity.COMMON; }
    @Override public int getWeight() { return 40; }

    @Override
    public void execute(EventContext context) {
        var level = context.world();
        var player = context.player();
        BlockPos origin = player.blockPosition();
        BlockPos found = null;
        try {
            found = level.findNearestMapStructure(StructureTags.ON_TREASURE_MAPS, origin, 100, true);
        } catch (Throwable ignored) {}
        int x = found != null ? found.getX() : origin.getX();
        int z = found != null ? found.getZ() : origin.getZ();
        ItemStack map = MapItem.create(level, x, z, (byte) 2, true, true);
        MapItem.renderBiomePreviewMap(level, map);
        map.set(DataComponents.CUSTOM_NAME, Component.literal("Карта скарбів").withStyle(ChatFormatting.GOLD));
        InventoryUtil.giveOrDrop(player, map);
        EventNotifyUtil.notifyPlayer(player, this, found != null ? "Знайдено скарб!" : "Карта місцевості");
    }
}