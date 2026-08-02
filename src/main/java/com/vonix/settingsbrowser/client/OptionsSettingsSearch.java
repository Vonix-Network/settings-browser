package com.vonix.settingsbrowser.client;

import com.vonix.settingsbrowser.SettingsBrowserMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.OptionsScreen;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Locale;

/** Adds a search field to the vanilla Minecraft Options screen. */
@Mod.EventBusSubscriber(modid = SettingsBrowserMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class OptionsSettingsSearch {
    private OptionsSettingsSearch() {}

    @SubscribeEvent
    public static void onOptionsInit(ScreenEvent.InitScreenEvent.Post event) {
        if (!(event.getScreen() instanceof OptionsScreen)) return;
        EditBox search = new EditBox(Minecraft.getInstance().font, event.getScreen().width / 2 - 100, 8, 200, 20,
                new TextComponent("Search key binds and options"));
        search.setValue("");
        search.setResponder(value -> filterOptionWidgets(event.getScreen(), value));
        event.addListener(search);
    }

    private static void filterOptionWidgets(net.minecraft.client.gui.screens.Screen screen, String query) {
        String normalized = query.trim().toLowerCase(Locale.ROOT);
        for (net.minecraft.client.gui.components.events.GuiEventListener child : screen.children()) {
            if (!(child instanceof net.minecraft.client.gui.components.AbstractWidget)) continue;
            net.minecraft.client.gui.components.AbstractWidget widget = (net.minecraft.client.gui.components.AbstractWidget) child;
            if (widget instanceof EditBox) continue;
            String label = widget.getMessage().getString().toLowerCase(Locale.ROOT);
            widget.visible = normalized.isEmpty() || label.contains(normalized);
        }
    }
}
