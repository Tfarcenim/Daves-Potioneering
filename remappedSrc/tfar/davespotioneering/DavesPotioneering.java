package tfar.davespotioneering;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import tfar.davespotioneering.block.ModCauldronInteractions;
import tfar.davespotioneering.config.ClothConfig;
import tfar.davespotioneering.init.*;
import tfar.davespotioneering.mixin.BlockEntityTypeAcces;
import tfar.davespotioneering.net.PacketHandler;

import java.util.HashSet;
import java.util.Set;

public class DavesPotioneering implements ModInitializer {
    // Directly reference a log4j logger.

    public static final String MODID = "davespotioneering";
    public static ClothConfig CONFIG;

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

        Set<Block> newSet = new HashSet<>(((BlockEntityTypeAcces)BlockEntityType.LECTERN).getBlocks());
        newSet.add(ModBlocks.MAGIC_LECTERN);
        ((BlockEntityTypeAcces)BlockEntityType.LECTERN).setBlocks(newSet);


      //  UseItemCallback.EVENT.register(Events::potionCooldown);
        UseEntityCallback.EVENT.register(Events::milkCow);
        AttackEntityCallback.EVENT.register(Events::afterHit);

        AutoConfig.register(ClothConfig.class, JanksonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(ClothConfig.class).getConfig();

        Util.setStackSize(Items.POTION,DavesPotioneering.CONFIG.potion_stack_size);
        Util.setStackSize(Items.SPLASH_POTION,DavesPotioneering.CONFIG.splash_potion_stack_size);
        Util.setStackSize(Items.LINGERING_POTION,DavesPotioneering.CONFIG.lingering_potion_stack_size);

        PacketHandler.registerMessages();
        ModCauldronInteractions.bootStrap();
    }

    protected static void strongRecipe(Potion potion,Potion strong) {
        PotionBrewing.addMix(potion, Items.GLOWSTONE_DUST, strong);
    }

    protected static void extendedRecipe(Potion potion,Potion extended) {
        PotionBrewing.addMix(potion, Items.REDSTONE, extended);
    }

    protected static void splashRecipe(Potion potion,Potion splash) {
        PotionBrewing.addMix(potion, Items.GUNPOWDER, splash);
    }

    protected static void lingerRecipe(Potion potion,Potion splash) {
        PotionBrewing.addMix(potion, Items.DRAGON_BREATH, splash);
    }

    public static void addPotions() {
        strongRecipe(Potions.INVISIBILITY,ModPotions.STRONG_INVISIBILITY);

        ItemStack milkPot = new ItemStack(Items.POTION);
        PotionUtils.setPotion(milkPot,ModPotions.MILK);

        ItemStack splashMilkPot = new ItemStack(Items.SPLASH_POTION);
        PotionUtils.setPotion(splashMilkPot,ModPotions.MILK);

        ItemStack lingerMilkPot = new ItemStack(Items.LINGERING_POTION);
        PotionUtils.setPotion(lingerMilkPot,ModPotions.MILK);

     //   splashRecipe(ModPotions.MILK,splashMilkPot);

      //  lingerRecipe(ModPotions.MILK,lingerMilkPot);
    }
}
