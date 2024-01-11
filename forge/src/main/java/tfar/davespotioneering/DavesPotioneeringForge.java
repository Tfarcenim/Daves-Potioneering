package tfar.davespotioneering;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.RegisterEvent;
import org.apache.commons.lang3.tuple.Pair;
import tfar.davespotioneering.block.ModCauldronInteractions;
import tfar.davespotioneering.client.ClientEvents;
import tfar.davespotioneering.datagen.ModDatagen;
import tfar.davespotioneering.effect.PotionIngredient;
import tfar.davespotioneering.init.*;
import tfar.davespotioneering.item.GauntletItem;
import tfar.davespotioneering.net.PacketHandler;

import java.util.*;
import java.util.function.Supplier;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(DavesPotioneering.MODID)
public class DavesPotioneeringForge {
    // Directly reference a log4j logger.
    public DavesPotioneeringForge() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        bus.addListener(ModDatagen::start);
        bus.addListener(this::register);

        ModLoadingContext.get().registerConfig(Type.CLIENT, CLIENT_SPEC);
        ModLoadingContext.get().registerConfig(Type.SERVER, SERVER_SPEC);

        // Register the setup method for modloading
        bus.addListener(this::setup);
        MinecraftForge.EVENT_BUS.addListener(this::stackAdj);
        MinecraftForge.EVENT_BUS.addListener(GauntletItem::tickCooldowns);
        if (FMLEnvironment.dist.isClient()) {
            // Register the doClientStuff method for modloading
            bus.addListener(ClientEvents::doClientStuff);
            bus.addListener(ClientEvents::registerLoader);
            bus.addListener(ClientEvents::particle);
            bus.addListener(ClientEvents::overlay);
            //bus.addListener(GauntletHUDForge::bake);
        }
        DavesPotioneering.earlySetup();
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

    public static Map<Registry<?>,List<Pair<ResourceLocation, Supplier<?>>>> registerLater = new HashMap<>();
    private void register(RegisterEvent e) {
        for (Map.Entry<Registry<?>,List<Pair<ResourceLocation, Supplier<?>>>> entry : registerLater.entrySet()) {
            Registry<?> registry = entry.getKey();
            List<Pair<ResourceLocation, Supplier<?>>> toRegister = entry.getValue();
            for (Pair<ResourceLocation,Supplier<?>> pair : toRegister) {
                e.register((ResourceKey<? extends Registry<Object>>)registry.key(),pair.getLeft(),(Supplier<Object>)pair.getValue());
            }
        }

        e.register(Registries.CREATIVE_MODE_TAB,new ResourceLocation(DavesPotioneering.MODID, DavesPotioneering.MODID),() -> ModCreativeTab.DAVESPOTIONEERING);
    }

    private void setup(final FMLCommonSetupEvent event) {
        ForgeEvents.register();

        ItemStack milkPot = new ItemStack(Items.POTION);
        PotionUtils.setPotion(milkPot,ModPotions.MILK);

        ItemStack splashMilkPot = new ItemStack(Items.SPLASH_POTION);
        PotionUtils.setPotion(splashMilkPot,ModPotions.MILK);

        ItemStack lingerMilkPot = new ItemStack(Items.LINGERING_POTION);
        PotionUtils.setPotion(lingerMilkPot,ModPotions.MILK);

        BrewingRecipeRegistry.addRecipe(PotionIngredient.create(milkPot),Ingredient.of(Items.GUNPOWDER),splashMilkPot);
        BrewingRecipeRegistry.addRecipe(PotionIngredient.create(milkPot),Ingredient.of(Items.DRAGON_BREATH),lingerMilkPot);

        strongRecipe(Potions.INVISIBILITY,ModPotions.STRONG_INVISIBILITY);

        PacketHandler.registerMessages();
        ModCauldronInteractions.bootStrap();



        DavesPotioneering.commonSetup();
    }

    private void stackAdj(ServerStartingEvent e) {
        Util.setStackSize(Items.POTION, ModConfig.Server.potion_stack_size.get());
        Util.setStackSize(Items.SPLASH_POTION, ModConfig.Server.splash_potion_stack_size.get());
        Util.setStackSize(Items.LINGERING_POTION, ModConfig.Server.lingering_potion_stack_size.get());
    }


    protected static void strongRecipe(Potion potion,Potion strong) {
        BrewingRecipeRegistry.addRecipe(PotionIngredient.create(
                        PotionUtils.setPotion(new ItemStack(Items.POTION),potion)),
                        Ingredient.of(Items.GLOWSTONE_DUST),
                        PotionUtils.setPotion(new ItemStack(Items.POTION), strong));
    }
}
