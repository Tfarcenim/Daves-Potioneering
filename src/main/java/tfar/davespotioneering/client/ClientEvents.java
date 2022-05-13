package tfar.davespotioneering.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.glfw.GLFW;
import software.bernie.geckolib3.renderer.geo.GeoItemRenderer;
import tfar.davespotioneering.ModConfig;
import tfar.davespotioneering.Util;
import tfar.davespotioneering.blockentity.ReinforcedCauldronBlockEntity;
import tfar.davespotioneering.client.model.gecko.DoubleGeoItemStackRenderer;
import tfar.davespotioneering.client.model.gecko.GeoItemStackRenderer;
import tfar.davespotioneering.client.particle.FastDripParticle;
import tfar.davespotioneering.client.particle.TintedSplashParticle;
import tfar.davespotioneering.init.*;
import tfar.davespotioneering.item.GauntletItem;
import tfar.davespotioneering.mixin.ParticleManagerAccess;
import tfar.davespotioneering.net.C2SGauntletCyclePacket;
import tfar.davespotioneering.net.PacketHandler;

import java.util.List;
import java.util.Locale;

public class ClientEvents implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        ParticleFactoryRegistry.getInstance().register(ModParticleTypes.FAST_DRIPPING_WATER,FastDripParticle.DrippingWaterFactory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticleTypes.FAST_FALLING_WATER,FastDripParticle.FallingWaterFactory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticleTypes.TINTED_SPLASH,TintedSplashParticle.Factory::new);

        ItemTooltipCallback.EVENT.register(ClientEvents::tooltips);
        HudRenderCallback.EVENT.register(ClientEvents::gauntletHud);
        ClientTickEvents.START_CLIENT_TICK.register(ClientEvents::playerTick);


        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.COMPOUND_BREWING_STAND, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.POTION_INJECTOR,RenderType.translucent());
        ScreenRegistry.register(ModContainerTypes.ADVANCED_BREWING_STAND, AdvancedBrewingStandScreen::new);
        ScreenRegistry.register(ModContainerTypes.ALCHEMICAL_GAUNTLET, GauntletWorkstationScreen::new);

        BlockEntityRendererRegistry.INSTANCE.register(ModBlockEntityTypes.POTION_INJECTOR, PotionInjectorRenderer::new);

        Minecraft.getInstance().getBlockColors().register((state, reader, pos, index) -> {
            if (pos != null) {
                BlockEntity blockEntity = reader.getBlockEntity(pos);
                if (blockEntity instanceof ReinforcedCauldronBlockEntity) {
                    return ((ReinforcedCauldronBlockEntity) blockEntity).getColor();
                }
            }
            return 0xffffff;
        }, ModBlocks.REINFORCED_CAULDRON);

        FabricModelPredicateProviderRegistry.register(ModItems.POTIONEER_GAUNTLET, new ResourceLocation("active"),
                (ItemStack a, ClientLevel b, LivingEntity c) -> a.hasTag() ? a.getTag().getBoolean("active") ? 1 : 0 : 0);

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

        GeoItemRenderer.registerItemRenderer(ModItems.AGED_UMBRELLA,createAgedUmbrellaItemStackRenderer());

    }

    private static BlockEntityWithoutLevelRenderer createGeoClassicUmbrellaItemStackRenderer(DyeColor color) {
        return createGeoClassicUmbrellaItemStackRenderer(color.name().toLowerCase(Locale.ROOT));
    }

    private static BlockEntityWithoutLevelRenderer createGeoClassicUmbrellaItemStackRenderer(String itemName) {
        return new DoubleGeoItemStackRenderer<>(
                GeoItemStackRenderer.GeoItemModel.makeClosedUmbrella(itemName),
                GeoItemStackRenderer.GeoItemModel.makeOpenUmbrella(itemName)
                ,GeoItemStackRenderer.NOTHING);
    }

    private static GeoItemRenderer createAgedUmbrellaItemStackRenderer() {
        return new DoubleGeoItemStackRenderer(
                GeoItemStackRenderer.GeoItemModel.makeClosedUmbrella("aged"),
                GeoItemStackRenderer.GeoItemModel.makeOpenAgedUmbrella()
                ,GeoItemStackRenderer.NOTHING);
    }

    private static BlockEntityWithoutLevelRenderer createGeoItemStackRendererTransparent(ResourceLocation itemName) {
        return new GeoItemStackRenderer<>(new GeoItemStackRenderer.GeoItemModel<>(itemName), RenderType::entityTranslucent,GeoItemStackRenderer.NOTHING);
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
                GauntletHUDMovementGui.open();
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
        if (stack.getItem() instanceof PotionItem) {
            if (Util.isMilkified(stack)) {
                tooltip.add(new TextComponent("Milkified"));
            }
        }

        if (stack.getItem() instanceof TieredItem && PotionUtils.getPotion(stack) != Potions.EMPTY) {
            tooltip.add(new TextComponent("Coated with"));
            PotionUtils.addPotionTooltip(stack, tooltip, 0.125F);
            tooltip.add(new TextComponent("Uses: " + stack.getTag().getInt("uses")));
        }
    }

    private static void registerBlockingProperty(Item item) {
        FabricModelPredicateProviderRegistry.register(item, new ResourceLocation("blocking"),
                (stack, world, entity) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);
    }

    public static void gauntletHud(PoseStack matrixStack, float tickDelta) {
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
            if (Minecraft.getInstance().screen instanceof GauntletHUDMovementGui) return;
            GauntletHUD.hudInstance.render(matrixStack);
            if (potions == null) {
                // reset
                GauntletHUD.hudInstance.init(null, null, null);
                return;
            }
            GauntletHUD.hudInstance.init(potions[0], potions[1], potions[2]);
        }
    }

    public static void playerTick(Minecraft e) {
        Player player = e.player;
        if (player.level.getGameTime() % ModConfig.Client.particle_drip_rate == 0) {

            ItemStack stack = player.getMainHandItem();

            if (stack.getItem() instanceof TieredItem && PotionUtils.getPotion(stack) != Potions.EMPTY) {


                ParticleOptions particleData = ModParticleTypes.FAST_DRIPPING_WATER;

                Vec3 vec = player.position().add(0, +player.getBbHeight() / 2, 0);

                double yaw = -Mth.wrapDegrees(player.yRot);

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
