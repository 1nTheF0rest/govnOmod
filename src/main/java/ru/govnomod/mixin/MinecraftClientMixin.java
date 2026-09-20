package ru.govnomod.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.govnomod.GovnOmodClient;

@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin {
    @Shadow private int rightClickDelay;

    @Inject(method = "startUseItem", at = @At("HEAD"))
    private void govnomod$fastPlace(CallbackInfo ci) {
        Minecraft client = (Minecraft) (Object) this;
        LocalPlayer player = client.player;
        if (player == null) {
            return;
        }

        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof BlockItem blockItem)) {
            return;
        }

        String blockId = BuiltInRegistries.BLOCK
                .getKey(blockItem.getBlock())
                .toString();

        double cps = GovnOmodClient.CONFIG.cpsFor(blockId);
        int maxDelay = Math.max(0, (int) Math.floor(20.0 / cps));

        if (rightClickDelay > maxDelay) {
            rightClickDelay = maxDelay;
        }
    }
}
