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
        ShapedRecipeBuilder.shapedRecipe(ModItems.UMBRELLA)
                .key('a', Blocks.RED_WOOL)
                .key('b', Items.SHIELD)
                .key('c', Items.IRON_INGOT)
                .patternLine("aba").patternLine(" c ").patternLine(" c ")
                .addCriterion("has_shield", hasItem(Items.SHIELD)).build(consumer);

        ShapedRecipeBuilder.shapedRecipe(ModItems.GENTLEMAN_UMBRELLA)
                .key('a', Blocks.BLACK_WOOL)
                .key('b', Items.SHIELD)
                .key('c', Items.IRON_INGOT)
                .patternLine("aba").patternLine(" c ").patternLine(" c ")
                .addCriterion("has_shield", hasItem(Items.SHIELD)).build(consumer);

        ShapedRecipeBuilder.shapedRecipe(ModItems.CLEAR_UMBRELLA)
                .key('a', Items.GLASS)
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
