package tfar.davespotioneering.datagen.assets;

import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.init.ModItems;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, DavesPotioneering.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        makeOneLayerItem(ModItems.ADVANCED_BREWING_STAND);
        makeSimpleBlockItem(ModItems.REINFORCED_CAULDRON,new ResourceLocation(DavesPotioneering.MODID,"block/reinforced_cauldron_level0"));
        makeSimpleBlockItem(ModItems.MAGIC_LECTERN);
        makeSimpleBlockItem(ModItems.POTION_INJECTOR);
        gauntlet();


       // addTippedLayer(Items.DIAMOND_SWORD);
    }

    //this is complicated
    private void addTippedLayer(Item item) {
        ResourceLocation rl = new ResourceLocation("item/"+item.getRegistryName().getPath());
        //first, get the original model from the item
        ModelFile.ExistingModelFile modelFile = getExistingFile(rl);
        //then replace it with a new file

        ModelFile newTippedFile = getBuilder("tipped_"+modelFile.getLocation().toString()).parent(modelFile)
                .texture("layer1",modLoc("item/sword_dripping"));

        ModelFile newFile = getBuilder(modelFile.getLocation().toString()).parent(getExistingFile(mcLoc("item/handheld")))
                .texture("layer0",rl)
                //1 means that it's tipped, and should use the new tipped + original model
                //0 means that it should use the original parent model
                .override()
                .predicate(new ResourceLocation("tipped"),1)
                .model(newTippedFile).end();
    }

    private void gauntlet() {


        ModelFile unlitFile = getExistingFile(modLoc("item/3d/alchemical_gauntlet"));

        ModelFile litFile = getExistingFile(modLoc("item/3d/lit_alchemical_gauntlet"));

        getBuilder("alchemical_gauntlet").parent(getExistingFile(mcLoc("item/generated")))
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
