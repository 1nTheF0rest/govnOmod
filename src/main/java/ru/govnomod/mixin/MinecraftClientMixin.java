package ru.govnomod.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.govnomod.GovnOmodClient;

@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin {
    @Shadow private int rightClickDelay;

    @Inject(method = "tick", at = @At("HEAD"))
    private void govnomod$fastBlockPlacement(CallbackInfo ci) {
        if (!GovnOmodClient.CONFIG.enabled) {
            return;
        }

        Minecraft client = (Minecraft) (Object) this;

        if (client.player == null
                || client.level == null
                || client.screen != null
                || !client.mouseHandler.isRightPressed()) {
            return;
        }

        int configuredDelay = GovnOmodClient.CONFIG.placementDelay;

        if (rightClickDelay > configuredDelay) {
            rightClickDelay = configuredDelay;
        }
    }
}
