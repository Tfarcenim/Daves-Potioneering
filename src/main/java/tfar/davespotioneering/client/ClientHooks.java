package tfar.davespotioneering.client;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import tfar.davespotioneering.duck.ModelManagerDuck;
import tfar.davespotioneering.init.ModItems;
import tfar.davespotioneering.item.Perspective;
import tfar.davespotioneering.mixin.BakedModelManagerAccess;

import java.util.HashMap;
import java.util.Map;

public class ClientHooks {


    static boolean chace;

    static Map<Identifier,BakedModel> map = new HashMap<>();

    public static BakedModel modifyModel(BakedModel model, ItemStack stack, ModelTransformation.Mode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, ItemModels models) {
        if (stack.getItem() instanceof Perspective) {
            if (renderMode == ModelTransformation.Mode.GUI) {
                //the other 2 vars are never used
                boolean active = ClientEvents.GAUNTLET.call(stack, null,null,0) == 1;

                if (!chace) {
                    chace = true;
                    for (Map.Entry<Identifier,BakedModel> entry : ((BakedModelManagerAccess)models.getModelManager()).getModels().entrySet()) {
                        if (entry.getKey().getPath().contains("gauntlet")) {
                            map.put(entry.getKey(),entry.getValue());
                        }
                    }
                }

                BakedModel bakedModel = getSpecial(models, ((Perspective)stack.getItem()).getGuiModel(active));
//                BakedModel bakedModel = models.getModelManager().getModel(new ModelIdentifier("iron_ingot","inventory"));

                return bakedModel;
            }
        }
        return model;
    }

    private static BakedModel getSpecial(ItemModels models,Identifier rl) {
        return ((ModelManagerDuck)models.getModelManager()).getSpecialModel(rl);
    }

    public static void injectCustomModels(ModelLoader modelLoader, Map<Identifier, UnbakedModel> unbakedModels, Map<Identifier, UnbakedModel> modelsToBake) {
        for (Item item : ModItems.getAllItems()) {
            if (item instanceof Perspective) {
                injectCustomModel(((Perspective) item).getGuiModel(false),modelLoader,unbakedModels,modelsToBake);
                injectCustomModel(((Perspective) item).getGuiModel(true),modelLoader,unbakedModels,modelsToBake);
            }
        }
    }

    public static void injectCustomModel(Identifier model,ModelLoader modelLoader, Map<Identifier, UnbakedModel> unbakedModels, Map<Identifier, UnbakedModel> modelsToBake) {
        UnbakedModel unbakedModel = modelLoader.getOrLoadModel(model);
        unbakedModels.put(model,unbakedModel);
        modelsToBake.put(model,unbakedModel);
    }

    /*public static void onModelBake(BakedModelManager modelManager, Map<Identifier, BakedModel> modelRegistry, ModelLoader modelLoader) {

        Identifier rl = new ModelIdentifier(new Identifier(DavesPotioneering.MODID,"potioneer_gauntlet"),"inventory");

        BakedModel gauntletModel = modelRegistry.get(rl);

        ModelOverrideList modelOverrideList = gauntletModel.getOverrides();

        BakedModel gauntletOverrideModel = ((ModelOverrideListMixin)modelOverrideList).getModels().get(0);

        FullBrightModel newModel = new FullBrightModel(gauntletOverrideModel,false);

        ((ModelOverrideListMixin)modelOverrideList).getModels().set(0,newModel);
    }*/
}
