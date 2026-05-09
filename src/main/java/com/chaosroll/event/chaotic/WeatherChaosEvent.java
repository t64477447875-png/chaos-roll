package com.chaosroll.event.chaotic;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;

public class WeatherChaosEvent extends BaseEvent {
    @Override public String getId() { return "weather_chaos"; }
    @Override public String getDisplayName() { return "Погодний хаос"; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.COMMON; }
    @Override public int getWeight() { return 15; }

    @Override
    public void execute(EventContext context) {
        int weather = context.random().nextInt(3);
        String label;
        switch (weather) {
            case 0 -> { context.world().setWeatherParameters(6000, 0, false, false); label = "Ясно"; }
            case 1 -> { context.world().setWeatherParameters(0, 6000, true, false); label = "Дощ"; }
            default -> { context.world().setWeatherParameters(0, 6000, true, true); label = "Гроза"; }
        }
        EventNotifyUtil.notifyAll(context.player(), this, "Погода: " + label);
    }
}