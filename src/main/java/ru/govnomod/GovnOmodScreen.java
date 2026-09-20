package ru.govnomod;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class GovnOmodScreen extends Screen {
    private final Screen parent;

    public GovnOmodScreen(Screen parent) {
        super(Component.literal("Говномод"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        addRenderableWidget(net.minecraft.client.gui.components.Button.builder(
                Component.literal(configText()),
                button -> {
                    GovnOmodClient.CONFIG.enabled = !GovnOmodClient.CONFIG.enabled;
                    GovnOmodClient.CONFIG.save();
                    button.setMessage(Component.literal(configText()));
                }
        ).bounds(width / 2 - 100, height / 2 - 10, 200, 20).build());
    }

    private static String configText() {
        return "Говномод: " + (GovnOmodClient.CONFIG.enabled ? "ВКЛ" : "ВЫКЛ");
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        graphics.fill(0, 0, width, height, 0xFF101010);
        graphics.drawCenteredString(font, title, width / 2, height / 2 - 45, 0xFFFFFF);
        graphics.drawCenteredString(font, Component.literal("Создатель: Фуня"),
                width / 2, height / 2 + 25, 0xFFFFFF);
        super.render(graphics, mouseX, mouseY, delta);
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parent);
    }
}
