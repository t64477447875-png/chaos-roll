package com.chaosroll.client.mixin;

import com.chaosroll.client.state.NoJumpState;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Cancels {@link LivingEntity#jumpFromGround()} for the local player while {@link NoJumpState} is active.
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityJumpMixin {

    @Inject(method = "jumpFromGround", at = @At("HEAD"), cancellable = true)
    private void chaosroll$blockJump(CallbackInfo ci) {
        if (!NoJumpState.isActive()) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.player == null) return;
        if ((Object) this != mc.player) return;
        ci.cancel();
    }
}
