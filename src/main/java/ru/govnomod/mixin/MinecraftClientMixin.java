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
    @Shadow private int rightClickDelay;

    /**
     * Vanilla applies a use cooldown after successful right-click actions.
     * When the mod is enabled, clamp that client-side cooldown every tick.
     *
     * We deliberately do not synthesize clicks or packets: vanilla still
     * performs the actual interaction, so server-side placement rules remain
     * authoritative.
     */
    @Inject(method = "tick", at = @At("HEAD"))
    private void govnomod$removeRightClickDelay(CallbackInfo ci) {
        if (GovnOmodClient.CONFIG.enabled) {
            rightClickDelay = GovnOmodClient.CONFIG.placementDelay;
        }
    }
}
