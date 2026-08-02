package com.vonix.settingsbrowser.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.vonix.settingsbrowser.SettingsBrowserMod;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.OptionsScreen;
import net.minecraft.client.gui.screens.controls.ControlsScreen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.Locale;
import java.util.Map;

/** Search integration for vanilla options and controls, including conflict reporting. */
@Mod.EventBusSubscriber(modid = SettingsBrowserMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class SettingsSearchScreens {
    private static final Map<Screen, EditBox> SEARCH_FIELDS = new IdentityHashMap<>();
    private SettingsSearchScreens() {}

    @SubscribeEvent
    public static void init(ScreenEvent.InitScreenEvent.Post event) {
        Screen screen = event.getScreen();
        if (!(screen instanceof OptionsScreen) && !(screen instanceof ControlsScreen)) return;
        EditBox search = new EditBox(Minecraft.getInstance().font, screen.width / 2 - 120, 8, 240, 20,
                new TextComponent(screen instanceof ControlsScreen ? "Search key binds" : "Search options"));
        SEARCH_FIELDS.put(screen, search);
        search.setResponder(value -> { if (screen instanceof OptionsScreen) filterOptionWidgets(screen, value); });
        event.addListener(search);
    }

    @SubscribeEvent
    public static void draw(ScreenEvent.DrawScreenEvent.Post event) {
        Screen screen = event.getScreen();
        if (!(screen instanceof ControlsScreen)) return;
        EditBox search = SEARCH_FIELDS.get(screen);
        if (search == null || search.getValue().trim().isEmpty()) return;
        KeyMapping[] matches = matchingKeyMappings(search.getValue());
        PoseStack pose = event.getPoseStack();
        int x = Math.max(8, screen.width - 300), y = 34;
        screen.getMinecraft().font.draw(pose, new TextComponent("MATCHING KEY BINDS (" + matches.length + ")"), x, y, 0xFFFFD56A);
        int shown = Math.min(matches.length, 9);
        for (int i = 0; i < shown; i++) {
            KeyMapping key = matches[i];
            boolean conflict = conflicts(key, Minecraft.getInstance().options.keyMappings);
            String name = new TranslatableComponent(key.getName()).getString();
            String line = name + "  [" + key.getTranslatedKeyMessage().getString() + "]" + (conflict ? "  CONFLICT" : "");
            screen.getMinecraft().font.draw(pose, new TextComponent(line), x, y + 14 + i * 12, conflict ? 0xFFFF6B6B : 0xFFD7E0EA);
        }
        if (matches.length > shown) screen.getMinecraft().font.draw(pose, new TextComponent("...and " + (matches.length - shown) + " more"), x, y + 14 + shown * 12, 0xFF9AA8B8);
    }

    private static void filterOptionWidgets(Screen screen, String query) {
        String normalized = query.trim().toLowerCase(Locale.ROOT);
        for (net.minecraft.client.gui.components.events.GuiEventListener child : screen.children()) {
            if (!(child instanceof net.minecraft.client.gui.components.AbstractWidget)) continue;
            net.minecraft.client.gui.components.AbstractWidget widget = (net.minecraft.client.gui.components.AbstractWidget) child;
            if (widget instanceof EditBox) continue;
            String label = widget.getMessage().getString().toLowerCase(Locale.ROOT);
            widget.visible = normalized.isEmpty() || label.contains(normalized);
        }
    }

    public static KeyMapping[] matchingKeyMappings(String query) {
        String normalized = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        return Arrays.stream(Minecraft.getInstance().options.keyMappings)
                .filter(k -> normalized.isEmpty()
                        || new TranslatableComponent(k.getName()).getString().toLowerCase(Locale.ROOT).contains(normalized)
                        || k.getCategory().toLowerCase(Locale.ROOT).contains(normalized))
                .toArray(KeyMapping[]::new);
    }

    public static boolean conflicts(KeyMapping key, KeyMapping[] all) {
        if (key.isUnbound()) return false;
        for (KeyMapping other : all) if (other != key && key.same(other)) return true;
        return false;
    }
}
