package tfar.davespotioneering.client;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemDisplayContext;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.client.model.perspective.BakedPerspectiveModel;
import tfar.davespotioneering.init.ModItems;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import tfar.davespotioneering.mixin.BakedOverrideAccessor;
import tfar.davespotioneering.mixin.ItemOverridesAccessor;

public class ClientHooks {


    static final ResourceLocation RUDIMENTARY_3D = new ResourceLocation(DavesPotioneering.MODID,"item/3d/rudimentary_gauntlet");
    static final ResourceLocation NETHERITE_3D = new ResourceLocation(DavesPotioneering.MODID,"item/3d/netherite_gauntlet");

    static final ResourceLocation POTIONEER_PERSPECTIVE = new ResourceLocation(DavesPotioneering.MODID,"item/perspective/potioneer_gauntlet");
    static final ResourceLocation LIT_POTIONEER_PERSPECTIVE = new ResourceLocation(DavesPotioneering.MODID,"item/perspective/lit_potioneer_gauntlet");

    static final ResourceLocation POTIONEER_SPRITE = new ResourceLocation(DavesPotioneering.MODID,"item/sprite/potioneer_gauntlet");
    static final ResourceLocation LIT_POTIONEER_SPRITE = new ResourceLocation(DavesPotioneering.MODID,"item/sprite/lit_potioneer_gauntlet");


    static final ResourceLocation RUDIMENTARY_SPRITE = new ResourceLocation(DavesPotioneering.MODID,"item/sprite/rudimentary_gauntlet");


    public static void onModelBake(Map<ResourceLocation, BakedModel> bakedTopLevelModels, ModelBakery modelBakery) {

        Map<ResourceLocation,BakedModel> gauntletModels = new HashMap<>();

        bakedTopLevelModels.entrySet().stream()
                .filter(resourceLocationBakedModelEntry -> resourceLocationBakedModelEntry.getKey().getPath().contains("potioneer_gauntlet"))
                .forEach(resourceLocationBakedModelEntry -> gauntletModels.put(resourceLocationBakedModelEntry.getKey(),resourceLocationBakedModelEntry.getValue()));

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

        ResourceLocation potioneerGauntletModelRL = getDefaultModel(ModItems.POTIONEER_GAUNTLET);
        BakedModel potioneerGauntletModel = bakedTopLevelModels.get(potioneerGauntletModelRL);

        if (potioneerGauntletModel != null) {
            ItemOverrides itemOverrides = potioneerGauntletModel.getOverrides();
            ItemOverrides.BakedOverride[] bakedOverrides = ((ItemOverridesAccessor)itemOverrides).getOverrides();

            ItemOverrides.BakedOverride override = bakedOverrides[1];
            BakedModel potioneerModel = ((BakedOverrideAccessor)override).getModel();
            if (potioneerModel != null) {
                BakedModel bakedModel = new BakedPerspectiveModel(potioneerModel, ImmutableMap
                        .<ItemDisplayContext, BakedModel>builder()
                        .put(ItemDisplayContext.GUI,bakedTopLevelModels.get(POTIONEER_SPRITE))
                        .build());
                ((BakedOverrideAccessor)override).setModel(bakedModel);
            }

            ItemOverrides.BakedOverride override1 = bakedOverrides[0];
            BakedModel litPotioneerModel = ((BakedOverrideAccessor)override1).getModel();
            if (litPotioneerModel != null) {
                BakedModel bakedModel = new BakedPerspectiveModel(litPotioneerModel, ImmutableMap
                        .<ItemDisplayContext, BakedModel>builder()
                        .put(ItemDisplayContext.GUI,bakedTopLevelModels.get(LIT_POTIONEER_SPRITE))
                        .build());
                ((BakedOverrideAccessor)override1).setModel(bakedModel);
            }
        }
    }

    public static ResourceLocation getDefaultModel(Item item) {
        ResourceLocation registryName = BuiltInRegistries.ITEM.getKey(item);
        return new ModelResourceLocation(registryName,"inventory");
    }

}
