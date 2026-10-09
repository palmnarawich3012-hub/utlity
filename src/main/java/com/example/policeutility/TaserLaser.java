package com.example.policeutility;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.joml.Vector3f;

/** ตอนถือเทเซอร์ (และไม่ได้อยู่ในคูลดาวน์) จะมีจุดเลเซอร์สีแดงตรงจุดที่เล็งอยู่ เหมือนศูนย์เล็งเลเซอร์ของจริง */
public class TaserLaser {
    private static final double RANGE = 16.0;
    private static final DustParticleEffect RED = new DustParticleEffect(new Vector3f(1.0f, 0.05f, 0.05f), 0.55f);

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(TaserLaser::tick);
    }

    private static boolean holdingTaser(ServerPlayerEntity p) {
        for (ItemStack s : new ItemStack[]{p.getMainHandStack(), p.getOffHandStack()}) {
            if (s.getItem() instanceof TaserItem) return !p.getItemCooldownManager().isCoolingDown(s.getItem());
        }
        return false;
    }

    private static void tick(MinecraftServer server) {
        if (server.getTicks() % 2 != 0) return;
        for (ServerPlayerEntity p : server.getPlayerManager().getPlayerList()) {
            if (p.isSpectator() || !holdingTaser(p)) continue;
            ServerWorld w = p.getServerWorld();
            Vec3d start = p.getEyePos();
            Vec3d look = p.getRotationVec(1.0F);
            Vec3d end = start.add(look.multiply(RANGE));
            var bhr = w.raycast(new RaycastContext(start, end, RaycastContext.ShapeType.COLLIDER,
                    RaycastContext.FluidHandling.NONE, p));
            if (bhr.getType() != HitResult.Type.MISS) end = bhr.getPos();
            Box box = p.getBoundingBox().stretch(look.multiply(RANGE)).expand(1.0);
            EntityHitResult ehr = ProjectileUtil.raycast(p, start, end, box,
                    e -> !e.isSpectator() && e.canHit() && e instanceof LivingEntity, RANGE * RANGE);
            Vec3d hit = ehr != null ? ehr.getPos() : end;
            Vec3d dot = hit.subtract(look.multiply(0.05)); // ขยับเข้าหาผู้เล่นนิดหน่อยกันจมผิวบล็อก
            w.spawnParticles(RED, dot.x, dot.y, dot.z, 1, 0, 0, 0, 0);
        }
    }
}
