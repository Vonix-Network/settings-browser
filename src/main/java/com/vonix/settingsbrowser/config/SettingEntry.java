package com.vonix.settingsbrowser.config;

import net.minecraftforge.common.ForgeConfigSpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class SettingEntry {
    private final String id, name, category, description;
    private final ForgeConfigSpec.ConfigValue<?> value;
    public SettingEntry(String id, String name, String category, String description, ForgeConfigSpec.ConfigValue<?> value) {
        this.id=id; this.name=name; this.category=category; this.description=description; this.value=value;
    }
    public String id() { return id; }
    public String name() { return name; }
    public String category() { return category; }
    public String description() { return description; }
    public Object currentValue() { return value.get(); }
    public void toggle() { if (value.get() instanceof Boolean) { @SuppressWarnings("unchecked") ForgeConfigSpec.ConfigValue<Boolean> bool = (ForgeConfigSpec.ConfigValue<Boolean>) value; bool.set(!bool.get()); bool.save(); } }
}
