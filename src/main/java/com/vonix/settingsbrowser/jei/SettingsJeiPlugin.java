package com.vonix.settingsbrowser.jei;

import com.vonix.settingsbrowser.SettingsBrowserMod;
import com.vonix.settingsbrowser.config.SettingsIndex;
import mezz.jei.api.IModPlugin;

import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.helpers.IJeiHelpers;
import java.util.stream.Collectors;
import net.minecraft.resources.ResourceLocation;

@mezz.jei.api.JeiPlugin
public final class SettingsJeiPlugin implements IModPlugin {
    private static final ResourceLocation UID = new ResourceLocation(SettingsBrowserMod.MOD_ID, "jei_plugin");
    @Override public ResourceLocation getPluginUid() { return UID; }
    @Override public void registerCategories(IRecipeCategoryRegistration registration) {
        IJeiHelpers helpers = registration.getJeiHelpers();
        registration.addRecipeCategories(new SettingsCategory(helpers.getGuiHelper().createBlankDrawable(160, 60)));
    }
    @Override public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(SettingsIndex.entries().stream().map(SettingRecipe::new).collect(Collectors.toList()), SettingsCategory.TYPE.getUid());
    }
}
