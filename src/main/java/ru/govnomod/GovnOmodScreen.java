package ru.govnomod;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
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
        int cx = width / 2;
        int top = Math.max(45, height / 2 - 75);

        addRenderableWidget(Button.builder(
                Component.literal(enabledText()),
                button -> {
                    GovnOmodClient.CONFIG.enabled =
                            !GovnOmodClient.CONFIG.enabled;
                    GovnOmodClient.CONFIG.save();
                    button.setMessage(Component.literal(enabledText()));
                }).bounds(cx - 160, top, 320, 22).build());

        addRenderableWidget(new DelaySlider(
                cx - 160,
                top + 38,
                320,
                22,
                GovnOmodClient.CONFIG.placementDelay));

        addRenderableWidget(Button.builder(Component.literal("Сохранить"),
                button -> {
                    GovnOmodClient.CONFIG.save();
                    button.setMessage(Component.literal("Сохранено"));
                }).bounds(cx - 75, top + 78, 150, 22).build());

        addRenderableWidget(Button.builder(Component.literal("Закрыть"),
                button -> onClose()).bounds(cx - 75, top + 108, 150, 22).build());
    }

    private static String enabledText() {
        return "Говномод: "
                + (GovnOmodClient.CONFIG.enabled ? "ВКЛ" : "ВЫКЛ");
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        graphics.fill(0, 0, width, height, 0xFF101214);
        graphics.fill(width / 2 - 180, height / 2 - 95,
                width / 2 + 180, height / 2 + 145, 0xFF202328);

        graphics.drawCenteredString(
                font, title, width / 2, height / 2 - 87, 0xFFFFFF);

        graphics.drawCenteredString(
                font,
                Component.literal("O — настройки | V — включение"),
                width / 2, height / 2 - 67, 0xAAAAAA);

        graphics.drawCenteredString(
                font,
                Component.literal("ПКМ: ускоренная установка блоков"),
                width / 2, height / 2 + 42, 0xCCCCCC);

        super.render(graphics, mouseX, mouseY, delta);
    }

    @Override
    public void onClose() {
        GovnOmodClient.CONFIG.save();
        minecraft.setScreen(parent);
    }

    private static final class DelaySlider extends AbstractSliderButton {
        private DelaySlider(int x, int y, int width, int height, int delay) {
            super(x, y, width, height,
                    Component.empty(),
                    Config.clampDelay(delay) / 20.0);
            updateMessage();
        }

        private int delay() {
            return Config.clampDelay((int) Math.round(value * 20.0));
        }

        @Override
        protected void updateMessage() {
            setMessage(Component.literal(
                    "Задержка установки: " + delay() + " тиков"));
        }

        @Override
        protected void applyValue() {
            GovnOmodClient.CONFIG.placementDelay = delay();
        }
    }
}
