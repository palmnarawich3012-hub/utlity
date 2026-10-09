package com.example.policeutility;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

/** ติดกุญแจมือ: เดินแทบไม่ได้ ตีไม่เจ็บ (การใช้ไอเทม/วางบล็อก/ตี ถูกบล็อกใน ModEvents) */
public class HandcuffedEffect extends StatusEffect {
    public HandcuffedEffect() {
        super(StatusEffectCategory.HARMFUL, 0x9E9E9E);
        addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED,
                "c2a1f4a0-5b1e-4c7a-9f3e-1d2b3c4d5e6f", -0.97,
                EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
        addAttributeModifier(EntityAttributes.GENERIC_ATTACK_DAMAGE,
                "a7d0e3b1-2c4f-4d68-8b19-5e6f7a8b9c0d", -1.0,
                EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
    }
}
