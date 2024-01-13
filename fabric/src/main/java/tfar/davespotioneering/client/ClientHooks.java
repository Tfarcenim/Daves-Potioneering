package tfar.davespotioneering.client;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemDisplayContext;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.DavesPotioneeringClient;
import tfar.davespotioneering.client.model.perspective.BakedPerspectiveModel;
import tfar.davespotioneering.duck.ModelManagerDuck;
import tfar.davespotioneering.init.ModItems;
import tfar.davespotioneering.item.GauntletItemFabric;
import tfar.davespotioneering.item.Perspective;
import tfar.davespotioneering.mixin.BakedModelManagerAccess;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.MultiBufferSource;
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
                boolean active = DavesPotioneeringClient.GAUNTLET.call(stack, null,null,0) == 1;

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

    static final ResourceLocation RUDIMENTARY_3D = new ResourceLocation(DavesPotioneering.MODID,"item/3d/rudimentary_gauntlet");
    static final ResourceLocation NETHERITE_3D = new ResourceLocation(DavesPotioneering.MODID,"item/3d/netherite_gauntlet");

    static final ResourceLocation RUDIMENTARY_SPRITE = new ResourceLocation(DavesPotioneering.MODID,"item/sprite/rudimentary_gauntlet");

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

    public static void onModelBake(Map<ResourceLocation, BakedModel> bakedTopLevelModels, ModelBakery modelBakery) {
        ResourceLocation rudimentaryGauntletRL = getDefaultModel(ModItems.RUDIMENTARY_GAUNTLET);
        BakedModel rudimentaryModel = bakedTopLevelModels.get(rudimentaryGauntletRL);
        if (rudimentaryModel != null) {
            BakedModel bakedModel = new BakedPerspectiveModel(rudimentaryModel, ImmutableMap
                    .<ItemDisplayContext, BakedModel>builder()
                    .put(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND,bakedTopLevelModels.get(RUDIMENTARY_3D))
                    .put(ItemDisplayContext.FIRST_PERSON_LEFT_HAND,bakedTopLevelModels.get(RUDIMENTARY_3D))
                    .put(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND,bakedTopLevelModels.get(RUDIMENTARY_3D))
                    .put(ItemDisplayContext.THIRD_PERSON_LEFT_HAND,bakedTopLevelModels.get(RUDIMENTARY_3D))
                    .build());
            bakedTopLevelModels.put(rudimentaryGauntletRL, bakedModel);
        }

        ResourceLocation netheriteGauntletRL = getDefaultModel(ModItems.NETHERITE_GAUNTLET);
        BakedModel netheriteModel = bakedTopLevelModels.get(netheriteGauntletRL);
        if (netheriteModel != null) {
            BakedModel bakedModel = new BakedPerspectiveModel(netheriteModel, ImmutableMap
                    .<ItemDisplayContext, BakedModel>builder()
                    .put(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND,bakedTopLevelModels.get(NETHERITE_3D))
                    .put(ItemDisplayContext.FIRST_PERSON_LEFT_HAND,bakedTopLevelModels.get(NETHERITE_3D))
                    .put(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND,bakedTopLevelModels.get(NETHERITE_3D))
                    .put(ItemDisplayContext.THIRD_PERSON_LEFT_HAND,bakedTopLevelModels.get(NETHERITE_3D))
                    .build());
            bakedTopLevelModels.put(netheriteGauntletRL, bakedModel);
        }
    }

    public static ResourceLocation getDefaultModel(Item item) {
        ResourceLocation registryName = BuiltInRegistries.ITEM.getKey(item);
        return new ModelResourceLocation(registryName,"inventory");
    }

}
