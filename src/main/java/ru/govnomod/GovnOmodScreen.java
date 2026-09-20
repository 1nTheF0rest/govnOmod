package ru.govnomod;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.Locale;

public final class GovnOmodScreen extends Screen {
    private final Screen parent;
    private CpsSlider leftSlider;
    private CpsSlider rightSlider;
    private Button enabledButton;
    private Button leftButton;
    private Button rightButton;
    private Button modeButton;

    public GovnOmodScreen(Screen parent) {
        super(Component.literal("govnOmod"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int cx = width / 2;
        int top = Math.max(35, height / 2 - 125);

        enabledButton = addRenderableWidget(Button.builder(Component.literal(enabledText()), b -> {
            GovnOmodClient.CONFIG.enabled = !GovnOmodClient.CONFIG.enabled;
            b.setMessage(Component.literal(enabledText()));
            GovnOmodClient.CONFIG.save();
        }).bounds(cx - 170, top, 340, 22).build());

        leftButton = addRenderableWidget(Button.builder(Component.literal(leftText()), b -> {
            GovnOmodClient.CONFIG.leftEnabled = !GovnOmodClient.CONFIG.leftEnabled;
            b.setMessage(Component.literal(leftText()));
            GovnOmodClient.CONFIG.save();
        }).bounds(cx - 170, top + 31, 165, 22).build());

        rightButton = addRenderableWidget(Button.builder(Component.literal(rightText()), b -> {
            GovnOmodClient.CONFIG.rightEnabled = !GovnOmodClient.CONFIG.rightEnabled;
            b.setMessage(Component.literal(rightText()));
            GovnOmodClient.CONFIG.save();
        }).bounds(cx + 5, top + 31, 165, 22).build());

        leftSlider = addRenderableWidget(new CpsSlider(
                cx - 170, top + 68, 340, 22, GovnOmodClient.CONFIG.leftCps,
                "ЛКМ CPS", value -> {
                    GovnOmodClient.CONFIG.leftCps = value;
                    GovnOmodClient.CONFIG.save();
                }));

        rightSlider = addRenderableWidget(new CpsSlider(
                cx - 170, top + 99, 340, 22, GovnOmodClient.CONFIG.rightCps,
                "ПКМ CPS", value -> {
                    GovnOmodClient.CONFIG.rightCps = value;
                    GovnOmodClient.CONFIG.save();
                }));

        modeButton = addRenderableWidget(Button.builder(Component.literal(modeText()), b -> {
            GovnOmodClient.CONFIG.holdMode = !GovnOmodClient.CONFIG.holdMode;
            b.setMessage(Component.literal(modeText()));
            GovnOmodClient.CONFIG.save();
        }).bounds(cx - 170, top + 130, 340, 22).build());

        addRenderableWidget(Button.builder(Component.literal("Настройки блоков/предметов"), b ->
                minecraft.setScreen(new ItemSettingsScreen(this))).bounds(cx - 170, top + 164, 340, 22).build());

        addRenderableWidget(Button.builder(Component.literal("Сохранить"), b -> {
            GovnOmodClient.CONFIG.save();
            b.setMessage(Component.literal("Сохранено"));
        }).bounds(cx - 82, top + 195, 164, 22).build());

        addRenderableWidget(Button.builder(Component.literal("Закрыть"), b -> onClose())
                .bounds(cx - 82, top + 224, 164, 22).build());
    }

    private static String enabledText() {
        return "Мод: " + (GovnOmodClient.CONFIG.enabled ? "ВКЛ" : "ВЫКЛ");
    }

    private static String leftText() {
        return "ЛКМ: " + (GovnOmodClient.CONFIG.leftEnabled ? "ВКЛ" : "ВЫКЛ");
    }

    private static String rightText() {
        return "ПКМ: " + (GovnOmodClient.CONFIG.rightEnabled ? "ВКЛ" : "ВЫКЛ");
    }

    private static String modeText() {
        return "Режим: " + (GovnOmodClient.CONFIG.holdMode ? "ПРИ ЗАЖАТИИ" : "ПОСТОЯННЫЙ");
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        graphics.fill(0, 0, width, height, 0xFF101214);
        graphics.fill(width / 2 - 190, height / 2 - 145, width / 2 + 190, height / 2 + 140, 0xFF202328);
        graphics.drawCenteredString(font, title, width / 2, height / 2 - 139, 0xFFFFFF);
        graphics.drawCenteredString(font, Component.literal("govnOmod • Fabric 1.21.11"),
                width / 2, height / 2 - 122, 0xAAAAAA);
        super.render(graphics, mouseX, mouseY, delta);
    }

    @Override
    public void onClose() {
        GovnOmodClient.CONFIG.save();
        minecraft.setScreen(parent);
    }

    private static final class CpsSlider extends AbstractSliderButton {
        private final String label;
        private final java.util.function.IntConsumer consumer;

        private CpsSlider(int x, int y, int width, int height, int cps, String label,
                          java.util.function.IntConsumer consumer) {
            super(x, y, width, height, Component.literal(label), (Config.clampCps(cps) - 1) / 199.0);
            this.label = label;
            this.consumer = consumer;
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            int cps = valueToCps();
            setMessage(Component.literal(label + ": " + cps));
        }

        @Override
        protected void applyValue() {
            int cps = valueToCps();
            consumer.accept(cps);
        }

        private int valueToCps() {
            return Config.clampCps((int) Math.round(1.0 + value * 199.0));
        }
    }
}
