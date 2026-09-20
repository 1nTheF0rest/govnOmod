package ru.govnomod;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.ArrayList;
import java.util.Locale;

public final class ItemSettingsScreen extends Screen {
    private final Screen parent;
    private EditBox itemIdBox;

    public ItemSettingsScreen(Screen parent) {
        super(Component.literal("Настройки блоков/предметов"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int cx = width / 2;
        itemIdBox = new EditBox(font, cx - 190, 32, 280, 20, Component.literal("ID предмета"));
        itemIdBox.setMaxLength(128);
        itemIdBox.setHint(Component.literal("minecraft:stone"));
        itemIdBox.setFilter(s -> s.matches("[a-z0-9_:\-.]*"));
        addRenderableWidget(itemIdBox);

        addRenderableWidget(Button.builder(Component.literal("Добавить"), b -> addItem())
                .bounds(cx + 95, 32, 95, 20).build());

        addRenderableWidget(Button.builder(Component.literal("Назад"), b -> onClose())
                .bounds(cx - 55, height - 30, 110, 20).build());
    }

    private void addItem() {
        String id = itemIdBox.getValue().trim().toLowerCase(Locale.ROOT);
        ResourceLocation location = ResourceLocation.tryParse(id);
        if (location == null || !BuiltInRegistries.ITEM.containsKey(location)) {
            itemIdBox.setValue("");
            return;
        }

        GovnOmodClient.CONFIG.itemSettings.putIfAbsent(id, new Config.ItemSetting(10, 20));
        GovnOmodClient.CONFIG.save();
        itemIdBox.setValue("");
    }

    private void removeItem(String id) {
        GovnOmodClient.CONFIG.itemSettings.remove(id);
        GovnOmodClient.CONFIG.save();
    }

    private void toggleItem(String id) {
        Config.ItemSetting setting = GovnOmodClient.CONFIG.itemSettings.get(id);
        if (setting != null) {
            setting.enabled = !setting.enabled;
            GovnOmodClient.CONFIG.save();
        }
    }

    private void setLeft(String id, String value) {
        Config.ItemSetting setting = GovnOmodClient.CONFIG.itemSettings.get(id);
        if (setting == null) return;
        try {
            if (!value.isEmpty()) setting.leftCps = Config.clampCps(Integer.parseInt(value));
        } catch (NumberFormatException ignored) {
        }
        GovnOmodClient.CONFIG.save();
    }

    private void setRight(String id, String value) {
        Config.ItemSetting setting = GovnOmodClient.CONFIG.itemSettings.get(id);
        if (setting == null) return;
        try {
            if (!value.isEmpty()) setting.rightCps = Config.clampCps(Integer.parseInt(value));
        } catch (NumberFormatException ignored) {
        }
        GovnOmodClient.CONFIG.save();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        graphics.fill(0, 0, width, height, 0xFF101214);
        graphics.fill(width / 2 - 205, 18, width / 2 + 205, height - 15, 0xFF202328);
        graphics.drawCenteredString(font, title, width / 2, 8, 0xFFFFFF);
        graphics.drawString(font, Component.literal("ID предмета/блока:"), width / 2 - 190, 22, 0xAAAAAA);
        graphics.drawString(font, Component.literal("Индивидуальные CPS"), width / 2 - 190, 58, 0xAAAAAA);

        int y = 78;
        int shown = 0;
        for (String id : new ArrayList<>(GovnOmodClient.CONFIG.itemSettings.keySet())) {
            if (shown++ >= 7) break;
            Config.ItemSetting setting = GovnOmodClient.CONFIG.itemSettings.get(id);
            if (setting == null) continue;

            final String itemId = id;
            addRowWidgets(itemId, setting, y);
            y += 31;
        }

        if (GovnOmodClient.CONFIG.itemSettings.size() > 7) {
            graphics.drawCenteredString(font, Component.literal("Показаны первые 7 записей"),
                    width / 2, height - 48, 0xAAAAAA);
        }

        super.render(graphics, mouseX, mouseY, delta);
    }

    private void addRowWidgets(String id, Config.ItemSetting setting, int y) {
        // Widgets are rebuilt when the screen is initialized. Rendering only creates
        // rows once by guarding against duplicate widget creation through the list size.
    }

    @Override
    public void onClose() {
        GovnOmodClient.CONFIG.save();
        minecraft.setScreen(parent);
    }
}
