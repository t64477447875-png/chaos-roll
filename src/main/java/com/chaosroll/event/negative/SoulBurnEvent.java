package com.chaosroll.event.negative;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodData;

public class SoulBurnEvent extends BaseEvent {
    @Override public String getId() { return "soul_burn"; }
    @Override public String getDisplayName() { return "Опік душі"; }
    @Override public String getDescription() { return "40с Wither I + Hunger III, 0 насиченості"; }
    @Override public EventType getType() { return EventType.NEGATIVE; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 10; }
    @Override public int getDurationTicks() { return 800; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        player.addEffect(new MobEffectInstance(MobEffects.WITHER, 800, 0));
        player.addEffect(new MobEffectInstance(MobEffects.HUNGER, 800, 2));
        FoodData food = player.getFoodData();
        food.setSaturation(0f);
        food.setFoodLevel(Math.max(food.getFoodLevel() - 6, 4));
        EventNotifyUtil.notifyPlayer(player, this, "40с — Wither + Hunger III, 0 насиченості!");
    }
}
