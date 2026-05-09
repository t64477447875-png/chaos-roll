package com.chaosroll.client;

import com.chaosroll.ChaosRollMod;
import com.chaosroll.client.hud.TimerState;
import com.chaosroll.config.ConfigManager;
import com.chaosroll.network.RollRequestPacket;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public final class RollKeybind {

    public static KeyMapping ROLL_KEY;
    public static KeyMapping MOUSE_ROLL_KEY;
    public static KeyMapping CONFIG_GUI_KEY;

    private RollKeybind() {}

    public static void register() {
        ROLL_KEY = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.chaosroll.roll",
                resolveKey(ConfigManager.get().rollKey),
                "category.chaosroll.main"
        ));

        MOUSE_ROLL_KEY = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.chaosroll.roll_mouse",
                InputConstants.Type.MOUSE,
                GLFW.GLFW_MOUSE_BUTTON_4,
                "category.chaosroll.main"
        ));

        CONFIG_GUI_KEY = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.chaosroll.config_gui",
                GLFW.GLFW_KEY_K,
                "category.chaosroll.main"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (ROLL_KEY.consumeClick()) {
                if (TimerState.isRollReady()) {
                    ChaosRollMod.LOGGER.info("[Chaos Roll] Roll key pressed - sending RollRequestPacket.");
                    ClientPlayNetworking.send(RollRequestPacket.INSTANCE);
                }
            }
            while (MOUSE_ROLL_KEY.consumeClick()) {
                if (TimerState.isRollReady()) {
                    ChaosRollMod.LOGGER.info("[Chaos Roll] Mouse roll button pressed - sending RollRequestPacket.");
                    ClientPlayNetworking.send(RollRequestPacket.INSTANCE);
                }
            }
            while (CONFIG_GUI_KEY.consumeClick()) {
                if (client.player != null && client.screen == null) {
                    client.setScreen(new com.chaosroll.client.gui.ChaosRollConfigScreen(null));
                }
            }
        });
    }

    private static int resolveKey(String name) {
        if (name == null || name.isBlank()) return GLFW.GLFW_KEY_R;
        String s = name.trim().toUpperCase();
        if (s.length() == 1) {
            char c = s.charAt(0);
            if (c >= 'A' && c <= 'Z') return GLFW.GLFW_KEY_A + (c - 'A');
            if (c >= '0' && c <= '9') return GLFW.GLFW_KEY_0 + (c - '0');
        }
        return GLFW.GLFW_KEY_R;
    }
}