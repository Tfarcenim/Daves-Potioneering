package tfar.davespotioneering.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.glfw.GLFW;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.block.LayeredReinforcedCauldronBlock;
import tfar.davespotioneering.blockentity.ReinforcedCauldronBlockEntity;
import tfar.davespotioneering.client.model.gecko.DoubleGeoItemStackRenderer;
import tfar.davespotioneering.client.model.gecko.GeoItemModel;
import tfar.davespotioneering.client.particle.FastDripParticle;
import tfar.davespotioneering.client.particle.TintedSplashParticle;
import tfar.davespotioneering.init.*;
import tfar.davespotioneering.item.GauntletItem;
import tfar.davespotioneering.mixin.ParticleManagerAccess;
import tfar.davespotioneering.net.C2SGauntletCyclePacket;
import com.mojang.blaze3d.platform.InputConstants;

import java.util.List;
import java.util.Locale;

public class ClientEvents implements ClientModInitializer {

    public static KeyMapping CONFIG = new KeyMapping("key.davespotioneering.open_config",
            InputConstants.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_3,"key.categories."+ DavesPotioneering.MODID);
    @Override
    public void onInitializeClient() {

        ParticleFactoryRegistry.getInstance().register(ModParticleTypes.FAST_DRIPPING_WATER,FastDripParticle.DrippingWaterFactory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticleTypes.FAST_FALLING_WATER, FastDripParticle.FallingWaterFactory::new);
       ParticleFactoryRegistry.getInstance().register(ModParticleTypes.TINTED_SPLASH, TintedSplashParticle.Factory::new);

        KeyBindingHelper.registerKeyBinding(CONFIG);

        ItemTooltipCallback.EVENT.register(ClientEvents::tooltips);
        HudRenderCallback.EVENT.register(ClientEvents::gauntletHud);
        ClientTickEvents.START_CLIENT_TICK.register(ClientEvents::playerTick);


        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.COMPOUND_BREWING_STAND, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.POTION_INJECTOR,RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.REINFORCED_WATER_CAULDRON,RenderType.translucent());

        MenuScreens.register(ModContainerTypes.ADVANCED_BREWING_STAND, AdvancedBrewingStandScreen::new);
        MenuScreens.register(ModContainerTypes.ALCHEMICAL_GAUNTLET, PotionInjectorScreen::new);

        BlockEntityRenderers.register(ModBlockEntityTypes.POTION_INJECTOR, PotionInjectorRenderer::new);

        ColorProviderRegistry.BLOCK.register((state, reader, pos, index) -> {
            if (pos != null) {
                BlockEntity blockEntity = reader.getBlockEntity(pos);
                if (blockEntity instanceof ReinforcedCauldronBlockEntity) {
                    return ((ReinforcedCauldronBlockEntity) blockEntity).getColor();
                }
            }
            return 0xffffff;
        }, ModBlocks.REINFORCED_WATER_CAULDRON);

       ItemProperties.register(ModItems.POTIONEER_GAUNTLET, new ResourceLocation("active"),GAUNTLET
                );

        registerBlockingProperty(ModItems.WHITE_UMBRELLA);
        registerBlockingProperty(ModItems.ORANGE_UMBRELLA);
        registerBlockingProperty(ModItems.MAGENTA_UMBRELLA);
        registerBlockingProperty(ModItems.LIGHT_BLUE_UMBRELLA);
        registerBlockingProperty(ModItems.YELLOW_UMBRELLA);
        registerBlockingProperty(ModItems.LIME_UMBRELLA);
        registerBlockingProperty(ModItems.PINK_UMBRELLA);
        registerBlockingProperty(ModItems.GRAY_UMBRELLA);
        registerBlockingProperty(ModItems.LIGHT_GRAY_UMBRELLA);
        registerBlockingProperty(ModItems.CYAN_UMBRELLA);
        registerBlockingProperty(ModItems.PURPLE_UMBRELLA);
        registerBlockingProperty(ModItems.BLUE_UMBRELLA);
        registerBlockingProperty(ModItems.BROWN_UMBRELLA);
        registerBlockingProperty(ModItems.GREEN_UMBRELLA);
        registerBlockingProperty(ModItems.RED_UMBRELLA);
        registerBlockingProperty(ModItems.BLACK_UMBRELLA);

        registerBlockingProperty(ModItems.AGED_UMBRELLA);
        registerBlockingProperty(ModItems.GILDED_UMBRELLA);

        //BuiltinItemRendererRegistry.INSTANCE.register(ModItems.AGED_UMBRELLA,createAgedUmbrellaItemStackRenderer());

    }

    public static final ClampedItemPropertyFunction GAUNTLET = (stack, level, entity, i) -> stack.hasTag() ? stack.getTag().getBoolean("active") ? 1 : 0 : 0;


    public static GeoItemRenderer umbrella(String s) {
        return createGeoClassicUmbrellaItemStackRenderer(s);
    }

    public static BlockEntityWithoutLevelRenderer classicUmbrella(DyeColor dyeColor) {
        return umbrella(dyeColor.name().toLowerCase(Locale.ROOT));
    }

    private static GeoItemRenderer createGeoClassicUmbrellaItemStackRenderer(String itemName) {
        return new DoubleGeoItemStackRenderer<>(
                GeoItemModel.makeClosedUmbrella(itemName),
                GeoItemModel.makeOpenUmbrella(itemName));
    }

    private static BlockEntityWithoutLevelRenderer  createAgedUmbrellaItemStackRenderer() {
        return new DoubleGeoItemStackRenderer<>(
                GeoItemModel.makeClosedUmbrella("aged"),
                GeoItemModel.makeOpenAgedUmbrella());
    }

    public static void switchGameMode(GameType oldGameType, GameType newGameType) {
        if (newGameType == GameType.SURVIVAL && DavesPotioneering.CONFIG.gauntlet_hud_preset == GauntletHUD.Preset.ABOVE_HOTBAR) {
            DavesPotioneering.CONFIG.gauntlet_hud_x = getFixedPositionValue(Minecraft.getInstance().getWindow().getGuiScaledHeight() - 42 - 40, false);
        }
        if (newGameType == GameType.CREATIVE && DavesPotioneering.CONFIG.gauntlet_hud_preset == GauntletHUD.Preset.ABOVE_HOTBAR) {
            DavesPotioneering.CONFIG.gauntlet_hud_y = getFixedPositionValue(Minecraft.getInstance().getWindow().getGuiScaledHeight() - 42 - 25, false);
        }
    }

    public static int getFixedPositionValue(int value, boolean isWidth) {
        return isWidth ? value * 2 - Minecraft.getInstance().getWindow().getGuiScaledWidth() : value -
                Minecraft.getInstance().getWindow().getGuiScaledHeight();
    }
  //  public static void registerLoader(final ModelRegistryEvent event) {
  //      ModelLoaderRegistry.registerLoader(new ResourceLocation(MODID, "fullbright"), ModelLoader.INSTANCE);
 //   }

    public static void onMouseInput(long handle, int button, int action, int mods) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        ItemStack held = player.getMainHandItem();
        if (held.isEmpty()) return;
        if (held.getItem() instanceof GauntletItem && player.isShiftKeyDown()) {
            if (button == GLFW.GLFW_MOUSE_BUTTON_3) {
                GauntletHUDMovementScreen.open();
            }
        }
    }

    public static boolean onMouseScroll(double scrollDelta) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return false;
        ItemStack held = player.getMainHandItem();
        if (held.isEmpty()) return false;
        if (held.getItem() instanceof GauntletItem && player.isShiftKeyDown()) {
            if (scrollDelta == 1.f) {
                C2SGauntletCyclePacket.encode(true);
                GauntletHUD.backwardCycle();
            } else {
                C2SGauntletCyclePacket.encode(false);
                GauntletHUD.forwardCycle();
            }
            return true;
        }
        return false;
    }

    public static void tooltips(ItemStack stack, TooltipFlag e2, List<Component> tooltip) {
        if (!dontTooltip(stack) && PotionUtils.getPotion(stack) != Potions.EMPTY) {
            if (stack.getItem().isEdible()) {
                if (DavesPotioneering.CONFIG.show_spiked_food) {
                    tooltip.add(Component.literal("Spiked with"));
                    PotionUtils.addPotionTooltip(stack, tooltip, 0.125F);
                }
            } else {
                tooltip.add(Component.literal("Coated with"));
                PotionUtils.addPotionTooltip(stack, tooltip, 0.125F);
                tooltip.add(Component.literal("Uses: " + stack.getTag().getInt(LayeredReinforcedCauldronBlock.USES)));
            }
        }
    }

    public static boolean dontTooltip(ItemStack stack) {
        return stack.getItem() instanceof PotionItem || stack.getItem() instanceof ArrowItem;
    }

    private static void registerBlockingProperty(Item item) {
        ItemProperties.register(item, new ResourceLocation("blocking"),
                (stack, world, entity,i) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);
    }

    public static void gauntletHud(GuiGraphics matrixStack, float tickDelta) {
        // only renders when the hotbar renders
        //            if (Minecraft.getInstance().currentScreen != null) return;
        // get player from client
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        ItemStack g = player.getMainHandItem();
        // check if holding gauntlet
        if (g.getItem() instanceof GauntletItem) {
            // get nbt
            CompoundTag info = player.getMainHandItem().getOrCreateTag().getCompound("info");
            Potion[] potions = GauntletItem.getPotionsFromNBT(info);
            GauntletHUD.render(matrixStack);
            if (potions == null) {
                // reset
                GauntletHUD.init(null, null, null);
                return;
            }
            GauntletHUD.init(potions[0], potions[1], potions[2]);
        }
    }

    public static void playerTick(Minecraft e) {
        Player player = e.player;
        if (player != null && player.level().getGameTime() % DavesPotioneering.CONFIG.particle_drip_rate == 0) {

            ItemStack stack = player.getMainHandItem();

            if (stack.getItem() instanceof TieredItem && PotionUtils.getPotion(stack) != Potions.EMPTY) {


                ParticleOptions particleData = ModParticleTypes.FAST_DRIPPING_WATER;

                Vec3 vec = player.position().add(0, +player.getBbHeight() / 2, 0);

                double yaw = -Mth.wrapDegrees(player.getYRot());

                double of1 = Math.random() * .60 + .15;
                double of2 = .40 + Math.random() * .10;


                double z1 = Math.cos(yaw * Math.PI / 180) * of1;
                double x1 = Math.sin(yaw * Math.PI / 180) * of1;

                double z2 = Math.cos((yaw + 270) * Math.PI / 180) * of2;
                double x2 = Math.sin((yaw + 270) * Math.PI / 180) * of2;

                vec = vec.add(x1 + x2, 0, z1 + z2);

                int color = PotionUtils.getColor(stack);
                spawnFluidParticle(Minecraft.getInstance().level, vec, particleData, color);
            }
        }
    }

    private static void spawnFluidParticle(ClientLevel world, Vec3 blockPosIn, ParticleOptions particleDataIn, int color) {
        // world.spawnParticle(new BlockPos(blockPosIn), particleDataIn, voxelshape, blockPosIn.getY() +.5);

        Particle particle = ((ParticleManagerAccess) Minecraft.getInstance().particleEngine).$makeParticle(particleDataIn, blockPosIn.x, blockPosIn.y, blockPosIn.z, 0, -.10, 0);

        float red = (color >> 16 & 0xff) / 255f;
        float green = (color >> 8 & 0xff) / 255f;
        float blue = (color & 0xff) / 255f;

        particle.setColor(red, green, blue);

        Minecraft.getInstance().particleEngine.add(particle);

        //world.addParticle(particleDataIn,blockPosIn.x,blockPosIn.y,blockPosIn.z,0,-.10,0);
    }

}
