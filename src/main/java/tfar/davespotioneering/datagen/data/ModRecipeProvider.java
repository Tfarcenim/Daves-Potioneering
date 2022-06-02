package tfar.davespotioneering.datagen.data;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import tfar.davespotioneering.init.ModItems;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    public void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(ModItems.WHITE_UMBRELLA)
                .define('a', Blocks.WHITE_WOOL)
                .define('b', Items.SHIELD)
                .define('c', Items.IRON_INGOT)
                .pattern("aba").pattern(" c ").pattern(" c ")
                .unlockedBy("has_shield", has(Items.SHIELD)).save(consumer);

        ShapedRecipeBuilder.shaped(ModItems.ORANGE_UMBRELLA)
                .define('a', Blocks.ORANGE_WOOL)
                .define('b', Items.SHIELD)
                .define('c', Items.IRON_INGOT)
                .pattern("aba").pattern(" c ").pattern(" c ")
                .unlockedBy("has_shield", has(Items.SHIELD)).save(consumer);

        ShapedRecipeBuilder.shaped(ModItems.MAGENTA_UMBRELLA)
                .define('a', Blocks.MAGENTA_WOOL)
                .define('b', Items.SHIELD)
                .define('c', Items.IRON_INGOT)
                .pattern("aba").pattern(" c ").pattern(" c ")
                .unlockedBy("has_shield", has(Items.SHIELD)).save(consumer);

        ShapedRecipeBuilder.shaped(ModItems.LIGHT_BLUE_UMBRELLA)
                .define('a', Blocks.LIGHT_BLUE_WOOL)
                .define('b', Items.SHIELD)
                .define('c', Items.IRON_INGOT)
                .pattern("aba").pattern(" c ").pattern(" c ")
                .unlockedBy("has_shield", has(Items.SHIELD)).save(consumer);

        ShapedRecipeBuilder.shaped(ModItems.YELLOW_UMBRELLA)
                .define('a', Blocks.YELLOW_WOOL)
                .define('b', Items.SHIELD)
                .define('c', Items.IRON_INGOT)
                .pattern("aba").pattern(" c ").pattern(" c ")
                .unlockedBy("has_shield", has(Items.SHIELD)).save(consumer);

        ShapedRecipeBuilder.shaped(ModItems.LIME_UMBRELLA)
                .define('a', Blocks.LIME_WOOL)
                .define('b', Items.SHIELD)
                .define('c', Items.IRON_INGOT)
                .pattern("aba").pattern(" c ").pattern(" c ")
                .unlockedBy("has_shield", has(Items.SHIELD)).save(consumer);

        ShapedRecipeBuilder.shaped(ModItems.PINK_UMBRELLA)
                .define('a', Blocks.PINK_WOOL)
                .define('b', Items.SHIELD)
                .define('c', Items.IRON_INGOT)
                .pattern("aba").pattern(" c ").pattern(" c ")
                .unlockedBy("has_shield", has(Items.SHIELD)).save(consumer);

        ShapedRecipeBuilder.shaped(ModItems.GRAY_UMBRELLA)
                .define('a', Blocks.GRAY_WOOL)
                .define('b', Items.SHIELD)
                .define('c', Items.IRON_INGOT)
                .pattern("aba").pattern(" c ").pattern(" c ")
                .unlockedBy("has_shield", has(Items.SHIELD)).save(consumer);

        ShapedRecipeBuilder.shaped(ModItems.LIGHT_GRAY_UMBRELLA)
                .define('a', Blocks.LIGHT_GRAY_WOOL)
                .define('b', Items.SHIELD)
                .define('c', Items.IRON_INGOT)
                .pattern("aba").pattern(" c ").pattern(" c ")
                .unlockedBy("has_shield", has(Items.SHIELD)).save(consumer);

        ShapedRecipeBuilder.shaped(ModItems.CYAN_UMBRELLA)
                .define('a', Blocks.CYAN_WOOL)
                .define('b', Items.SHIELD)
                .define('c', Items.IRON_INGOT)
                .pattern("aba").pattern(" c ").pattern(" c ")
                .unlockedBy("has_shield", has(Items.SHIELD)).save(consumer);

        ShapedRecipeBuilder.shaped(ModItems.PURPLE_UMBRELLA)
                .define('a', Blocks.PURPLE_WOOL)
                .define('b', Items.SHIELD)
                .define('c', Items.IRON_INGOT)
                .pattern("aba").pattern(" c ").pattern(" c ")
                .unlockedBy("has_shield", has(Items.SHIELD)).save(consumer);

        ShapedRecipeBuilder.shaped(ModItems.BLUE_UMBRELLA)
                .define('a', Blocks.BLUE_WOOL)
                .define('b', Items.SHIELD)
                .define('c', Items.IRON_INGOT)
                .pattern("aba").pattern(" c ").pattern(" c ")
                .unlockedBy("has_shield", has(Items.SHIELD)).save(consumer);

        ShapedRecipeBuilder.shaped(ModItems.BROWN_UMBRELLA)
                .define('a', Blocks.BROWN_WOOL)
                .define('b', Items.SHIELD)
                .define('c', Items.IRON_INGOT)
                .pattern("aba").pattern(" c ").pattern(" c ")
                .unlockedBy("has_shield", has(Items.SHIELD)).save(consumer);

        ShapedRecipeBuilder.shaped(ModItems.GREEN_UMBRELLA)
                .define('a', Blocks.GREEN_WOOL)
                .define('b', Items.SHIELD)
                .define('c', Items.IRON_INGOT)
                .pattern("aba").pattern(" c ").pattern(" c ")
                .unlockedBy("has_shield", has(Items.SHIELD)).save(consumer);

        ShapedRecipeBuilder.shaped(ModItems.RED_UMBRELLA)
                .define('a', Blocks.RED_WOOL)
                .define('b', Items.SHIELD)
                .define('c', Items.IRON_INGOT)
                .pattern("aba").pattern(" c ").pattern(" c ")
                .unlockedBy("has_shield", has(Items.SHIELD)).save(consumer);

        ShapedRecipeBuilder.shaped(ModItems.BLACK_UMBRELLA)
                .define('a', Blocks.BLACK_WOOL)
                .define('b', Items.SHIELD)
                .define('c', Items.IRON_INGOT)
                .pattern("aba").pattern(" c ").pattern(" c ")
                .unlockedBy("has_shield", has(Items.SHIELD)).save(consumer);

        ShapedRecipeBuilder.shaped(ModItems.GILDED_UMBRELLA)
                .define('a', Items.GOLD_INGOT)
                .define('b', Items.SHIELD)
                .define('c', Items.IRON_INGOT)
                .pattern("aba").pattern(" c ").pattern(" c ")
                .unlockedBy("has_shield", has(Items.SHIELD)).save(consumer);

        ShapedRecipeBuilder.shaped(ModItems.AGED_UMBRELLA)
                .define('a', Items.IRON_INGOT)
                .define('b', Items.SHIELD)
                .define('c', Items.IRON_INGOT)
                .pattern("aba").pattern(" c ").pattern(" c ")
                .unlockedBy("has_shield", has(Items.SHIELD)).save(consumer);


        ShapedRecipeBuilder.shaped(ModItems.COMPOUND_BREWING_STAND)
                .define('a', Blocks.HOPPER)
                .define('b', Items.CRYING_OBSIDIAN)
                .define('c', Items.BREWING_STAND)
                .define('d', Items.BASALT)
                .pattern(" a ").pattern("bcb").pattern("ddd")
                .unlockedBy("has_shield", has(Items.SHIELD)).save(consumer);

        ShapedRecipeBuilder.shaped(ModItems.REINFORCED_CAULDRON)
                .define('a', Blocks.CAULDRON)
                .define('b', Items.GOLD_INGOT)
                .pattern("b b").pattern("bab").pattern("b b")
                .unlockedBy("has_gold", has(Items.GOLD_INGOT)).save(consumer);

        netheriteSmithing(consumer, ModItems.RUDIMENTARY_GAUNTLET, ModItems.NETHERITE_GAUNTLET);

        ShapedRecipeBuilder.shaped(ModItems.POTION_INJECTOR)
                .define('a', Blocks.DIORITE)
                .define('b', Items.STONE_BUTTON)
                .define('c', Items.LEATHER)
                .define('d', Items.GLASS)
                .pattern("aba").pattern("cac").pattern("dad")
                .unlockedBy("has_leather", has(Items.LEATHER)).save(consumer);

        ShapedRecipeBuilder.shaped(ModItems.POTIONEER_GAUNTLET)
                .define('a', Items.GLASS_BOTTLE)
                .define('b', Items.HOPPER)
                .define('c', ModItems.NETHERITE_GAUNTLET)
                .define('d', Items.LEVER)
                .pattern("aba").pattern("aca").pattern("ada")
                .unlockedBy("has_netherite_gauntlet", has(ModItems.NETHERITE_GAUNTLET)).save(consumer);

        ShapedRecipeBuilder.shaped(ModItems.RUDIMENTARY_GAUNTLET)
                .define('a', Items.IRON_INGOT)
                .define('b', Items.GOLD_INGOT)
                .pattern("abb").pattern("aab")
                .unlockedBy("has_gold_ingot", has(Items.GOLD_INGOT)).save(consumer);
    }
}
