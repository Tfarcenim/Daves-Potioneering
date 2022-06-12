package tfar.davespotioneering.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.UnclampedModelPredicateProvider;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.item.ToolItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.text.LiteralText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import org.lwjgl.glfw.GLFW;
import tfar.davespotioneering.Util;
import tfar.davespotioneering.blockentity.ReinforcedCauldronBlockEntity;
import tfar.davespotioneering.client.model.gecko.DoubleGeoItemStackRenderer;
import tfar.davespotioneering.client.model.gecko.GeoItemStackRenderer;
import tfar.davespotioneering.client.particle.FastDripParticle;
import tfar.davespotioneering.client.particle.TintedSplashParticle;
import tfar.davespotioneering.config.ClothConfig;
import tfar.davespotioneering.init.*;
import tfar.davespotioneering.item.GauntletItem;
import tfar.davespotioneering.mixin.ParticleManagerAccess;
import tfar.davespotioneering.net.C2SGauntletCyclePacket;

import java.util.List;
import java.util.Locale;

public class ClientEvents implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        ParticleFactoryRegistry.getInstance().register(ModParticleTypes.FAST_DRIPPING_WATER,FastDripParticle.DrippingWaterFactory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticleTypes.FAST_FALLING_WATER, FastDripParticle.FallingWaterFactory::new);
       ParticleFactoryRegistry.getInstance().register(ModParticleTypes.TINTED_SPLASH, TintedSplashParticle.Factory::new);

        ItemTooltipCallback.EVENT.register(ClientEvents::tooltips);
        HudRenderCallback.EVENT.register(ClientEvents::gauntletHud);
        ClientTickEvents.START_CLIENT_TICK.register(ClientEvents::playerTick);


        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.COMPOUND_BREWING_STAND, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.POTION_INJECTOR,RenderLayer.getTranslucent());
        HandledScreens.register(ModContainerTypes.ADVANCED_BREWING_STAND, AdvancedBrewingStandScreen::new);
        HandledScreens.register(ModContainerTypes.ALCHEMICAL_GAUNTLET, GauntletWorkstationScreen::new);

        BlockEntityRendererRegistry.register(ModBlockEntityTypes.POTION_INJECTOR, PotionInjectorRenderer::new);

        ColorProviderRegistry.BLOCK.register((state, reader, pos, index) -> {
            if (pos != null) {
                BlockEntity blockEntity = reader.getBlockEntity(pos);
                if (blockEntity instanceof ReinforcedCauldronBlockEntity) {
                    return ((ReinforcedCauldronBlockEntity) blockEntity).getColor();
                }
            }
            return 0xffffff;
        }, ModBlocks.REINFORCED_WATER_CAULDRON);

       ModelPredicateProviderRegistry.register(ModItems.POTIONEER_GAUNTLET, new Identifier("active"),GAUNTLET
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

        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.AGED_UMBRELLA,createAgedUmbrellaItemStackRenderer());
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.GILDED_UMBRELLA,umbrella("gilded"));
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.WHITE_UMBRELLA,classicUmbrella(DyeColor.WHITE));
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.ORANGE_UMBRELLA,classicUmbrella(DyeColor.ORANGE));
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.MAGENTA_UMBRELLA,classicUmbrella(DyeColor.MAGENTA));
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.LIGHT_BLUE_UMBRELLA,classicUmbrella(DyeColor.LIGHT_BLUE));
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.YELLOW_UMBRELLA,classicUmbrella(DyeColor.YELLOW));
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.GRAY_UMBRELLA,classicUmbrella(DyeColor.GRAY));
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.LIGHT_GRAY_UMBRELLA,classicUmbrella(DyeColor.LIGHT_GRAY));
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.CYAN_UMBRELLA,classicUmbrella(DyeColor.CYAN));
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.PURPLE_UMBRELLA,classicUmbrella(DyeColor.PURPLE));
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.BLUE_UMBRELLA,classicUmbrella(DyeColor.BLUE));
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.BROWN_UMBRELLA,classicUmbrella(DyeColor.BROWN));
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.GREEN_UMBRELLA,classicUmbrella(DyeColor.GREEN));
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.RED_UMBRELLA,classicUmbrella(DyeColor.RED));
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.BLACK_UMBRELLA,classicUmbrella(DyeColor.BLACK));

    }

    public static final UnclampedModelPredicateProvider GAUNTLET = (stack, level, entity, i) -> stack.hasNbt() ? stack.getNbt().getBoolean("active") ? 1 : 0 : 0;


    public static BuiltinItemRendererRegistry.DynamicItemRenderer umbrella(String s) {
        return createGeoClassicUmbrellaItemStackRenderer(s);
    }

    public static BuiltinItemRendererRegistry.DynamicItemRenderer classicUmbrella(DyeColor dyeColor) {
        return umbrella(dyeColor.name().toLowerCase(Locale.ROOT));
    }

    private static BuiltinItemRendererRegistry.DynamicItemRenderer  createGeoClassicUmbrellaItemStackRenderer(String itemName) {
        return new DoubleGeoItemStackRenderer<>(
                GeoItemStackRenderer.GeoItemModel.makeClosedUmbrella(itemName),
                GeoItemStackRenderer.GeoItemModel.makeOpenUmbrella(itemName)
                ,GeoItemStackRenderer.NOTHING,MinecraftClient.getInstance().getBlockEntityRenderDispatcher(), MinecraftClient.getInstance().getEntityModelLoader());
    }

    private static BuiltinItemRendererRegistry.DynamicItemRenderer  createAgedUmbrellaItemStackRenderer() {
        return new DoubleGeoItemStackRenderer<>(
                GeoItemStackRenderer.GeoItemModel.makeClosedUmbrella("aged"),
                GeoItemStackRenderer.GeoItemModel.makeOpenAgedUmbrella()
                ,GeoItemStackRenderer.NOTHING,MinecraftClient.getInstance().getBlockEntityRenderDispatcher(), MinecraftClient.getInstance().getEntityModelLoader());
    }

    public static void switchGameMode(GameMode oldGameType, GameMode newGameType) {
        if (newGameType == GameMode.SURVIVAL && ClothConfig.gauntlet_hud_preset == GauntletHUD.HudPreset.ABOVE_HOTBAR) {
            ClothConfig.gauntlet_hud_x = GauntletHUDMovementScreen.getFixedPositionValue(MinecraftClient.getInstance().getWindow().getScaledHeight() - 42 - 40, false);
        }
        if (newGameType == GameMode.CREATIVE && ClothConfig.gauntlet_hud_preset == GauntletHUD.HudPreset.ABOVE_HOTBAR) {
            ClothConfig.gauntlet_hud_y = GauntletHUDMovementScreen.getFixedPositionValue(MinecraftClient.getInstance().getWindow().getScaledHeight() - 42 - 25, false);
        }
    }

  //  public static void registerLoader(final ModelRegistryEvent event) {
  //      ModelLoaderRegistry.registerLoader(new ResourceLocation(MODID, "fullbright"), ModelLoader.INSTANCE);
 //   }

    public static void onMouseInput(long handle, int button, int action, int mods) {
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;
        ItemStack held = player.getMainHandStack();
        if (held.isEmpty()) return;
        if (held.getItem() instanceof GauntletItem && player.isSneaking()) {
            if (button == GLFW.GLFW_MOUSE_BUTTON_3) {
                GauntletHUDMovementScreen.open();
            }
        }
    }

    public static boolean onMouseScroll(double scrollDelta) {
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return false;
        ItemStack held = player.getMainHandStack();
        if (held.isEmpty()) return false;
        if (held.getItem() instanceof GauntletItem && player.isSneaking()) {
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

    public static void tooltips(ItemStack stack, TooltipContext e2, List<Text> tooltip) {
        if (stack.getItem() instanceof PotionItem) {
            if (Util.isMilkified(stack)) {
                tooltip.add(new LiteralText("Milkified"));
            }
        }

        if (stack.getItem() instanceof ToolItem && PotionUtil.getPotion(stack) != Potions.EMPTY) {
            tooltip.add(new LiteralText("Coated with"));
            PotionUtil.buildTooltip(stack, tooltip, 0.125F);
            tooltip.add(new LiteralText("Uses: " + stack.getNbt().getInt("uses")));
        }
    }

    private static void registerBlockingProperty(Item item) {
        FabricModelPredicateProviderRegistry.register(item, new Identifier("blocking"),
                (stack, world, entity,i) -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F);
    }

    public static void gauntletHud(MatrixStack matrixStack, float tickDelta) {
        // only renders when the hotbar renders
        //            if (Minecraft.getInstance().currentScreen != null) return;
        // get player from client
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;
        ItemStack g = player.getMainHandStack();
        // check if holding gauntlet
        if (g.getItem() instanceof GauntletItem) {
            // get nbt
            NbtCompound info = player.getMainHandStack().getOrCreateNbt().getCompound("info");
            Potion[] potions = GauntletItem.getPotionsFromNBT(info);
            if (MinecraftClient.getInstance().currentScreen instanceof GauntletHUDMovementScreen) return;
            GauntletHUD.render(matrixStack);
            if (potions == null) {
                // reset
                GauntletHUD.init(null, null, null);
                return;
            }
            GauntletHUD.init(potions[0], potions[1], potions[2]);
        }
    }

    public static void playerTick(MinecraftClient e) {
        PlayerEntity player = e.player;
        if (player != null && player.world.getTime() % ClothConfig.particle_drip_rate == 0) {

            ItemStack stack = player.getMainHandStack();

            if (stack.getItem() instanceof ToolItem && PotionUtil.getPotion(stack) != Potions.EMPTY) {


                ParticleEffect particleData = ModParticleTypes.FAST_DRIPPING_WATER;

                Vec3d vec = player.getPos().add(0, +player.getHeight() / 2, 0);

                double yaw = -MathHelper.wrapDegrees(player.getYaw());

                double of1 = Math.random() * .60 + .15;
                double of2 = .40 + Math.random() * .10;


                double z1 = Math.cos(yaw * Math.PI / 180) * of1;
                double x1 = Math.sin(yaw * Math.PI / 180) * of1;

                double z2 = Math.cos((yaw + 270) * Math.PI / 180) * of2;
                double x2 = Math.sin((yaw + 270) * Math.PI / 180) * of2;

                vec = vec.add(x1 + x2, 0, z1 + z2);

                int color = PotionUtil.getColor(stack);
                spawnFluidParticle(MinecraftClient.getInstance().world, vec, particleData, color);
            }
        }
    }

    private static void spawnFluidParticle(ClientWorld world, Vec3d blockPosIn, ParticleEffect particleDataIn, int color) {
        // world.spawnParticle(new BlockPos(blockPosIn), particleDataIn, voxelshape, blockPosIn.getY() +.5);

        Particle particle = ((ParticleManagerAccess) MinecraftClient.getInstance().particleManager).$makeParticle(particleDataIn, blockPosIn.x, blockPosIn.y, blockPosIn.z, 0, -.10, 0);

        float red = (color >> 16 & 0xff) / 255f;
        float green = (color >> 8 & 0xff) / 255f;
        float blue = (color & 0xff) / 255f;

        particle.setColor(red, green, blue);

        MinecraftClient.getInstance().particleManager.addParticle(particle);

        //world.addParticle(particleDataIn,blockPosIn.x,blockPosIn.y,blockPosIn.z,0,-.10,0);
    }

    public static void renderWrappedToolTip(Screen screen, ItemStack stack,MatrixStack matrixStack, List<? extends StringVisitable> tooltips, int mouseX, int mouseY, TextRenderer font) {
        GuiUtils.drawWrappedHoveringText(stack,matrixStack, tooltips, mouseX, mouseY, screen.width, screen.height, -1, font);
    }
}
