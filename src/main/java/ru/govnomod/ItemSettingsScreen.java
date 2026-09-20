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
        itemIdBox.setFilter(s -> s.matches("[a-z0-9_:.\\-]*"));
        addRenderableWidget(itemIdBox);

        addRenderableWidget(Button.builder(Component.literal("Добавить"), b -> addItem())
                .bounds(cx + 95, 32, 95, 20).build());

        addRenderableWidget(Button.builder(Component.literal("Назад"), b -> onClose())
                .bounds(cx - 55, height - 30, 110, 20).build());

        buildRows();
    }

    private void buildRows() {
        int y = 78;
        int shown = 0;
        for (String id : new ArrayList<>(GovnOmodClient.CONFIG.itemSettings.keySet())) {
            if (shown++ >= 7) break;
            Config.ItemSetting setting = GovnOmodClient.CONFIG.itemSettings.get(id);
            if (setting == null) continue;
            addRow(id, setting, y);
            y += 31;
        }
    }

    private void addRow(String id, Config.ItemSetting setting, int y) {
        int cx = width / 2;
        final String itemId = id;

        addRenderableWidget(Button.builder(Component.literal(shortId(itemId) + (setting.enabled ? " [ВКЛ]" : " [ВЫКЛ]")),
                b -> {
                    toggleItem(itemId);
                    Config.ItemSetting s = GovnOmodClient.CONFIG.itemSettings.get(itemId);
                    if (s != null) {
                        b.setMessage(Component.literal(shortId(itemId) + (s.enabled ? " [ВКЛ]" : " [ВЫКЛ]")));
                    }
                }).bounds(cx - 200, y, 150, 20).build());

        EditBox left = new EditBox(font, cx - 43, y, 55, 20, Component.literal("ЛКМ"));
        left.setMaxLength(3);
        left.setValue(Integer.toString(Config.clampCps(setting.leftCps)));
        left.setFilter(s -> s.matches("\\d{0,3}"));
        left.setResponder(v -> setLeft(itemId, v));
        addRenderableWidget(left);

        EditBox right = new EditBox(font, cx + 17, y, 55, 20, Component.literal("ПКМ"));
        right.setMaxLength(3);
        right.setValue(Integer.toString(Config.clampCps(setting.rightCps)));
        right.setFilter(s -> s.matches("\\d{0,3}"));
        right.setResponder(v -> setRight(itemId, v));
        addRenderableWidget(right);

        addRenderableWidget(Button.builder(Component.literal("Удалить"), b -> {
            removeItem(itemId);
            clearAndInit();
        }).bounds(cx + 80, y, 90, 20).build());
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
        clearAndInit();
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
        if (setting == null || value.isEmpty()) return;
        try {
            setting.leftCps = Config.clampCps(Integer.parseInt(value));
            GovnOmodClient.CONFIG.save();
        } catch (NumberFormatException ignored) {
        }
    }

    private void setRight(String id, String value) {
        Config.ItemSetting setting = GovnOmodClient.CONFIG.itemSettings.get(id);
        if (setting == null || value.isEmpty()) return;
        try {
            setting.rightCps = Config.clampCps(Integer.parseInt(value));
            GovnOmodClient.CONFIG.save();
        } catch (NumberFormatException ignored) {
        }
    }

    private static String shortId(String id) {
        return id.length() <= 20 ? id : id.substring(0, 17) + "...";
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        graphics.fill(0, 0, width, height, 0xFF101214);
        graphics.fill(width / 2 - 210, 18, width / 2 + 210, height - 15, 0xFF202328);
        graphics.drawCenteredString(font, title, width / 2, 8, 0xFFFFFF);
        graphics.drawString(font, Component.literal("ID предмета/блока:"), width / 2 - 190, 22, 0xAAAAAA);
        graphics.drawString(font, Component.literal("ЛКМ"), width / 2 - 38, 66, 0xAAAAAA);
        graphics.drawString(font, Component.literal("ПКМ"), width / 2 + 22, 66, 0xAAAAAA);

        if (GovnOmodClient.CONFIG.itemSettings.isEmpty()) {
            graphics.drawCenteredString(font, Component.literal("Добавь ID, например minecraft:stone"),
                    width / 2, 100, 0xAAAAAA);
        } else if (GovnOmodClient.CONFIG.itemSettings.size() > 7) {
            graphics.drawCenteredString(font, Component.literal("Показаны первые 7 записей"),
                    width / 2, height - 48, 0xAAAAAA);
        }

        super.render(graphics, mouseX, mouseY, delta);
    }

    @Override
    public void onClose() {
        GovnOmodClient.CONFIG.save();
        minecraft.setScreen(parent);
    }
}
