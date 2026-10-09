package com.example.policeutility;

import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

/**
 * วิทยุสื่อสาร 9 ช่อง
 *  - คลิกขวา: เปลี่ยนช่อง | Shift+คลิกขวา: เปิด/ปิด
 *  - ถือวิทยุที่เปิดอยู่แล้วพิมพ์แชท = ส่งเข้าวิทยุ (ผู้ที่มีวิทยุเปิดช่องเดียวกันในช่องเก็บของจะได้ยิน)
 */
public class RadioItem extends Item {
    public RadioItem(Settings settings) { super(settings); }

    public static boolean isOn(ItemStack s) {
        NbtCompound n = s.getNbt();
        return n != null && n.getBoolean("On");
    }

    public static int channel(ItemStack s) {
        NbtCompound n = s.getNbt();
        return (n != null && n.contains("Channel")) ? Math.max(1, Math.min(9, n.getInt("Channel"))) : 1;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!world.isClient) {
            NbtCompound nbt = stack.getOrCreateNbt();
            if (user.isSneaking()) {
                boolean on = !isOn(stack);
                nbt.putBoolean("On", on);
                user.sendMessage(Text.literal("วิทยุ: " + (on ? "เปิด" : "ปิด") + " | ช่อง " + channel(stack)), true);
            } else {
                int ch = channel(stack) % 9 + 1;
                nbt.putInt("Channel", ch);
                user.sendMessage(Text.literal("วิทยุ: ช่อง " + ch + (isOn(stack) ? "" : " (ปิดอยู่)")), true);
            }
            world.playSound(null, user.getBlockPos(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.5f, 2.0f);
        }
        return TypedActionResult.success(stack, world.isClient());
    }

    @Override
    public boolean hasGlint(ItemStack stack) { return isOn(stack); }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal((isOn(stack) ? "เปิด" : "ปิด") + " | ช่อง " + channel(stack)).formatted(Formatting.GREEN));
        tooltip.add(Text.literal("คลิกขวา: เปลี่ยนช่อง | Shift+คลิกขวา: เปิด/ปิด").formatted(Formatting.GRAY));
        tooltip.add(Text.literal("ถือแล้วพิมพ์แชทเพื่อพูดผ่านวิทยุ").formatted(Formatting.DARK_GRAY));
    }

    private static ItemStack heldOnRadio(ServerPlayerEntity p) {
        for (ItemStack s : new ItemStack[]{p.getMainHandStack(), p.getOffHandStack()}) {
            if (s.getItem() instanceof RadioItem && isOn(s)) return s;
        }
        return null;
    }

    private static boolean hasRadioOn(PlayerEntity p, int ch) {
        for (int i = 0; i < p.getInventory().size(); i++) {
            ItemStack s = p.getInventory().getStack(i);
            if (s.getItem() instanceof RadioItem && isOn(s) && channel(s) == ch) return true;
        }
        return false;
    }

    public static void registerChat() {
        ServerMessageEvents.ALLOW_CHAT_MESSAGE.register((message, sender, params) -> {
            ItemStack radio = heldOnRadio(sender);
            if (radio == null) return true;
            int ch = channel(radio);
            Text out = Text.literal("[วิทยุ CH" + ch + "] ").formatted(Formatting.GREEN)
                    .append(Text.literal(sender.getName().getString() + ": ").formatted(Formatting.YELLOW))
                    .append(Text.literal(message.getSignedContent()).formatted(Formatting.WHITE));
            for (ServerPlayerEntity p : sender.getServer().getPlayerManager().getPlayerList()) {
                if (hasRadioOn(p, ch)) {
                    p.sendMessage(out, false);
                    p.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 0.4f, 2.0f);
                }
            }
            return false; // ไม่ส่งเข้าแชทสาธารณะ
        });
    }
}
