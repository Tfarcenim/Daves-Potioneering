package tfar.davespotioneering;

import net.fabricmc.api.ModInitializer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import tfar.davespotioneering.effect.PotionIngredient;
import tfar.davespotioneering.init.*;
import tfar.davespotioneering.mixin.BlockEntityTypeAcces;
import tfar.davespotioneering.net.PacketHandler;

import java.util.HashSet;
import java.util.Set;

public class DavesPotioneering implements ModInitializer {
    // Directly reference a log4j logger.

    public static final String MODID = "davespotioneering";

    @Override
    public void onInitialize() {
        ModBlocks.register();
        ModItems.register();
        ModEffects.register();
        ModPotions.register();
        ModBlockEntityTypes.register();
        ModContainerTypes.register();
        ModSoundEvents.register();
        ModParticleTypes.register();

        Util.setStackSize(Items.POTION,16);
        Util.setStackSize(Items.SPLASH_POTION,4);
        Util.setStackSize(Items.LINGERING_POTION,4);

        ItemStack milkPot = new ItemStack(Items.POTION);
        PotionUtils.setPotion(milkPot,ModPotions.MILK);

        ItemStack splashMilkPot = new ItemStack(Items.SPLASH_POTION);
        PotionUtils.setPotion(splashMilkPot,ModPotions.MILK);

        ItemStack lingerMilkPot = new ItemStack(Items.LINGERING_POTION);
        PotionUtils.setPotion(lingerMilkPot,ModPotions.MILK);

        BrewingRecipeRegistry.addRecipe(PotionIngredient.create(milkPot),Ingredient.of(Items.GUNPOWDER),splashMilkPot);

        BrewingRecipeRegistry.addRecipe(PotionIngredient.create(milkPot),Ingredient.of(Items.DRAGON_BREATH),lingerMilkPot);

        strongRecipe(Potions.INVISIBILITY,ModPotions.STRONG_INVISIBILITY);

        Set<Block> newSet = new HashSet<>(((BlockEntityTypeAcces)BlockEntityType.LECTERN).getValidBlocks());
        newSet.add(ModBlocks.MAGIC_LECTERN);
        ((BlockEntityTypeAcces)BlockEntityType.LECTERN).setValidBlocks(newSet);

        PacketHandler.registerMessages();
    }

    protected static void strongRecipe(Potion potion,Potion strong) {
        BrewingRecipeRegistry.addRecipe(PotionIngredient.create(
                        PotionUtils.setPotion(new ItemStack(Items.POTION),potion)),
                        Ingredient.of(Items.GLOWSTONE_DUST),
                        PotionUtils.setPotion(new ItemStack(Items.POTION), strong));
    }


}
