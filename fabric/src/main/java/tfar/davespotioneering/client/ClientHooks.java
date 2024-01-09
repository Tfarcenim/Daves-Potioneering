package tfar.davespotioneering.client;

import net.minecraft.world.item.ItemDisplayContext;
import tfar.davespotioneering.duck.ModelManagerDuck;
import tfar.davespotioneering.init.ModItems;
import tfar.davespotioneering.item.Perspective;
import tfar.davespotioneering.mixin.BakedModelManagerAccess;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ClientHooks {


    static boolean chace;

    static Map<ResourceLocation,BakedModel> map = new HashMap<>();

    public static BakedModel modifyModel(BakedModel model, ItemStack stack, ItemDisplayContext renderMode, boolean leftHanded, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay, ItemModelShaper models) {
        if (stack.getItem() instanceof Perspective) {
            if (renderMode == ItemDisplayContext.GUI) {
                //the other 2 vars are never used
                boolean active = ClientEvents.GAUNTLET.call(stack, null,null,0) == 1;

                if (!chace) {
                    chace = true;
                    for (Map.Entry<ResourceLocation,BakedModel> entry : ((BakedModelManagerAccess)models.getModelManager()).getBakedRegistry().entrySet()) {
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

    private static BakedModel getSpecial(ItemModelShaper models,ResourceLocation rl) {
        return ((ModelManagerDuck)models.getModelManager()).getSpecialModel(rl);
    }

    public static void injectCustomModels(ModelBakery modelLoader, Map<ResourceLocation, UnbakedModel> unbakedModels, Map<ResourceLocation, UnbakedModel> modelsToBake) {
        for (Item item : ModItems.getAllItems()) {
            if (item instanceof Perspective) {
                injectCustomModel(((Perspective) item).getGuiModel(false),modelLoader,unbakedModels,modelsToBake);
                injectCustomModel(((Perspective) item).getGuiModel(true),modelLoader,unbakedModels,modelsToBake);
            }
        }
    }

    public static void injectCustomModel(ResourceLocation model,ModelBakery modelLoader, Map<ResourceLocation, UnbakedModel> unbakedModels, Map<ResourceLocation, UnbakedModel> modelsToBake) {
        UnbakedModel unbakedModel = modelLoader.getModel(model);
        unbakedModels.put(model,unbakedModel);
        modelsToBake.put(model,unbakedModel);
    }

    /*public static void onModelBake(BakedModelManager modelManager, Map<Identifier, BakedModel> modelRegistry, ModelLoader modelLoader) {

        Identifier rl = new ModelIdentifier(new Identifier(DavesPotioneeringFabric.MODID,"potioneer_gauntlet"),"inventory");

        BakedModel gauntletModel = modelRegistry.get(rl);

        ModelOverrideList modelOverrideList = gauntletModel.getOverrides();

        BakedModel gauntletOverrideModel = ((ModelOverrideListMixin)modelOverrideList).getModels().get(0);

        FullBrightModel newModel = new FullBrightModel(gauntletOverrideModel,false);

        ((ModelOverrideListMixin)modelOverrideList).getModels().set(0,newModel);
    }*/
}
