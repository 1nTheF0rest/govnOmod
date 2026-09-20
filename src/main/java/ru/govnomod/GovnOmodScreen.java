package ru.govnomod;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class GovnOmodScreen extends Screen {
    private final Screen parent;
    private EditBox leftCps;
    private EditBox rightCps;
    private Button enabledButton;
    private Button modeButton;
    private Button leftButton;
    private Button rightButton;

    public GovnOmodScreen(Screen parent) {
        super(Component.literal("Говномод"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int cx = width / 2;
        int y = height / 2 - 105;

        enabledButton = addRenderableWidget(Button.builder(Component.literal(enabledText()), b -> {
            GovnOmodClient.CONFIG.enabled = !GovnOmodClient.CONFIG.enabled;
            GovnOmodClient.CONFIG.save();
            b.setMessage(Component.literal(enabledText()));
        }).bounds(cx - 160, y, 320, 20).build());

        modeButton = addRenderableWidget(Button.builder(Component.literal(modeText()), b -> {
            GovnOmodClient.CONFIG.holdMode = !GovnOmodClient.CONFIG.holdMode;
            GovnOmodClient.CONFIG.save();
            b.setMessage(Component.literal(modeText()));
        }).bounds(cx - 160, y + 28, 320, 20).build());

        leftButton = addRenderableWidget(Button.builder(Component.literal(leftText()), b -> {
            GovnOmodClient.CONFIG.leftEnabled = !GovnOmodClient.CONFIG.leftEnabled;
            GovnOmodClient.CONFIG.save();
            b.setMessage(Component.literal(leftText()));
        }).bounds(cx - 160, y + 56, 150, 20).build());

        rightButton = addRenderableWidget(Button.builder(Component.literal(rightText()), b -> {
            GovnOmodClient.CONFIG.rightEnabled = !GovnOmodClient.CONFIG.rightEnabled;
            GovnOmodClient.CONFIG.save();
            b.setMessage(Component.literal(rightText()));
        }).bounds(cx + 10, y + 56, 150, 20).build());

        leftCps = new EditBox(font, cx - 160, y + 100, 150, 20, Component.literal("ЛКМ CPS"));
        leftCps.setValue(format(GovnOmodClient.CONFIG.leftCps));
        leftCps.setHint(Component.literal("ЛКМ CPS"));
        leftCps.setResponder(v -> saveCps());
        addRenderableWidget(leftCps);

        rightCps = new EditBox(font, cx + 10, y + 100, 150, 20, Component.literal("ПКМ CPS"));
        rightCps.setValue(format(GovnOmodClient.CONFIG.rightCps));
        rightCps.setHint(Component.literal("ПКМ CPS"));
        rightCps.setResponder(v -> saveCps());
        addRenderableWidget(rightCps);
    }

    private void saveCps() {
        GovnOmodClient.CONFIG.leftCps = parse(leftCps == null ? "" : leftCps.getValue(), GovnOmodClient.CONFIG.leftCps);
        GovnOmodClient.CONFIG.rightCps = parse(rightCps == null ? "" : rightCps.getValue(), GovnOmodClient.CONFIG.rightCps);
        GovnOmodClient.CONFIG.save();
    }

    private static double parse(String value, double fallback) {
        try { return Config.sanitize(Double.parseDouble(value.replace(',', '.')), fallback); }
        catch (NumberFormatException ignored) { return fallback; }
    }

    private static String format(double value) {
        return String.format(java.util.Locale.ROOT, "%.1f", value);
    }

    private static String enabledText() {
        return "Говномод: " + (GovnOmodClient.CONFIG.enabled ? "ВКЛ" : "ВЫКЛ");
    }

    private static String modeText() {
        return "Режим: " + (GovnOmodClient.CONFIG.holdMode ? "ПРИ ЗАЖАТИИ" : "ПОСТОЯННЫЙ");
    }

    private static String leftText() {
        return "ЛКМ: " + (GovnOmodClient.CONFIG.leftEnabled ? "ВКЛ" : "ВЫКЛ");
    }

    private static String rightText() {
        return "ПКМ: " + (GovnOmodClient.CONFIG.rightEnabled ? "ВКЛ" : "ВЫКЛ");
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        graphics.fill(0, 0, width, height, 0xFF101010);
        graphics.drawCenteredString(font, title, width / 2, height / 2 - 140, 0xFFFFFF);
        graphics.drawCenteredString(font, Component.literal("CPS задаётся отдельно для ЛКМ и ПКМ"),
                width / 2, height / 2 - 125, 0xAAAAAA);
        graphics.drawCenteredString(font, Component.literal("Создатель: Фуня"),
                width / 2, height / 2 + 50, 0xFFFFFF);
        super.render(graphics, mouseX, mouseY, delta);
    }

    @Override
    public void onClose() {
        saveCps();
        minecraft.setScreen(parent);
    }
}
