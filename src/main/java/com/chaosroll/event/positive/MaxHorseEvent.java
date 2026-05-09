package com.chaosroll.event.positive;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class MaxHorseEvent extends BaseEvent {
    @Override public String getId() { return "max_horse"; }
    @Override public String getDisplayName() { return "Топ-кінь"; }
    @Override public EventType getType() { return EventType.POSITIVE; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 20; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        Horse horse = EntityType.HORSE.create(context.world());
        if (horse == null) return;
        horse.setPos(player.getX() + 1.5, player.getY(), player.getZ() + 1.5);
        horse.setTamed(true);
        horse.setOwnerUUID(player.getUUID());
        var hp = horse.getAttribute(Attributes.MAX_HEALTH);
        if (hp != null) hp.setBaseValue(30.0);
        var sp = horse.getAttribute(Attributes.MOVEMENT_SPEED);
        if (sp != null) sp.setBaseValue(0.4);
        var jp = horse.getAttribute(Attributes.JUMP_STRENGTH);
        if (jp != null) jp.setBaseValue(1.0);
        horse.setHealth(30.0f);
        horse.equipSaddle(new ItemStack(Items.SADDLE), null);
        context.world().addFreshEntity(horse);
        EventNotifyUtil.notifyPlayer(player, this, "Швидкий кінь з сідлом");
    }
}