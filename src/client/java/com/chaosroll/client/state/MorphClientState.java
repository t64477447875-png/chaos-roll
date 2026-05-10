package com.chaosroll.client.state;

import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;

/**
 * Tracks "morph" mode on the client: forces third-person view, hides the first-person hand,
 * and reports a custom eye-height so the camera sits at the morph mob's eye level.
 */
public final class MorphClientState {

    private static long endTimeMs = 0L;
    private static String mobTypeKey = "";
    private static float eyeHeight = 1.62f;
    private static CameraType savedCameraType;

    private MorphClientState() {}

    public static void start(int durationTicks, String mobKey, float mobEyeHeight) {
        endTimeMs = System.currentTimeMillis() + durationTicks * 50L;
        mobTypeKey = mobKey;
        eyeHeight = mobEyeHeight;
        Minecraft mc = Minecraft.getInstance();
        if (mc.options != null) {
            if (savedCameraType == null) {
                savedCameraType = mc.options.getCameraType();
            }
            mc.options.setCameraType(CameraType.THIRD_PERSON_BACK);
        }
    }

    public static void clear() {
        endTimeMs = 0L;
        mobTypeKey = "";
        Minecraft mc = Minecraft.getInstance();
        if (mc.options != null && savedCameraType != null) {
            mc.options.setCameraType(savedCameraType);
            savedCameraType = null;
        }
    }

    public static boolean isActive() {
        if (System.currentTimeMillis() < endTimeMs) return true;
        if (endTimeMs != 0L) {
            // Auto-clear once the timer elapses.
            clear();
        }
        return false;
    }

    public static String mobTypeKey() {
        return mobTypeKey;
    }

    public static float eyeHeight() {
        return eyeHeight;
    }
}
