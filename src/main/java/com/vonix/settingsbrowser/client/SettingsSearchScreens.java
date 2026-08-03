package com.vonix.settingsbrowser.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.vonix.settingsbrowser.SettingsBrowserMod;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.controls.KeyBindsScreen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Adds a readable bottom-left key-bind finder only to the dedicated Key Binds screen. */
@Mod.EventBusSubscriber(modid = SettingsBrowserMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class SettingsSearchScreens {
    private static final int PANEL_WIDTH = 300;
    private static final int PANEL_HEIGHT = 140;
    private static final int PANEL_X = 8;
    private static final int PANEL_BOTTOM_GAP = 38;
    private static final int RESULT_ROWS = 4;

    private static final Map<Screen, EditBox> SEARCH_FIELDS = new IdentityHashMap<>();
    private static final Map<Screen, Filter> FILTERS = new IdentityHashMap<>();
    private static final Map<Screen, Button[]> FILTER_BUTTONS = new IdentityHashMap<>();

    private enum Filter {
        ALL,
        CONFLICTS,
        UNBOUND
    }

    private SettingsSearchScreens() {}

    @SubscribeEvent
    public static void init(ScreenEvent.InitScreenEvent.Post event) {
        Screen screen = event.getScreen();
        if (!(screen instanceof KeyBindsScreen)) return;

        int panelY = panelY(screen);
        EditBox search = new EditBox(Minecraft.getInstance().font, PANEL_X + 8, panelY + 110, 238, 20,
                new TextComponent("Find a key bind"));
        search.setMaxLength(80);
        search.setSuggestion("name, key, category, or mod");
        search.setResponder(value -> FILTERS.putIfAbsent(screen, Filter.ALL));
        SEARCH_FIELDS.put(screen, search);
        FILTERS.put(screen, Filter.ALL);
        event.addListener(search);

        Button[] buttons = new Button[4];
        buttons[0] = new Button(PANEL_X + 8, panelY + 84, 58, 20, new TextComponent("All"),
                button -> setFilter(screen, Filter.ALL));
        buttons[1] = new Button(PANEL_X + 70, panelY + 84, 92, 20, new TextComponent("Conflicts"),
                button -> setFilter(screen, Filter.CONFLICTS));
        buttons[2] = new Button(PANEL_X + 166, panelY + 84, 82, 20, new TextComponent("Unbound"),
                button -> setFilter(screen, Filter.UNBOUND));
        buttons[3] = new Button(PANEL_X + 252, panelY + 110, 40, 20, new TextComponent("Clear"),
                button -> {
                    search.setValue("");
                    search.setFocus(true);
                });
        FILTER_BUTTONS.put(screen, buttons);
        for (Button button : buttons) event.addListener(button);
    }

    private static void setFilter(Screen screen, Filter filter) {
        FILTERS.put(screen, filter);
        EditBox search = SEARCH_FIELDS.get(screen);
        if (search != null) search.setFocus(false);
    }

    @SubscribeEvent
    public static void draw(ScreenEvent.DrawScreenEvent.Post event) {
        Screen screen = event.getScreen();
        if (!(screen instanceof KeyBindsScreen)) return;

        EditBox search = SEARCH_FIELDS.get(screen);
        if (search == null) return;

        KeyMapping[] matches = matchingKeyMappings(search.getValue(), FILTERS.getOrDefault(screen, Filter.ALL));
        PoseStack pose = event.getPoseStack();
        int x = PANEL_X;
        int y = panelY(screen);
        int panelRight = x + PANEL_WIDTH;
        GuiComponent.fill(pose, x, y, panelRight, y + PANEL_HEIGHT, 0xF0101520);
        GuiComponent.fill(pose, x, y, panelRight, y + 1, 0xFFFFD56A);
        GuiComponent.fill(pose, x, y + 22 + Math.min(matches.length, RESULT_ROWS) * 14, panelRight, y + 23 + Math.min(matches.length, RESULT_ROWS) * 14, 0xFF354252);

        Minecraft minecraft = screen.getMinecraft();
        minecraft.font.draw(pose, new TextComponent("FIND KEY BINDS"), x + 10, y + 8, 0xFFFFD56A);
        String count = matches.length + " result" + (matches.length == 1 ? "" : "s");
        minecraft.font.draw(pose, new TextComponent(count), panelRight - 10 - minecraft.font.width(count), y + 8, 0xFFB8C4D2);

        int rows = Math.min(matches.length, RESULT_ROWS);
        for (int i = 0; i < rows; i++) {
            KeyMapping key = matches[i];
            boolean conflict = conflicts(key, minecraft.options.keyMappings);
            String name = displayName(key);
            int maxNameWidth = PANEL_WIDTH - 96;
            if (minecraft.font.width(name) > maxNameWidth) {
                name = minecraft.font.plainSubstrByWidth(name, maxNameWidth - 10) + "...";
            }
            int rowY = y + 26 + i * 14;
            minecraft.font.draw(pose, new TextComponent(name), x + 10, rowY,
                    conflict ? 0xFFFF8888 : 0xFFF0F4F8);
            String assigned = key.isUnbound() ? "UNBOUND" : key.getTranslatedKeyMessage().getString();
            if (minecraft.font.width(assigned) > 78) assigned = minecraft.font.plainSubstrByWidth(assigned, 78);
            minecraft.font.draw(pose, new TextComponent(assigned), panelRight - 10 - minecraft.font.width(assigned), rowY,
                    key.isUnbound() ? 0xFF9EABB9 : 0xFFE5C95B);
        }

        if (matches.length == 0) {
            minecraft.font.draw(pose, new TextComponent("No key binds found."), x + 10, y + 31, 0xFFB8C4D2);
        } else if (matches.length > rows) {
            String more = "...and " + (matches.length - rows) + " more — refine your search";
            minecraft.font.draw(pose, new TextComponent(more), x + 10, y + 26 + rows * 14, 0xFF9EABB9);
        }

        String hint = "Click a result to jump to it";
        minecraft.font.draw(pose, new TextComponent(hint), x + 10, y + 72, 0xFF8F9BAA);

        // Draw these controls once more over the opaque panel so they remain readable above the key-bind list.
        search.render(pose, event.getMouseX(), event.getMouseY(), event.getPartialTicks());
        Button[] buttons = FILTER_BUTTONS.get(screen);
        if (buttons != null) {
            for (Button button : buttons) button.render(pose, event.getMouseX(), event.getMouseY(), event.getPartialTicks());
        }
    }

    @SubscribeEvent
    public static void click(ScreenEvent.MouseClickedEvent.Pre event) {
        Screen screen = event.getScreen();
        if (!(screen instanceof KeyBindsScreen) || event.getButton() != GLFW.GLFW_MOUSE_BUTTON_1) return;

        EditBox search = SEARCH_FIELDS.get(screen);
        if (search == null) return;
        KeyMapping[] matches = matchingKeyMappings(search.getValue(), FILTERS.getOrDefault(screen, Filter.ALL));
        int y = panelY(screen);
        int rowStart = y + 22;
        int rowEnd = rowStart + Math.min(matches.length, RESULT_ROWS) * 14;
        if (event.getMouseX() < PANEL_X || event.getMouseX() > PANEL_X + PANEL_WIDTH
                || event.getMouseY() < rowStart || event.getMouseY() >= rowEnd) return;

        int index = (int) ((event.getMouseY() - rowStart) / 14);
        if (index >= 0 && index < matches.length) {
            selectKeyBinding((KeyBindsScreen) screen, matches[index]);
            event.setCanceled(true);
        }
    }

    /** Ctrl+F focuses the search box without leaving the vanilla Key Binds screen. */
    @SubscribeEvent
    public static void keyboard(ScreenEvent.KeyboardKeyPressedEvent.Pre event) {
        Screen screen = event.getScreen();
        if (!(screen instanceof KeyBindsScreen) || event.getKeyCode() != GLFW.GLFW_KEY_F
                || (event.getModifiers() & GLFW.GLFW_MOD_CONTROL) == 0) return;
        EditBox search = SEARCH_FIELDS.get(screen);
        if (search != null) {
            search.setFocus(true);
            search.moveCursorToEnd();
            event.setCanceled(true);
        }
    }

    public static KeyMapping[] matchingKeyMappings(String query) {
        return matchingKeyMappings(query, Filter.ALL);
    }

    private static KeyMapping[] matchingKeyMappings(String query, Filter filter) {
        String normalized = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        KeyMapping[] all = Minecraft.getInstance().options.keyMappings;
        return Arrays.stream(all)
                .filter(key -> filter == Filter.ALL
                        || (filter == Filter.CONFLICTS && conflicts(key, all))
                        || (filter == Filter.UNBOUND && key.isUnbound()))
                .filter(key -> normalized.isEmpty() || searchableText(key).contains(normalized))
                .toArray(KeyMapping[]::new);
    }

    private static String searchableText(KeyMapping key) {
        return (displayName(key) + " " + key.getName() + " " + key.getCategory() + " "
                + key.getTranslatedKeyMessage().getString() + " " + key.getKey().getName()).toLowerCase(Locale.ROOT);
    }

    private static String displayName(KeyMapping key) {
        return new TranslatableComponent(key.getName()).getString();
    }

    public static boolean conflicts(KeyMapping key, KeyMapping[] all) {
        if (key.isUnbound()) return false;
        for (KeyMapping other : all) if (other != key && key.same(other)) return true;
        return false;
    }

    private static int panelY(Screen screen) {
        return Math.max(30, screen.height - PANEL_HEIGHT - PANEL_BOTTOM_GAP);
    }

    /** Selects and centers the vanilla row; reflection is isolated and fails safely if mappings change. */
    private static void selectKeyBinding(KeyBindsScreen screen, KeyMapping key) {
        screen.selectedKey = key;
        try {
            Field listField = KeyBindsScreen.class.getDeclaredField("keyBindsList");
            listField.setAccessible(true);
            Object list = listField.get(screen);
            Method childrenMethod = list.getClass().getMethod("children");
            List<?> entries = (List<?>) childrenMethod.invoke(list);
            Object matchingEntry = null;
            for (Object entry : entries) {
                if (findKeyMapping(entry) == key) {
                    matchingEntry = entry;
                    break;
                }
            }
            if (matchingEntry == null) return;

            Class<?> selectionList = Class.forName("net.minecraft.client.gui.components.AbstractSelectionList");
            Method center = selectionList.getDeclaredMethod("centerScrollOn", Class.forName("net.minecraft.client.gui.components.AbstractSelectionList$Entry"));
            center.setAccessible(true);
            center.invoke(list, matchingEntry);
        } catch (ReflectiveOperationException ignored) {
            // The selected key still receives vanilla's normal highlight even if a future Forge mapping changes.
        }
    }

    private static KeyMapping findKeyMapping(Object entry) throws IllegalAccessException {
        for (Class<?> type = entry.getClass(); type != null; type = type.getSuperclass()) {
            for (Field field : type.getDeclaredFields()) {
                if (!KeyMapping.class.isAssignableFrom(field.getType())) continue;
                field.setAccessible(true);
                return (KeyMapping) field.get(entry);
            }
        }
        return null;
    }
}
