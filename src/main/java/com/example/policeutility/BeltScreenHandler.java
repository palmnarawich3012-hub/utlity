package com.example.policeutility;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.SlotActionType;

/** หน้าจอ 9x2 ของ vanilla แต่ล็อกไม่ให้ย้าย/ทิ้งตัวเข็มขัดขณะเปิดอยู่ (กันของหาย/ดูป) */
public class BeltScreenHandler extends GenericContainerScreenHandler {
    private final ItemStack belt;

    public BeltScreenHandler(int syncId, PlayerInventory playerInv, Inventory beltInv, ItemStack belt) {
        super(ScreenHandlerType.GENERIC_9X2, syncId, playerInv, beltInv, 2);
        this.belt = belt;
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        if (slotIndex >= 0 && slotIndex < slots.size() && slots.get(slotIndex).getStack() == belt) return;
        if (actionType == SlotActionType.SWAP && button >= 0
                && button < player.getInventory().size() && player.getInventory().getStack(button) == belt) return;
        super.onSlotClick(slotIndex, button, actionType, player);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        for (int i = 0; i < player.getInventory().size(); i++) {
            if (player.getInventory().getStack(i) == belt) return super.canUse(player);
        }
        return false;
    }
}
