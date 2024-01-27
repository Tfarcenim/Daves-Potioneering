package tfar.davespotioneering;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import tfar.davespotioneering.block.ModCauldronInteractions;
import tfar.davespotioneering.config.DavesPotioneeringClothConfig;
import tfar.davespotioneering.init.*;
import tfar.davespotioneering.net.PacketHandler;

public class DavesPotioneeringFabric implements ModInitializer {
    // Directly reference a log4j logger.

    public static DavesPotioneeringClothConfig CONFIG;

    @Override
    public void onInitialize() {
        DavesPotioneering.earlySetup();
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB,new ResourceLocation(DavesPotioneering.MODID,"tab"),ModCreativeTab.DAVESPOTIONEERING);

      //  UseItemCallback.EVENT.register(FabricEvents::potionCooldown);
        UseEntityCallback.EVENT.register(FabricEvents::milkCow);
        AttackEntityCallback.EVENT.register(FabricEvents::afterHit);

        AutoConfig.register(DavesPotioneeringClothConfig.class, JanksonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(DavesPotioneeringClothConfig.class).getConfig();

        Util.setStackSize(Items.POTION, DavesPotioneeringFabric.CONFIG.potion_stack_size);
        Util.setStackSize(Items.SPLASH_POTION, DavesPotioneeringFabric.CONFIG.splash_potion_stack_size);
        Util.setStackSize(Items.LINGERING_POTION, DavesPotioneeringFabric.CONFIG.lingering_potion_stack_size);
        CommonLifecycleEvents.TAGS_LOADED.register((registries, client) -> DavesPotioneering.tagsUpdated());

        PacketHandler.registerMessages();
        DavesPotioneering.commonSetup();
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
