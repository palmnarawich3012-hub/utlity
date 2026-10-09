package com.example.policeutility.client;

import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * ห่อโมเดลเดิม (กล่องเหลี่ยม) แล้วแทนที่ตอนเรนเดอร์ไอเทมด้วยเมช OBJ โค้งมนความละเอียดสูง
 * หน่วย OBJ = 1/16 บล็อก, UV อ้างอิงเทกซ์เจอร์ที่ใช้เป็น particle ของโมเดลเดิม (taser_obj.png)
 */
public class ObjItemModel extends ForwardingBakedModel {
    private final Mesh mesh;

    public ObjItemModel(BakedModel base, Identifier objId) throws IOException {
        this.wrapped = base;
        this.mesh = build(objId, base.getParticleSprite());
    }

    @Override
    public boolean isVanillaAdapter() { return false; }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
        context.meshConsumer().accept(mesh);
    }

    private static Mesh build(Identifier id, Sprite sprite) throws IOException {
        Renderer renderer = RendererAccess.INSTANCE.getRenderer();
        if (renderer == null) throw new IOException("No Fabric renderer available");

        List<float[]> v = new ArrayList<>(), vt = new ArrayList<>(), vn = new ArrayList<>();
        List<int[][]> faces = new ArrayList<>();
        Resource res = MinecraftClient.getInstance().getResourceManager().getResourceOrThrow(id);
        try (BufferedReader r = res.getReader()) {
            String line;
            while ((line = r.readLine()) != null) {
                String[] p = line.trim().split("\\s+");
                if (p.length < 2) continue;
                switch (p[0]) {
                    case "v" -> v.add(new float[]{Float.parseFloat(p[1]), Float.parseFloat(p[2]), Float.parseFloat(p[3])});
                    case "vt" -> vt.add(new float[]{Float.parseFloat(p[1]), Float.parseFloat(p[2])});
                    case "vn" -> vn.add(new float[]{Float.parseFloat(p[1]), Float.parseFloat(p[2]), Float.parseFloat(p[3])});
                    case "f" -> {
                        int[][] f = new int[3][3];
                        for (int i = 0; i < 3; i++) {
                            String[] t = p[i + 1].split("/");
                            f[i][0] = Integer.parseInt(t[0]) - 1;
                            f[i][1] = Integer.parseInt(t[1]) - 1;
                            f[i][2] = Integer.parseInt(t[2]) - 1;
                        }
                        faces.add(f);
                    }
                    default -> { }
                }
            }
        }

        float u0 = sprite.getMinU(), u1 = sprite.getMaxU(), v0 = sprite.getMinV(), v1 = sprite.getMaxV();
        MeshBuilder mb = renderer.meshBuilder();
        QuadEmitter q = mb.getEmitter();
        for (int[][] f : faces) {
            for (int k = 0; k < 4; k++) {
                int[] i = f[Math.min(k, 2)]; // สามเหลี่ยม = ควอดที่ซ้ำจุดสุดท้าย
                float[] pos = v.get(i[0]);
                float[] uv = vt.get(i[1]);
                float[] n = vn.get(i[2]);
                q.pos(k, pos[0] / 16f, pos[1] / 16f, pos[2] / 16f);
                q.uv(k, u0 + uv[0] * (u1 - u0), v0 + (1f - uv[1]) * (v1 - v0));
                q.normal(k, n[0], n[1], n[2]);
            }
            q.emit();
        }
        return mb.build();
    }
}
