package com.example.policeutility;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

/** ช่องเก็บของ 18 ช่อง เก็บใน NBT ของเข็มขัดเอง */
public class BeltInventory extends SimpleInventory {
    public static final int SIZE = 18;
    private final ItemStack belt;
    private boolean loading;

    public BeltInventory(ItemStack belt) {
        super(SIZE);
        this.belt = belt;
        load();
    }

    private void load() {
        loading = true;
        NbtCompound nbt = belt.getNbt();
        if (nbt != null && nbt.contains("Items", NbtElement.LIST_TYPE)) {
            NbtList list = nbt.getList("Items", NbtElement.COMPOUND_TYPE);
            for (int i = 0; i < list.size(); i++) {
                NbtCompound c = list.getCompound(i);
                int slot = c.getByte("Slot") & 255;
                if (slot < SIZE) setStack(slot, ItemStack.fromNbt(c));
            }
        }
        loading = false;
    }

    private void save() {
        NbtList list = new NbtList();
        for (int i = 0; i < SIZE; i++) {
            ItemStack s = getStack(i);
            if (!s.isEmpty()) {
                NbtCompound c = new NbtCompound();
                c.putByte("Slot", (byte) i);
                s.writeNbt(c);
                list.add(c);
            }
        }
        if (list.isEmpty()) belt.removeSubNbt("Items");
        else belt.getOrCreateNbt().put("Items", list);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (!loading) save();
    }

    @Override
    public void onClose(PlayerEntity player) {
        save();
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return !(stack.getItem() instanceof UtilityBeltItem);
    }
}
