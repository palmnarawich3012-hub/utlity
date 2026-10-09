package com.example.policeutility;

import net.fabricmc.api.ModInitializer;

public class PoliceUtilityMod implements ModInitializer {
    public static final String MOD_ID = "policeutility";

    @Override
    public void onInitialize() {
        ModEffects.register();
        ModItems.register();
        ModEvents.register();
        FlashlightManager.register();
        TaserLaser.register();
        RadioItem.registerChat();
    }
}
