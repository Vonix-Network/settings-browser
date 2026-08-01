package com.vonix.settingsbrowser.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import com.vonix.settingsbrowser.SettingsBrowserMod;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

public final class SettingsCategory implements IRecipeCategory<SettingRecipe> {
    public static final RecipeType<SettingRecipe> TYPE = new RecipeType<>(new ResourceLocation(SettingsBrowserMod.MOD_ID, "settings"), SettingRecipe.class);
    private final IDrawable background;
    public SettingsCategory(IDrawable background) { this.background = background; }
    @Override public RecipeType<SettingRecipe> getRecipeType() { return TYPE; }
    @Override public ResourceLocation getUid() { return TYPE.getUid(); }
    @Override public Class<? extends SettingRecipe> getRecipeClass() { return SettingRecipe.class; }
    @Override public Component getTitle() { return new TextComponent("Settings"); }
    @Override public IDrawable getBackground() { return background; }
    @Override public IDrawable getIcon() { return null; }
    @Override public void setRecipe(IRecipeLayoutBuilder builder, SettingRecipe recipe, IFocusGroup focuses) { }
    @Override public void draw(SettingRecipe recipe, PoseStack pose, double mouseX, double mouseY) { }
}
