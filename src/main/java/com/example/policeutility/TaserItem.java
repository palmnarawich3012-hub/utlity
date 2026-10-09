package com.example.policeutility;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.List;

/** ยิงไฟฟ้าระยะ 8 บล็อก ทำให้เป้าหมายช็อต (ช้า/อ่อนแรง/มึน) โดยไม่ฆ่า ใช้ความทนทาน 64 ครั้ง ซ่อมด้วย Redstone ที่ทั่ง */
public class TaserItem extends Item {
    private static final double RANGE = 8.0;

    public TaserItem(Settings settings) { super(settings); }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (world.isClient) return TypedActionResult.success(stack, true);

        ServerWorld sw = (ServerWorld) world;
        Vec3d start = user.getEyePos();
        Vec3d look = user.getRotationVec(1.0F);
        Vec3d end = start.add(look.multiply(RANGE));
        var bhr = world.raycast(new RaycastContext(start, end, RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE, user));
        if (bhr.getType() != HitResult.Type.MISS) end = bhr.getPos();

        Box box = user.getBoundingBox().stretch(look.multiply(RANGE)).expand(1.0);
        EntityHitResult ehr = ProjectileUtil.raycast(user, start, end, box,
                e -> !e.isSpectator() && e.canHit() && e instanceof LivingEntity, RANGE * RANGE);

        Vec3d impact = end;
        if (ehr != null) {
            impact = ehr.getPos();
            LivingEntity target = (LivingEntity) ehr.getEntity();
            stun(sw, target);
            sw.spawnParticles(ParticleTypes.ELECTRIC_SPARK, impact.x, impact.y, impact.z, 16, 0.3, 0.5, 0.3, 0.15);
        }
        double dist = start.distanceTo(impact);
        for (double d = 0.6; d < dist; d += 0.4) {
            Vec3d p = start.add(look.multiply(d));
            sw.spawnParticles(ParticleTypes.ELECTRIC_SPARK, p.x, p.y - 0.15, p.z, 1, 0.02, 0.02, 0.02, 0.0);
        }

        world.playSound(null, user.getBlockPos(), SoundEvents.ENTITY_LIGHTNING_BOLT_IMPACT, SoundCategory.PLAYERS, 0.3f, 2.0f);
        user.getItemCooldownManager().set(this, 60);
        stack.damage(1, user, p -> p.sendToolBreakStatus(hand));
        return TypedActionResult.success(stack, false);
    }

    private static void stun(ServerWorld world, LivingEntity target) {
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, 6));
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 100, 2));
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 80, 0));
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 160, 0)); // พิษ 8 วินาที (20 tick x 8)
        if (target.getHealth() > 2.0f) target.damage(world.getDamageSources().generic(), 1.0f);
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return ingredient.isOf(Items.REDSTONE);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("คลิกขวา: ยิงช็อต (ระยะ 8 บล็อก)").formatted(Formatting.GRAY));
        tooltip.add(Text.literal("โดนแล้วติดพิษ 8 วินาที").formatted(Formatting.DARK_GREEN));
        tooltip.add(Text.literal("ซ่อมด้วย Redstone ที่ทั่ง").formatted(Formatting.DARK_GRAY));
    }
}
