package tfar.davespotioneering;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.commons.lang3.tuple.Pair;
import tfar.davespotioneering.client.ClientEvents;
import tfar.davespotioneering.datagen.ModDatagen;
import tfar.davespotioneering.effect.PotionIngredient;
import tfar.davespotioneering.init.*;
import tfar.davespotioneering.mixin.BlockEntityTypeAcces;
import tfar.davespotioneering.net.PacketHandler;

import java.util.HashSet;
import java.util.Set;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(DavesPotioneering.MODID)
public class DavesPotioneering {
    // Directly reference a log4j logger.

    public static final String MODID = "davespotioneering";

    public DavesPotioneering() {


        ModBlocks.register();
        ModItems.register();
        ModEffects.register();
        ModPotions.register();
        ModBlockEntityTypes.register();
        ModContainerTypes.register();
        ModSoundEvents.register();
        ModParticleTypes.register();

        ModLoadingContext.get().registerConfig(Type.CLIENT, CLIENT_SPEC);
        ModLoadingContext.get().registerConfig(Type.SERVER, SERVER_SPEC);

        // Register the setup method for modloading
        bus.addListener(this::setup);
        if (FMLEnvironment.dist.isClient()) {
            // Register the doClientStuff method for modloading
            bus.addListener(ClientEvents::doClientStuff);
            bus.addListener(ClientEvents::registerLoader);
            bus.addListener(ClientEvents::particle);
        }
    }

    public static final ModConfig.Client CLIENT;
    public static final ForgeConfigSpec CLIENT_SPEC;

    public static final ModConfig.Server SERVER;
    public static final ForgeConfigSpec SERVER_SPEC;

    static {
        final Pair<ModConfig.Client, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ModConfig.Client::new);
        CLIENT_SPEC = specPair.getRight();
        CLIENT = specPair.getLeft();
        final Pair<ModConfig.Server, ForgeConfigSpec> specPair2 = new ForgeConfigSpec.Builder().configure(ModConfig.Server::new);
        SERVER_SPEC = specPair2.getRight();
        SERVER = specPair2.getLeft();
    }


    private void setup(final FMLCommonSetupEvent event) {
        Util.setStackSize(Items.POTION,16);
        Util.setStackSize(Items.SPLASH_POTION,4);
        Util.setStackSize(Items.LINGERING_POTION,4);
        MinecraftForge.EVENT_BUS.addListener(Events::potionCooldown);
        MinecraftForge.EVENT_BUS.addListener(Events::milkCow);
        MinecraftForge.EVENT_BUS.addListener(Events::afterHit);
        MinecraftForge.EVENT_BUS.addListener(Events::switchGameMode);

        MinecraftForge.EVENT_BUS.addListener(Events::playerBrew);
        MinecraftForge.EVENT_BUS.addListener(Events::canApplyEffect);

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
