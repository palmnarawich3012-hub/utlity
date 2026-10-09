package com.example.policeutility;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.Blocks;
import net.minecraft.block.LightBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.*;

/** วางบล็อกแสง (Light block) ชั่วคราวที่จุดปลายลำแสงของไฟฉาย แล้วลบเมื่อขยับ/เก็บ */
public class FlashlightManager {
    private record Placed(ServerWorld world, BlockPos pos) {}
    private static final Map<UUID, Placed> ACTIVE = new HashMap<>();
    private static final double RANGE = 14.0;

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(FlashlightManager::tick);
        ServerLifecycleEvents.SERVER_STOPPING.register(s -> new ArrayList<>(ACTIVE.keySet()).forEach(FlashlightManager::remove));
    }

    private static boolean holdingOn(ServerPlayerEntity p) {
        for (ItemStack s : new ItemStack[]{p.getMainHandStack(), p.getOffHandStack()}) {
            if (s.getItem() instanceof FlashlightItem && FlashlightItem.isOn(s)) return true;
        }
        return false;
    }

    private static BlockPos target(ServerPlayerEntity p) {
        ServerWorld w = p.getServerWorld();
        Vec3d start = p.getEyePos();
        Vec3d look = p.getRotationVec(1.0F);
        Vec3d end = start.add(look.multiply(RANGE));
        var hit = w.raycast(new RaycastContext(start, end, RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE, p));
        Vec3d pt = hit.getType() == HitResult.Type.MISS ? end : hit.getPos().subtract(look.multiply(0.6));
        BlockPos bp = BlockPos.ofFloored(pt);
        return w.getBlockState(bp).isAir() ? bp : null;
    }

    private static void tick(MinecraftServer server) {
        if (server.getTicks() % 2 != 0) return;
        Set<UUID> seen = new HashSet<>();
        for (ServerPlayerEntity p : server.getPlayerManager().getPlayerList()) {
            UUID id = p.getUuid();
            BlockPos t = holdingOn(p) ? target(p) : null;
            if (t == null) continue;
            ServerWorld w = p.getServerWorld();
            Placed old = ACTIVE.get(id);
            seen.add(id);
            if (old != null && old.world() == w && old.pos().equals(t)) continue;
            remove(id);
            w.setBlockState(t, Blocks.LIGHT.getDefaultState().with(LightBlock.LEVEL_15, 13));
            ACTIVE.put(id, new Placed(w, t));
        }
        for (UUID id : new ArrayList<>(ACTIVE.keySet())) {
            if (!seen.contains(id)) remove(id);
        }
    }

    private static void remove(UUID id) {
        Placed pl = ACTIVE.remove(id);
        if (pl != null && pl.world().getBlockState(pl.pos()).isOf(Blocks.LIGHT)) {
            pl.world().setBlockState(pl.pos(), Blocks.AIR.getDefaultState());
        }
    }
}
