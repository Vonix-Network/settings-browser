package com.vonix.settingsbrowser.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.vonix.settingsbrowser.SettingsBrowserMod;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.controls.KeyBindsScreen;
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

/** Adds a compact, vanilla-styled search and conflict summary only to Controls. */
@Mod.EventBusSubscriber(modid = SettingsBrowserMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class SettingsSearchScreens {
    private static final Map<Screen, EditBox> SEARCH_FIELDS = new IdentityHashMap<>();
    private SettingsSearchScreens() {}

    @SubscribeEvent
    public static void init(ScreenEvent.InitScreenEvent.Post event) {
        Screen screen = event.getScreen();
        if (!(screen instanceof KeyBindsScreen)) return;
        int width = screen.width;
        EditBox search = new EditBox(Minecraft.getInstance().font, width - 178, 4, 168, 20,
                new TextComponent("Search key binds"));
        SEARCH_FIELDS.put(screen, search);
        event.addListener(search);
    }

    @SubscribeEvent
    public static void draw(ScreenEvent.DrawScreenEvent.Post event) {
        Screen screen = event.getScreen();
        if (!(screen instanceof KeyBindsScreen)) return;
        EditBox search = SEARCH_FIELDS.get(screen);
        if (search == null || search.getValue().trim().isEmpty()) return;

        KeyMapping[] matches = matchingKeyMappings(search.getValue());
        PoseStack pose = event.getPoseStack();
        int panelWidth = 220;
        int x = screen.width - panelWidth - 8;
        int y = 30;
        int rows = Math.min(matches.length, 8);
        int panelHeight = 28 + rows * 14 + (matches.length > rows ? 14 : 0);

        // The panel uses the same dark translucent treatment as vanilla list surfaces.
        net.minecraft.client.gui.GuiComponent.fill(pose, x, y, x + panelWidth, y + panelHeight, 0xE6101520);
        net.minecraft.client.gui.GuiComponent.fill(pose, x, y, x + panelWidth, y + 1, 0xFF6B7280);
        screen.getMinecraft().font.draw(pose, new TextComponent("KEY BIND SEARCH"), x + 8, y + 8, 0xFFE0E0E0);
        screen.getMinecraft().font.draw(pose, new TextComponent(matches.length + " match" + (matches.length == 1 ? "" : "es")), x + panelWidth - 58, y + 8, 0xFF9EABB9);

        for (int i = 0; i < rows; i++) {
            KeyMapping key = matches[i];
            boolean conflict = conflicts(key, Minecraft.getInstance().options.keyMappings);
            String name = new TranslatableComponent(key.getName()).getString();
            String line = name + "  [" + key.getTranslatedKeyMessage().getString() + "]";
            screen.getMinecraft().font.draw(pose, new TextComponent(line), x + 8, y + 24 + i * 14, conflict ? 0xFFFF7777 : 0xFFD7E0EA);
            if (conflict) screen.getMinecraft().font.draw(pose, new TextComponent("CONFLICT"), x + panelWidth - 55, y + 24 + i * 14, 0xFFFF7777);
        }
        if (matches.length > rows) screen.getMinecraft().font.draw(pose, new TextComponent("...and " + (matches.length - rows) + " more"), x + 8, y + 24 + rows * 14, 0xFF9AA8B8);
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
