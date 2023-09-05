package tfar.davespotioneering;

import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.RegisterEvent;
import org.apache.commons.lang3.tuple.Pair;
import tfar.davespotioneering.block.ModCauldronInteractions;
import tfar.davespotioneering.client.ClientEvents;
import tfar.davespotioneering.client.GauntletHUD;
import tfar.davespotioneering.datagen.ModDatagen;
import tfar.davespotioneering.effect.PotionIngredient;
import tfar.davespotioneering.init.*;
import tfar.davespotioneering.mixin.BlockEntityTypeAcces;
import tfar.davespotioneering.net.PacketHandler;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(DavesPotioneering.MODID)
public class DavesPotioneering {
    // Directly reference a log4j logger.

    public static final String MODID = "davespotioneering";
    public static final boolean DEBUG = !FMLEnvironment.production;

    public DavesPotioneering() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        bus.addListener(ModDatagen::start);
        bus.addListener(this::register);

        ModLoadingContext.get().registerConfig(Type.CLIENT, CLIENT_SPEC);
        ModLoadingContext.get().registerConfig(Type.SERVER, SERVER_SPEC);

        // Register the setup method for modloading
        bus.addListener(this::setup);
        MinecraftForge.EVENT_BUS.addListener(this::stackAdj);
        MinecraftForge.EVENT_BUS.addListener(this::stackAdj1);
        if (FMLEnvironment.dist.isClient()) {
            // Register the doClientStuff method for modloading
            bus.addListener(ClientEvents::doClientStuff);
            bus.addListener(ClientEvents::registerLoader);
            bus.addListener(ClientEvents::particle);
            bus.addListener(ClientEvents::overlay);
            bus.addListener(GauntletHUD::bake);
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

    private void register(RegisterEvent e) {
        superRegister(e, ModBlocks.class, Registry.BLOCK_REGISTRY, Block.class);
        superRegister(e, ModItems.class, Registry.ITEM_REGISTRY, Item.class);
        superRegister(e, ModBlockEntityTypes.class, Registry.BLOCK_ENTITY_TYPE_REGISTRY, BlockEntityType.class);
        superRegister(e, ModMenuTypes.class, Registry.MENU_REGISTRY, MenuType.class);
        superRegister(e, ModEffects.class, Registry.MOB_EFFECT_REGISTRY, MobEffect.class);
        superRegister(e, ModParticleTypes.class, Registry.PARTICLE_TYPE_REGISTRY, ParticleType.class);
        superRegister(e, ModPotions.class, Registry.POTION_REGISTRY, Potion.class);
        superRegister(e, ModSoundEvents.class, Registry.SOUND_EVENT_REGISTRY, SoundEvent.class);
    }

    public static <T> void superRegister(RegisterEvent e, Class<?> clazz, ResourceKey<? extends Registry<T>> resourceKey, Class<?> filter) {
        for (Field field : clazz.getFields()) {
            try {
                Object o = field.get(null);
                if (filter.isInstance(o)) {
                    e.register(resourceKey, new ResourceLocation(MODID, field.getName().toLowerCase(Locale.ROOT)), () -> (T) o);
                }
            } catch (IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
            }
        }
    }

    private void setup(final FMLCommonSetupEvent event) {
        Util.setStackSize(Items.POTION, 16);
        Util.setStackSize(Items.SPLASH_POTION, 4);
        Util.setStackSize(Items.LINGERING_POTION, 4);

        Events.register();

        ItemStack milkPot = new ItemStack(Items.POTION);
        PotionUtils.setPotion(milkPot, ModPotions.MILK);

        ItemStack splashMilkPot = new ItemStack(Items.SPLASH_POTION);
        PotionUtils.setPotion(splashMilkPot, ModPotions.MILK);

        ItemStack lingerMilkPot = new ItemStack(Items.LINGERING_POTION);
        PotionUtils.setPotion(lingerMilkPot, ModPotions.MILK);

        BrewingRecipeRegistry.addRecipe(PotionIngredient.create(milkPot), Ingredient.of(Items.GUNPOWDER), splashMilkPot);

        BrewingRecipeRegistry.addRecipe(PotionIngredient.create(milkPot), Ingredient.of(Items.DRAGON_BREATH), lingerMilkPot);

        strongRecipe(Potions.INVISIBILITY, ModPotions.STRONG_INVISIBILITY);

        Set<Block> newSet = new HashSet<>(((BlockEntityTypeAcces) BlockEntityType.LECTERN).getValidBlocks());
        newSet.add(ModBlocks.MAGIC_LECTERN);
        ((BlockEntityTypeAcces) BlockEntityType.LECTERN).setValidBlocks(newSet);

        PacketHandler.registerMessages();

        ModCauldronInteractions.bootStrap();

    }

    private void stackAdj(ServerStartingEvent e) {
        Util.setStackSize(Items.POTION, ModConfig.Server.potion_stack_size.get());
        Util.setStackSize(Items.SPLASH_POTION, ModConfig.Server.splash_potion_stack_size.get());
        Util.setStackSize(Items.LINGERING_POTION, ModConfig.Server.lingering_potion_stack_size.get());
    }

    protected static void strongRecipe(Potion potion, Potion strong) {
        BrewingRecipeRegistry.addRecipe(PotionIngredient.create(
                        PotionUtils.setPotion(new ItemStack(Items.POTION), potion)),
                Ingredient.of(Items.GLOWSTONE_DUST),
                PotionUtils.setPotion(new ItemStack(Items.POTION), strong));
    }
}
