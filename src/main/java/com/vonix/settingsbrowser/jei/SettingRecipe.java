package com.vonix.settingsbrowser.jei;

import com.vonix.settingsbrowser.config.SettingEntry;
public final class SettingRecipe {
    private final SettingEntry entry;
    public SettingRecipe(SettingEntry entry) { this.entry = entry; }
    public SettingEntry entry() { return entry; }
}
