package ru.govnomod;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public final class GovnOmodClient implements ClientModInitializer {
    public static final String MOD_ID = "govnomod";
    public static final Config CONFIG = Config.load();

    private static final KeyMapping.Category KEY_CATEGORY =
            KeyMapping.Category.create(Identifier.of(MOD_ID, "keys"));

    public static KeyMapping TOGGLE_KEY;
    public static KeyMapping MENU_KEY;

    @Override
    public void onInitializeClient() {
        TOGGLE_KEY = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.govnomod.toggle",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                KEY_CATEGORY
        ));

        MENU_KEY = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.govnomod.menu",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                KEY_CATEGORY
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (TOGGLE_KEY.consumeClick()) {
                CONFIG.enabled = !CONFIG.enabled;
                CONFIG.save();
                if (client.player != null) {
                    client.player.displayClientMessage(
                            net.minecraft.network.chat.Component.literal(
                                    "Говномод: " + (CONFIG.enabled ? "ВКЛЮЧЕН" : "ВЫКЛЮЧЕН")
                            ),
                            true
                    );
                }
            }

            while (MENU_KEY.consumeClick()) {
                client.setScreen(new GovnOmodScreen(client.screen));
            }
        });

        CONFIG.save();
    }
}
