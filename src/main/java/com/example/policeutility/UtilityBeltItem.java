package com.example.policeutility;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

/** คลิกขวาเปิดช่องเก็บของ 18 ช่อง (เก็บกุญแจมือ เทเซอร์ วิทยุ ฯลฯ) */
public class UtilityBeltItem extends Item {
    public UtilityBeltItem(Settings settings) { super(settings); }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!world.isClient) {
            user.openHandledScreen(new SimpleNamedScreenHandlerFactory(
                    (syncId, inv, p) -> new BeltScreenHandler(syncId, inv, new BeltInventory(stack), stack),
                    stack.getName()));
        }
        return TypedActionResult.success(stack, world.isClient());
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        int used = stack.hasNbt() && stack.getNbt().contains("Items", NbtElement.LIST_TYPE)
                ? stack.getNbt().getList("Items", NbtElement.COMPOUND_TYPE).size() : 0;
        tooltip.add(Text.literal("ช่องเก็บของ: " + used + "/" + BeltInventory.SIZE).formatted(Formatting.GRAY));
        tooltip.add(Text.literal("คลิกขวาเพื่อเปิด").formatted(Formatting.DARK_GRAY));
    }
}
