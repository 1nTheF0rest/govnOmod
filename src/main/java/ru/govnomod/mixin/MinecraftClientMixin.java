package ru.govnomod.mixin;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.govnomod.GovnOmodClient;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Shadow private int itemUseCooldown;

    @Inject(method = "tick", at = @At("HEAD"))
    private void govnomod$removeRightClickDelay(CallbackInfo ci) {
        if (GovnOmodClient.CONFIG.enabled) {
            itemUseCooldown = GovnOmodClient.CONFIG.placementDelay;
        }
    }
}
