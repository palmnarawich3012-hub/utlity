package com.example.policeutility;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.util.Identifier;

public class ModEffects {
    public static StatusEffect HANDCUFFED;

    public static void register() {
        HANDCUFFED = Registry.register(Registries.STATUS_EFFECT,
                new Identifier(PoliceUtilityMod.MOD_ID, "handcuffed"), new HandcuffedEffect());
    }
}
