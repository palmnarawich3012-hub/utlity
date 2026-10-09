package com.example.policeutility;

import net.fabricmc.fabric.api.event.player.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;

public class ModEvents {
    private static boolean cuffed(PlayerEntity p) {
        return p.hasStatusEffect(ModEffects.HANDCUFFED);
    }

    public static void register() {
        UseItemCallback.EVENT.register((player, world, hand) ->
                cuffed(player) ? TypedActionResult.fail(player.getStackInHand(hand))
                               : TypedActionResult.pass(player.getStackInHand(hand)));
        UseBlockCallback.EVENT.register((player, world, hand, hit) ->
                cuffed(player) ? ActionResult.FAIL : ActionResult.PASS);
        UseEntityCallback.EVENT.register((player, world, hand, entity, hit) ->
                cuffed(player) ? ActionResult.FAIL : ActionResult.PASS);
        AttackBlockCallback.EVENT.register((player, world, hand, pos, dir) ->
                cuffed(player) ? ActionResult.FAIL : ActionResult.PASS);
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hit) ->
                cuffed(player) ? ActionResult.FAIL : ActionResult.PASS);
    }
}
