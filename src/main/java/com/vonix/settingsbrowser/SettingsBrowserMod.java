package com.vonix.settingsbrowser;

import com.vonix.settingsbrowser.config.SettingsIndex;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(SettingsBrowserMod.MOD_ID)
public final class SettingsBrowserMod {
    public static final String MOD_ID = "settingsbrowser";
    public SettingsBrowserMod() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SettingsIndex.SPEC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
    }
    private void setup(FMLCommonSetupEvent event) { }
}
