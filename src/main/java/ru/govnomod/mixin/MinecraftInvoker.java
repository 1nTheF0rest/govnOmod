package ru.govnomod.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Minecraft.class)
public interface MinecraftInvoker {
    @Invoker("startAttack")
    boolean govnomod$startAttack();

    @Invoker("startUseItem")
    void govnomod$startUseItem();
}
