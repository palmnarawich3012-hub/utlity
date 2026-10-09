package com.example.policeutility;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

/** คลิกขวาที่ผู้เล่น/มอนสเตอร์เพื่อใส่กุญแจมือ คลิกซ้ำเพื่อปลด */
public class HandcuffsItem extends Item {
    public HandcuffsItem(Settings settings) { super(settings); }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity target, Hand hand) {
        if (user.getWorld().isClient) return ActionResult.SUCCESS;
        boolean already = target.hasStatusEffect(ModEffects.HANDCUFFED);
        if (already) {
            target.removeStatusEffect(ModEffects.HANDCUFFED);
            user.sendMessage(Text.literal("ปลดกุญแจมือแล้ว"), true);
            if (target instanceof PlayerEntity p) p.sendMessage(Text.literal("คุณถูกปลดกุญแจมือ"), true);
        } else {
            target.addStatusEffect(new StatusEffectInstance(ModEffects.HANDCUFFED, 20 * 60 * 10, 0, false, false, true));
            user.sendMessage(Text.literal("ใส่กุญแจมือแล้ว"), true);
            if (target instanceof PlayerEntity p) p.sendMessage(Text.literal("คุณถูกใส่กุญแจมือ!"), true);
        }
        user.getWorld().playSound(null, target.getBlockPos(), SoundEvents.BLOCK_CHAIN_PLACE, SoundCategory.PLAYERS, 1f, already ? 1.3f : 0.8f);
        return ActionResult.SUCCESS;
    }
}
