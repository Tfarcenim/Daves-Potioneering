package tfar.davespotioneering.client;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.duck.ModelManagerDuck;
import tfar.davespotioneering.init.ModItems;
import tfar.davespotioneering.mixin.BakedModelManagerAccess;

import java.util.HashMap;
import java.util.Map;

public class ClientHooks {

    private static final String RUD_ID = "item/sprite/rudimentary_gauntlet";


    static boolean chace;

    static Map<Identifier,BakedModel> map = new HashMap<>();

    public static BakedModel modifyModel(BakedModel model, ItemStack stack, ModelTransformation.Mode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, ItemModels models) {
        if (stack.getItem() == ModItems.RUDIMENTARY_GAUNTLET) {
            if (renderMode == ModelTransformation.Mode.GUI) {

                if (!chace) {
                    chace = true;
                    for (Map.Entry<Identifier,BakedModel> entry : ((BakedModelManagerAccess)models.getModelManager()).getModels().entrySet()) {
                        if (entry.getKey().getPath().contains("gauntlet")) {
                            map.put(entry.getKey(),entry.getValue());
                        }
                    }
                }

                BakedModel bakedModel = getSpecial(models,new Identifier(DavesPotioneering.MODID, RUD_ID));
                return bakedModel;
            }
        }
        return model;
    }

    private static BakedModel getSpecial(ItemModels models,Identifier rl) {
        return ((ModelManagerDuck)models.getModelManager()).getSpecialModel(rl);
    }

    public static void injectCustomModels(ModelLoader modelLoader, Map<Identifier, UnbakedModel> unbakedModels, Map<Identifier, UnbakedModel> modelsToBake) {
        injectCustomModel(new Identifier(DavesPotioneering.MODID, RUD_ID),modelLoader,unbakedModels,modelsToBake);
    }

    public static void injectCustomModel(Identifier model,ModelLoader modelLoader, Map<Identifier, UnbakedModel> unbakedModels, Map<Identifier, UnbakedModel> modelsToBake) {
        UnbakedModel unbakedModel = modelLoader.getOrLoadModel(model);
        unbakedModels.put(model,unbakedModel);
        modelsToBake.put(model,unbakedModel);
    }

    public static int modifyLight(int model, ItemStack stack, ModelTransformation.Mode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, ItemModels models) {
        float hue = (Util.getMeasuringTimeMs() / 1000f) % 1;

        int c = MathHelper.hsvToRgb(hue, 1, 1);

      //  return 0x100 * c;
        return light;//OverlayTexture.DEFAULT_UV;
    }
}
