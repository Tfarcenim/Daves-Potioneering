package tfar.davespotioneering.datagen.data;

import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.Items;
import tfar.davespotioneering.init.ModItems;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    public void registerRecipes(Consumer<IFinishedRecipe> consumer) {
        ShapedRecipeBuilder.shapedRecipe(ModItems.WHITE_UMBRELLA)
                .key('a', Blocks.WHITE_WOOL)
                .key('b', Items.SHIELD)
                .key('c', Items.IRON_INGOT)
                .patternLine("aba").patternLine(" c ").patternLine(" c ")
                .addCriterion("has_shield", hasItem(Items.SHIELD)).build(consumer);

        ShapedRecipeBuilder.shapedRecipe(ModItems.ORANGE_UMBRELLA)
                .key('a', Blocks.ORANGE_WOOL)
                .key('b', Items.SHIELD)
                .key('c', Items.IRON_INGOT)
                .patternLine("aba").patternLine(" c ").patternLine(" c ")
                .addCriterion("has_shield", hasItem(Items.SHIELD)).build(consumer);

        ShapedRecipeBuilder.shapedRecipe(ModItems.MAGENTA_UMBRELLA)
                .key('a', Blocks.MAGENTA_WOOL)
                .key('b', Items.SHIELD)
                .key('c', Items.IRON_INGOT)
                .patternLine("aba").patternLine(" c ").patternLine(" c ")
                .addCriterion("has_shield", hasItem(Items.SHIELD)).build(consumer);

        ShapedRecipeBuilder.shapedRecipe(ModItems.LIGHT_BLUE_UMBRELLA)
                .key('a', Blocks.LIGHT_BLUE_WOOL)
                .key('b', Items.SHIELD)
                .key('c', Items.IRON_INGOT)
                .patternLine("aba").patternLine(" c ").patternLine(" c ")
                .addCriterion("has_shield", hasItem(Items.SHIELD)).build(consumer);

        ShapedRecipeBuilder.shapedRecipe(ModItems.YELLOW_UMBRELLA)
                .key('a', Blocks.YELLOW_WOOL)
                .key('b', Items.SHIELD)
                .key('c', Items.IRON_INGOT)
                .patternLine("aba").patternLine(" c ").patternLine(" c ")
                .addCriterion("has_shield", hasItem(Items.SHIELD)).build(consumer);

        ShapedRecipeBuilder.shapedRecipe(ModItems.LIME_UMBRELLA)
                .key('a', Blocks.LIME_WOOL)
                .key('b', Items.SHIELD)
                .key('c', Items.IRON_INGOT)
                .patternLine("aba").patternLine(" c ").patternLine(" c ")
                .addCriterion("has_shield", hasItem(Items.SHIELD)).build(consumer);

        ShapedRecipeBuilder.shapedRecipe(ModItems.PINK_UMBRELLA)
                .key('a', Blocks.PINK_WOOL)
                .key('b', Items.SHIELD)
                .key('c', Items.IRON_INGOT)
                .patternLine("aba").patternLine(" c ").patternLine(" c ")
                .addCriterion("has_shield", hasItem(Items.SHIELD)).build(consumer);

        ShapedRecipeBuilder.shapedRecipe(ModItems.GRAY_UMBRELLA)
                .key('a', Blocks.GRAY_WOOL)
                .key('b', Items.SHIELD)
                .key('c', Items.IRON_INGOT)
                .patternLine("aba").patternLine(" c ").patternLine(" c ")
                .addCriterion("has_shield", hasItem(Items.SHIELD)).build(consumer);

        ShapedRecipeBuilder.shapedRecipe(ModItems.LIGHT_GRAY_UMBRELLA)
                .key('a', Blocks.LIGHT_GRAY_WOOL)
                .key('b', Items.SHIELD)
                .key('c', Items.IRON_INGOT)
                .patternLine("aba").patternLine(" c ").patternLine(" c ")
                .addCriterion("has_shield", hasItem(Items.SHIELD)).build(consumer);

        ShapedRecipeBuilder.shapedRecipe(ModItems.CYAN_UMBRELLA)
                .key('a', Blocks.CYAN_WOOL)
                .key('b', Items.SHIELD)
                .key('c', Items.IRON_INGOT)
                .patternLine("aba").patternLine(" c ").patternLine(" c ")
                .addCriterion("has_shield", hasItem(Items.SHIELD)).build(consumer);

        ShapedRecipeBuilder.shapedRecipe(ModItems.PURPLE_UMBRELLA)
                .key('a', Blocks.PURPLE_WOOL)
                .key('b', Items.SHIELD)
                .key('c', Items.IRON_INGOT)
                .patternLine("aba").patternLine(" c ").patternLine(" c ")
                .addCriterion("has_shield", hasItem(Items.SHIELD)).build(consumer);

        ShapedRecipeBuilder.shapedRecipe(ModItems.BLUE_UMBRELLA)
                .key('a', Blocks.BLUE_WOOL)
                .key('b', Items.SHIELD)
                .key('c', Items.IRON_INGOT)
                .patternLine("aba").patternLine(" c ").patternLine(" c ")
                .addCriterion("has_shield", hasItem(Items.SHIELD)).build(consumer);

        ShapedRecipeBuilder.shapedRecipe(ModItems.BROWN_UMBRELLA)
                .key('a', Blocks.BROWN_WOOL)
                .key('b', Items.SHIELD)
                .key('c', Items.IRON_INGOT)
                .patternLine("aba").patternLine(" c ").patternLine(" c ")
                .addCriterion("has_shield", hasItem(Items.SHIELD)).build(consumer);

        ShapedRecipeBuilder.shapedRecipe(ModItems.GREEN_UMBRELLA)
                .key('a', Blocks.GREEN_WOOL)
                .key('b', Items.SHIELD)
                .key('c', Items.IRON_INGOT)
                .patternLine("aba").patternLine(" c ").patternLine(" c ")
                .addCriterion("has_shield", hasItem(Items.SHIELD)).build(consumer);

        ShapedRecipeBuilder.shapedRecipe(ModItems.RED_UMBRELLA)
                .key('a', Blocks.RED_WOOL)
                .key('b', Items.SHIELD)
                .key('c', Items.IRON_INGOT)
                .patternLine("aba").patternLine(" c ").patternLine(" c ")
                .addCriterion("has_shield", hasItem(Items.SHIELD)).build(consumer);

        ShapedRecipeBuilder.shapedRecipe(ModItems.BLACK_UMBRELLA)
                .key('a', Blocks.BLACK_WOOL)
                .key('b', Items.SHIELD)
                .key('c', Items.IRON_INGOT)
                .patternLine("aba").patternLine(" c ").patternLine(" c ")
                .addCriterion("has_shield", hasItem(Items.SHIELD)).build(consumer);

        ShapedRecipeBuilder.shapedRecipe(ModItems.GILDED_UMBRELLA)
                .key('a', Items.GOLD_INGOT)
                .key('b', Items.SHIELD)
                .key('c', Items.IRON_INGOT)
                .patternLine("aba").patternLine(" c ").patternLine(" c ")
                .addCriterion("has_shield", hasItem(Items.SHIELD)).build(consumer);

        ShapedRecipeBuilder.shapedRecipe(ModItems.AGED_UMBRELLA)
                .key('a', Items.IRON_INGOT)
                .key('b', Items.SHIELD)
                .key('c', Items.IRON_INGOT)
                .patternLine("aba").patternLine(" c ").patternLine(" c ")
                .addCriterion("has_shield", hasItem(Items.SHIELD)).build(consumer);


        ShapedRecipeBuilder.shapedRecipe(ModItems.COMPOUND_BREWING_STAND)
                .key('a', Blocks.HOPPER)
                .key('b', Items.CRYING_OBSIDIAN)
                .key('c', Items.BREWING_STAND)
                .key('d', Items.BASALT)
                .patternLine(" a ").patternLine("bcb").patternLine("ddd")
                .addCriterion("has_shield", hasItem(Items.SHIELD)).build(consumer);

        ShapedRecipeBuilder.shapedRecipe(ModItems.REINFORCED_CAULDRON)
                .key('a', Blocks.CAULDRON)
                .key('b', Items.GOLD_INGOT)
                .patternLine("b b").patternLine("bab").patternLine("b b")
                .addCriterion("has_gold", hasItem(Items.GOLD_INGOT)).build(consumer);

        smithingReinforce(consumer, ModItems.RUDIMENTARY_GAUNTLET, ModItems.NETHERITE_GAUNTLET);

        ShapedRecipeBuilder.shapedRecipe(ModItems.POTION_INJECTOR)
                .key('a', Blocks.DIORITE)
                .key('b', Items.STONE_BUTTON)
                .key('c', Items.LEATHER)
                .key('d', Items.GLASS)
                .patternLine("aba").patternLine("cac").patternLine("dad")
                .addCriterion("has_leather", hasItem(Items.LEATHER)).build(consumer);

        ShapedRecipeBuilder.shapedRecipe(ModItems.POTIONEER_GAUNTLET)
                .key('a', Items.GLASS_BOTTLE)
                .key('b', Items.HOPPER)
                .key('c', ModItems.NETHERITE_GAUNTLET)
                .key('d', Items.LEVER)
                .patternLine("aba").patternLine("aca").patternLine("ada")
                .addCriterion("has_netherite_gauntlet", hasItem(ModItems.NETHERITE_GAUNTLET)).build(consumer);

        ShapedRecipeBuilder.shapedRecipe(ModItems.RUDIMENTARY_GAUNTLET)
                .key('a', Items.IRON_INGOT)
                .key('b', Items.GOLD_INGOT)
                .patternLine("abb").patternLine("aab")
                .addCriterion("has_gold_ingot", hasItem(Items.GOLD_INGOT)).build(consumer);
    }
}
