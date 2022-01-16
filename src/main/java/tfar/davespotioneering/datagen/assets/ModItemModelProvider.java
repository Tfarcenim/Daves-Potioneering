package tfar.davespotioneering.datagen.assets;

import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.loaders.SeparatePerspectiveModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.init.ModItems;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, DavesPotioneering.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        makeOneLayerItem(ModItems.COMPOUND_BREWING_STAND);
        makeOneLayerItem(ModItems.REINFORCED_CAULDRON);
        makeSimpleBlockItem(ModItems.MAGIC_LECTERN);
        makeSimpleBlockItem(ModItems.POTION_INJECTOR);

        otherGauntlets();
        alchemicalGauntlet();
    }

    private void otherGauntlets() {
        perspectiveGauntlet("rudimentary_gauntlet");
        perspectiveGauntlet("netherite_gauntlet");
    }

    private void perspectiveGauntlet(String name){
        ItemModelBuilder r3dFile = getBuilder("item/3d/"+name+"_1")
                .parent(getExistingFile(modLoc("item/3d/"+name)));

        ItemModelBuilder rSpriteFile = getBuilder("sprite/"+name)
                .parent(getExistingFile(mcLoc("item/generated")))
                .texture("layer0","item/sprite/"+name);

        getBuilder(name).guiLight(BlockModel.GuiLight.FRONT)
                .customLoader(SeparatePerspectiveModelBuilder::begin).base(rSpriteFile)
                .perspective(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND,r3dFile)
                .perspective(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND,r3dFile)
                .perspective(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND,r3dFile)
                .perspective(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND,r3dFile)
                .end();
    }

    private void alchemicalGauntlet() {

        String s = ModItems.POTIONEER_GAUNTLET.getRegistryName().getPath();

        ModelFile unlitFile = getExistingFile(modLoc("item/3d/"+s));

        ModelFile litFile = getExistingFile(modLoc("item/3d/lit_"+s));

        getBuilder(s).parent(getExistingFile(mcLoc("item/generated")))
                .override().model(unlitFile).predicate(mcLoc("active"),0).end()
        .override().model(litFile).predicate(mcLoc("active"),1).end()
        ;
    }

    protected void makeSimpleBlockItem(Item item,ResourceLocation loc) {
        getBuilder(item.getRegistryName().toString())
                .parent(getExistingFile(loc));
    }

    protected void makeSimpleBlockItem(Item item) {
        makeSimpleBlockItem(item,new ResourceLocation(DavesPotioneering.MODID,"block/" + item.getRegistryName().getPath()));
    }


    protected void makeOneLayerItem(Item item, ResourceLocation texture) {
        String path = item.getRegistryName().getPath();
        if (existingFileHelper.exists(new ResourceLocation(texture.getNamespace(),"item/" + texture.getPath())
                , ResourcePackType.CLIENT_RESOURCES, ".png", "textures")) {
            getBuilder(path).parent(getExistingFile(mcLoc("item/generated")))
                    .texture("layer0",new ResourceLocation(texture.getNamespace(),"item/" + texture.getPath()));
        } else {
            System.out.println("no texture for " + item + " found, skipping");
        }
    }

    protected void makeOneLayerItem(Item item) {
        ResourceLocation texture = item.getRegistryName();
        makeOneLayerItem(item,texture);
    }
}
