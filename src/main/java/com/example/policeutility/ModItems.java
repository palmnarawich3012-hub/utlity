package com.example.policeutility;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item FLASHLIGHT = reg("flashlight", new FlashlightItem(new FabricItemSettings().maxCount(1)));
    public static final Item HANDCUFFS = reg("handcuffs", new HandcuffsItem(new FabricItemSettings().maxCount(16)));
    public static final Item TASER = reg("taser", new TaserItem(new FabricItemSettings().maxDamage(64)));
    public static final Item RADIO = reg("radio", new RadioItem(new FabricItemSettings().maxCount(1)));
    public static final Item UTILITY_BELT = reg("utility_belt", new UtilityBeltItem(new FabricItemSettings().maxCount(1)));

    private static Item reg(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(PoliceUtilityMod.MOD_ID, name), item);
    }

    public static void register() {
        Registry.register(Registries.ITEM_GROUP, new Identifier(PoliceUtilityMod.MOD_ID, "police_utility"),
                FabricItemGroup.builder()
                        .icon(() -> new ItemStack(TASER))
                        .displayName(Text.translatable("itemGroup.policeutility"))
                        .entries((ctx, entries) -> {
                            entries.add(FLASHLIGHT);
                            entries.add(HANDCUFFS);
                            entries.add(TASER);
                            entries.add(RADIO);
                            entries.add(UTILITY_BELT);
                        }).build());
    }
}
