package ru.govnomod.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.govnomod.Config;
import ru.govnomod.GovnOmodClient;

@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin {
    @Shadow private int rightClickDelay;

    private long govnomod$nextLeftClick;
    private long govnomod$nextRightClick;
    private String govnomod$lastItemId = "";

    @Inject(method = "startUseItem", at = @At("HEAD"))
    private void govnomod$adjustRightClickDelay(CallbackInfo ci) {
        if (!GovnOmodClient.CONFIG.enabled) return;

        Minecraft client = (Minecraft) (Object) this;
        if (client.player == null) return;

        ItemStack stack = client.player.getMainHandItem();
        String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
        int cps = GovnOmodClient.CONFIG.rightCpsFor(itemId);
        int maxDelay = Math.max(0, (int) Math.floor(20.0 / cps) - 1);
        if (rightClickDelay > maxDelay) rightClickDelay = maxDelay;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void govnomod$autoClick(CallbackInfo ci) {
        if (!GovnOmodClient.CONFIG.enabled) return;

        Minecraft client = (Minecraft) (Object) this;
        if (client.player == null || client.level == null || client.screen != null) return;

        ItemStack stack = client.player.getMainHandItem();
        String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();

        long now = System.nanoTime();
        if (!itemId.equals(govnomod$lastItemId)) {
            govnomod$lastItemId = itemId;
            govnomod$nextLeftClick = now;
            govnomod$nextRightClick = now;
        }

        boolean leftDown = client.mouseHandler.isLeftPressed();
        boolean rightDown = client.mouseHandler.isRightPressed();

        if (!GovnOmodClient.CONFIG.holdMode) {
            leftDown = true;
            rightDown = true;
        }

        if (GovnOmodClient.CONFIG.leftEnabled && leftDown && now >= govnomod$nextLeftClick) {
            ((MinecraftInvoker) (Object) client).govnomod$startAttack();
            govnomod$nextLeftClick = now + cpsDelay(GovnOmodClient.CONFIG.leftCpsFor(itemId));
        }

        if (GovnOmodClient.CONFIG.rightEnabled && rightDown && now >= govnomod$nextRightClick) {
            ((MinecraftInvoker) (Object) client).govnomod$startUseItem();
            govnomod$nextRightClick = now + cpsDelay(GovnOmodClient.CONFIG.rightCpsFor(itemId));
        }

        if (!leftDown) govnomod$nextLeftClick = now;
        if (!rightDown) govnomod$nextRightClick = now;
    }

    private static long cpsDelay(int cps) {
        int safe = Config.clampCps(cps);
        return Math.max(1L, Math.round(1_000_000_000.0 / safe));
    }
}
