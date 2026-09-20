package ru.govnomod.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.govnomod.GovnOmodClient;

@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin {
    @Shadow private int rightClickDelay;

    private long govnomod$nextLeftClick;
    private long govnomod$nextRightClick;

    @Inject(method = "startUseItem", at = @At("HEAD"))
    private void govnomod$fastPlace(CallbackInfo ci) {
        if (!GovnOmodClient.CONFIG.enabled) return;
        Minecraft client = (Minecraft)(Object)this;
        LocalPlayer player = client.player;
        if (player == null) return;

        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof BlockItem blockItem)) return;

        String blockId = BuiltInRegistries.BLOCK.getKey(blockItem.getBlock()).toString();
        double cps = GovnOmodClient.CONFIG.cpsFor(blockId);
        int maxDelay = Math.max(0, (int)Math.floor(20.0 / cps));
        if (rightClickDelay > maxDelay) rightClickDelay = maxDelay;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void govnomod$autoClick(CallbackInfo ci) {
        if (!GovnOmodClient.CONFIG.enabled) return;

        Minecraft client = (Minecraft)(Object)this;
        if (client.player == null || client.level == null || client.screen != null) return;

        long now = System.nanoTime();
        long window = client.getWindow().getWindow();

        boolean leftDown = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        boolean rightDown = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;

        if (!GovnOmodClient.CONFIG.holdMode) {
            leftDown = true;
            rightDown = true;
        }

        if (GovnOmodClient.CONFIG.leftEnabled && leftDown &&
                now >= govnomod$nextLeftClick) {
            ((MinecraftInvoker)(Object)client).govnomod$startAttack();
            govnomod$nextLeftClick = now + cpsDelay(GovnOmodClient.CONFIG.leftCps);
        }

        if (GovnOmodClient.CONFIG.rightEnabled && rightDown &&
                now >= govnomod$nextRightClick) {
            ((MinecraftInvoker)(Object)client).govnomod$startUseItem();
            govnomod$nextRightClick = now + cpsDelay(GovnOmodClient.CONFIG.rightCps);
        }

        if (!leftDown) govnomod$nextLeftClick = now;
        if (!rightDown) govnomod$nextRightClick = now;
    }

    private static long cpsDelay(double cps) {
        double safe = ConfigHolder.cps(cps);
        return Math.max(1L, (long)(1_000_000_000.0 / safe));
    }

    private static final class ConfigHolder {
        private static double cps(double cps) {
            return Math.max(0.1, Math.min(1000.0, Double.isFinite(cps) && cps > 0 ? cps : 20.0));
        }
    }
}
