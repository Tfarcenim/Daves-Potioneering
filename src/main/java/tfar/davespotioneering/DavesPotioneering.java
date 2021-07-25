package tfar.davespotioneering;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.commons.lang3.tuple.Pair;
import tfar.davespotioneering.blockentity.ReinforcedCauldronBlockEntity;
import tfar.davespotioneering.client.AdvancedBrewingStandScreen;
import tfar.davespotioneering.datagen.ModDatagen;
import tfar.davespotioneering.init.*;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(DavesPotioneering.MODID)
public class DavesPotioneering {
    // Directly reference a log4j logger.

    public static final String MODID = "davespotioneering";

    public DavesPotioneering() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        bus.addListener(ModDatagen::start);

        bus.addGenericListener(Block.class, ModBlocks::register);
        bus.addGenericListener(Item.class, ModItems::register);
        bus.addGenericListener(Effect.class,ModEffects::register);
        bus.addGenericListener(Potion.class,ModPotions::register);
        bus.addGenericListener(TileEntityType.class, ModBlockEntityTypes::register);
        bus.addGenericListener(ContainerType.class, ModContainerTypes::register);

        ModLoadingContext.get().registerConfig(Type.CLIENT, CLIENT_SPEC);
        ModLoadingContext.get().registerConfig(Type.SERVER, SERVER_SPEC);

        // Register the setup method for modloading
        bus.addListener(this::setup);
        // Register the doClientStuff method for modloading
        bus.addListener(this::doClientStuff);
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

        ItemStack milkPot = new ItemStack(Items.POTION);
        PotionUtils.addPotionToItemStack(milkPot,ModPotions.MILK);

        ItemStack splashMilkPot = new ItemStack(Items.SPLASH_POTION);
        PotionUtils.addPotionToItemStack(splashMilkPot,ModPotions.MILK);

        ItemStack lingerMilkPot = new ItemStack(Items.LINGERING_POTION);
        PotionUtils.addPotionToItemStack(lingerMilkPot,ModPotions.MILK);

        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(Ingredient.fromStacks(milkPot),Ingredient.fromItems(Items.GUNPOWDER),splashMilkPot));

        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(Ingredient.fromStacks(milkPot),Ingredient.fromItems(Items.DRAGON_BREATH),lingerMilkPot));

    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.addListener(ClientEvents::playSound);
        MinecraftForge.EVENT_BUS.addListener(ClientEvents::tooltips);
        RenderTypeLookup.setRenderLayer(ModBlocks.ADVANCED_BREWING_STAND, RenderType.getCutoutMipped());
        ScreenManager.registerFactory(ModContainerTypes.ADVANCED_BREWING_STAND, AdvancedBrewingStandScreen::new);

        Minecraft.getInstance().getBlockColors().register((state, reader, pos, index) -> {
            if (pos != null) {
                TileEntity blockEntity = reader.getTileEntity(pos);
                if (blockEntity instanceof ReinforcedCauldronBlockEntity) {
                    return ((ReinforcedCauldronBlockEntity)blockEntity).getColor();
                }
            }
            return 0xffffff;
        },ModBlocks.REINFORCED_CAULDRON);
    }
}
