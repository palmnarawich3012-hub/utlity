package com.example.policeutility.client;

import com.example.policeutility.PoliceUtilityMod;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PoliceUtilityClient implements ClientModInitializer {
    private static final Logger LOG = LoggerFactory.getLogger("policeutility");
    private static final java.util.Set<String> MESH_ITEMS = java.util.Set.of("taser", "flashlight", "handcuffs", "radio", "utility_belt");

    @Override
    public void onInitializeClient() {
        ModelLoadingPlugin.register(ctx -> ctx.modifyModelAfterBake().register((model, context) -> {
            if (model == null) return null;
            Identifier id = context.id();
            if (id instanceof ModelIdentifier mid
                    && PoliceUtilityMod.MOD_ID.equals(mid.getNamespace())
                    && MESH_ITEMS.contains(mid.getPath())
                    && "inventory".equals(mid.getVariant())) {
                try {
                    return new ObjItemModel(model, new Identifier(PoliceUtilityMod.MOD_ID, "models/obj/" + mid.getPath() + ".obj"));
                } catch (Exception e) {
                    LOG.error("Failed to load OBJ for " + mid.getPath() + ", using block model fallback", e);
                }
            }
            return model;
        }));
    }
}
