package tfar.davespotioneering.client;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemDisplayContext;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.client.model.perspective.BakedPerspectiveModel;
import tfar.davespotioneering.init.ModItems;
import tfar.davespotioneering.item.Perspective;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class ClientHooks {


    static final ResourceLocation RUDIMENTARY_3D = new ResourceLocation(DavesPotioneering.MODID,"item/3d/rudimentary_gauntlet");
    static final ResourceLocation NETHERITE_3D = new ResourceLocation(DavesPotioneering.MODID,"item/3d/netherite_gauntlet");

    static final ResourceLocation POTIONEER_SPRITE = new ResourceLocation(DavesPotioneering.MODID,"item/sprite/potioneer_gauntlet");
    static final ResourceLocation LIT_POTIONEER_SPRITE = new ResourceLocation(DavesPotioneering.MODID,"item/sprite/lit_potioneer_gauntlet");


    static final ResourceLocation RUDIMENTARY_SPRITE = new ResourceLocation(DavesPotioneering.MODID,"item/sprite/rudimentary_gauntlet");

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
