package com.vonix.settingsbrowser.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.vonix.settingsbrowser.config.SettingEntry;
import com.vonix.settingsbrowser.config.SettingsIndex;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.TextComponent;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class SettingsScreen extends Screen {
    private EditBox search;
    private final List<SettingEntry> filtered = new ArrayList<>();
    public SettingsScreen() { super(new TextComponent("Settings Browser")); }
    @Override protected void init() {
        search = new EditBox(font, width / 2 - 140, 18, 280, 20, new TextComponent("Search settings"));
        search.setResponder(s -> refresh()); addRenderableWidget(search);
        addRenderableWidget(new Button(width / 2 - 35, height - 28, 70, 20, new TextComponent("Close"), b -> onClose()));
        refresh();
    }
    private void refresh() {
        filtered.clear(); String q = search == null ? "" : search.getValue().toLowerCase(Locale.ROOT);
        for (SettingEntry e : SettingsIndex.entries()) if (q.isEmpty() || (e.name()+" "+e.category()+" "+e.description()).toLowerCase(Locale.ROOT).contains(q)) filtered.add(e);
    }
    @Override public void render(PoseStack p, int mx, int my, float partial) {
        renderBackground(p); GuiComponent.fill(p, 0, 0, width, height, 0xE9101420);
        drawCenteredString(p, font, new TextComponent("SETTINGS BROWSER"), width / 2, 4, 0xFFFFD56A);
        int y=52; for (SettingEntry e : filtered) { GuiComponent.fill(p, 30, y-3, width-30, y+28, 0xFF1B2432); drawString(p,font,new TextComponent(e.name()),42,y+2,0xFFFFFFFF); drawString(p,font,new TextComponent(e.category()+"  |  "+e.description()),42,y+15,0xFF9EAFC2); drawString(p,font,new TextComponent(currentValue(e)), width - 100, y + 8, 0xFFE5C95B); y+=36; }
        if (filtered.isEmpty()) drawCenteredString(p,font,new TextComponent("No settings match your search."),width/2,height/2,0xFFB8C4D2);
        super.render(p,mx,my,partial);
    }
    private String currentValue(SettingEntry e) { Object value = e.currentValue(); return value == null ? "(unset)" : String.valueOf(value); }
    @Override public boolean mouseClicked(double x, double y, int button) {
        if (button == 0 && x >= 30 && x <= width - 30 && y >= 49) {
            int index = (int)((y - 49) / 36);
            if (index >= 0 && index < filtered.size()) {
                SettingEntry entry = filtered.get(index);
                if (entry.currentValue() instanceof Boolean) { entry.toggle(); return true; }
            }
        }
        return super.mouseClicked(x, y, button);
    }
    @Override public boolean isPauseScreen() { return false; }
}
