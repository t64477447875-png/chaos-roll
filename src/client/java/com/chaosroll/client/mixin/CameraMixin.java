package com.chaosroll.client.mixin;

import com.chaosroll.client.state.ScreenFlipState;
import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {

    @Unique
    private boolean chaosroll$reentry;

    @Inject(method = "setRotation(FF)V", at = @At("TAIL"))
    private void chaosroll$flipRotation(float yaw, float pitch, CallbackInfo ci) {
        if (chaosroll$reentry) return;
        if (!ScreenFlipState.isActive()) return;
        chaosroll$reentry = true;
        try {
            ((CameraInvoker) (Object) this).chaosroll$setRotation(yaw + 180.0f, -pitch);
        } finally {
            chaosroll$reentry = false;
        }
    }
}
