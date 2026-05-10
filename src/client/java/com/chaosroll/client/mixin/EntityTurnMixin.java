package com.chaosroll.client.mixin;

import com.chaosroll.client.state.DirectionLockState;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Locks the local player's yaw while {@link DirectionLockState} is active.
 *
 * <p>Re-enters {@link Entity#turn(double, double)} only via {@code setYRot}/{@code setXRot} side-effects;
 * we cancel the call entirely when locked and re-set rotations to the locked yaw.
 * Movement (W/A/S/D) is unaffected.</p>
 */
@Mixin(Entity.class)
public abstract class EntityTurnMixin {

    @Inject(method = "turn(DD)V", at = @At("HEAD"), cancellable = true)
    private void chaosroll$lockYaw(double yawDelta, double pitchDelta, CallbackInfo ci) {
        if (!DirectionLockState.isActive()) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.player == null) return;
        if ((Object) this != mc.player) return;
        Entity self = (Entity) (Object) this;
        // Cancel rotation; force back to locked yaw, keep current pitch.
        self.setYRot(DirectionLockState.lockedYaw());
        self.yRotO = DirectionLockState.lockedYaw();
        ci.cancel();
    }
}
