package com.vonix.settingsbrowser.config;

import net.minecraftforge.common.ForgeConfigSpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class SettingsIndex {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec.ConfigValue<Boolean> EXAMPLE_ENABLED = BUILDER.comment("Example setting indexed by Settings Browser.").define("example.enabled", true);
    public static final ForgeConfigSpec SPEC = BUILDER.build();
    private static final List<SettingEntry> ENTRIES;
    static {
        List<SettingEntry> entries = new ArrayList<>();
        entries.add(new SettingEntry("settingsbrowser:example.enabled", "Example Enabled", "Settings Browser", "Example setting indexed by Settings Browser.", EXAMPLE_ENABLED));
        ENTRIES = Collections.unmodifiableList(entries);
    }
    private SettingsIndex() {}
    public static List<SettingEntry> entries() { return ENTRIES; }
}
