package tfar.davespotioneering.datagen.assets;

import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
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
        makeOneLayerItem(ModItems.REINFORCED_CAULDRON);
        makeSimpleBlockItem(ModItems.MAGIC_LECTERN);
        makeSimpleBlockItem(ModItems.POTION_INJECTOR);

        otherGauntlets();
        alchemicalGauntlet();
    }

    private void otherGauntlets() {
        ModelFile rFile = getExistingFile(modLoc("item/3d/rudimentary_gauntlet"));
        ModelFile nFile = getExistingFile(modLoc("item/3d/netherite_gauntlet"));
        getBuilder("rudimentary_gauntlet").parent(rFile);
        getBuilder("netherite_gauntlet").parent(nFile);

    }

    private void alchemicalGauntlet() {


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
