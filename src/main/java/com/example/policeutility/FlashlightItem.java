package com.example.policeutility;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

/** คลิกขวาเพื่อเปิด/ปิด ตอนเปิดและถืออยู่ FlashlightManager จะส่องแสงไปยังจุดที่มองอยู่ */
public class FlashlightItem extends Item {
    public FlashlightItem(Settings settings) { super(settings); }

    public static boolean isOn(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        return nbt != null && nbt.getBoolean("On");
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!world.isClient) {
            boolean on = !isOn(stack);
            stack.getOrCreateNbt().putBoolean("On", on);
            world.playSound(null, user.getBlockPos(), SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.PLAYERS, 0.8f, on ? 1.4f : 1.0f);
            user.sendMessage(Text.literal(on ? "ไฟฉาย: เปิด" : "ไฟฉาย: ปิด"), true);
        }
        return TypedActionResult.success(stack, world.isClient());
    }

    @Override
    public boolean hasGlint(ItemStack stack) { return isOn(stack); }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("คลิกขวา: เปิด/ปิด").formatted(Formatting.GRAY));
    }
}
