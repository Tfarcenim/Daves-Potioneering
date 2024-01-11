package tfar.davespotioneering.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.glfw.GLFW;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import tfar.davespotioneering.DavesPotioneeringClient;
import tfar.davespotioneering.DavesPotioneeringFabric;
import tfar.davespotioneering.block.CLayeredReinforcedCauldronBlock;
import tfar.davespotioneering.client.model.gecko.DoubleGeoItemStackRenderer;
import tfar.davespotioneering.client.model.gecko.GeoItemModel;
import tfar.davespotioneering.client.particle.FastDripParticle;
import tfar.davespotioneering.client.particle.TintedSplashParticle;
import tfar.davespotioneering.init.ModBlockEntityTypes;
import tfar.davespotioneering.init.ModBlocks;
import tfar.davespotioneering.init.ModMenuTypes;
import tfar.davespotioneering.init.ModParticleTypes;
import tfar.davespotioneering.item.GauntletItemFabric;
import tfar.davespotioneering.mixin.ParticleManagerAccess;
import tfar.davespotioneering.net.C2SGauntletCyclePacket;
import tfar.davespotioneering.net.ClientPacketHandler;

import java.util.List;
import java.util.Locale;

public class DavesPotioneeeringClientFabric implements ClientModInitializer {


    @Override
    public void onInitializeClient() {

        ParticleFactoryRegistry.getInstance().register(ModParticleTypes.FAST_DRIPPING_WATER,FastDripParticle.DrippingWaterFactory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticleTypes.FAST_FALLING_WATER, FastDripParticle.FallingWaterFactory::new);
       ParticleFactoryRegistry.getInstance().register(ModParticleTypes.TINTED_SPLASH, TintedSplashParticle.Factory::new);

        KeyBindingHelper.registerKeyBinding(DavesPotioneeringClient.CONFIG_KEY);

        ItemTooltipCallback.EVENT.register(DavesPotioneeeringClientFabric::tooltips);
        HudRenderCallback.EVENT.register(DavesPotioneeeringClientFabric::gauntletHud);
        ClientTickEvents.START_CLIENT_TICK.register(DavesPotioneeeringClientFabric::playerTick);



        ColorProviderRegistry.BLOCK.register(DavesPotioneeringClient.CAULDRON, ModBlocks.REINFORCED_WATER_CAULDRON);

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.COMPOUND_BREWING_STAND, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.POTION_INJECTOR,RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.REINFORCED_WATER_CAULDRON,RenderType.translucent());

        MenuScreens.register(ModMenuTypes.ADVANCED_BREWING_STAND, AdvancedBrewingStandScreen::new);
        MenuScreens.register(ModMenuTypes.ALCHEMICAL_GAUNTLET, PotionInjectorScreen::new);

        BlockEntityRenderers.register(ModBlockEntityTypes.POTION_INJECTOR, PotionInjectorRenderer::new);
        //BuiltinItemRendererRegistry.INSTANCE.register(ModItems.AGED_UMBRELLA,createAgedUmbrellaItemStackRenderer());
        DavesPotioneeringClient.clientSetup();
        ClientPacketHandler.registerClientMessages();
    }


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

    public static void onMouseInput(long handle, int button, int action, int mods) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        ItemStack held = player.getMainHandItem();
        if (held.isEmpty()) return;
        if (held.getItem() instanceof GauntletItemFabric && player.isShiftKeyDown()) {
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
        if (held.getItem() instanceof GauntletItemFabric && player.isShiftKeyDown()) {
            if (scrollDelta == 1.f) {
                C2SGauntletCyclePacket.encode(true);
                GauntletHUDCommon.backwardCycle();
            } else {
                C2SGauntletCyclePacket.encode(false);
                GauntletHUDCommon.forwardCycle();
            }
            return true;
        }
        return false;
    }

    public static void tooltips(ItemStack stack, TooltipFlag e2, List<Component> tooltip) {
        if (!dontTooltip(stack) && PotionUtils.getPotion(stack) != Potions.EMPTY) {
            if (stack.getItem().isEdible()) {
                if (DavesPotioneeringFabric.CONFIG.show_spiked_food) {
                    tooltip.add(Component.literal("Spiked with"));
                    PotionUtils.addPotionTooltip(stack, tooltip, 0.125F);
                }
            } else {
                tooltip.add(Component.literal("Coated with"));
                PotionUtils.addPotionTooltip(stack, tooltip, 0.125F);
                tooltip.add(Component.literal("Uses: " + stack.getTag().getInt(CLayeredReinforcedCauldronBlock.USES)));
            }
        }
    }

    public static boolean dontTooltip(ItemStack stack) {
        return stack.getItem() instanceof PotionItem || stack.getItem() instanceof ArrowItem;
    }



    public static void gauntletHud(GuiGraphics matrixStack, float tickDelta) {
        Gui gui = GauntletHUDCommon.mc.gui;
        int screenWidth = GauntletHUDCommon.mc.getWindow().getGuiScaledWidth();
        int screenHeight = GauntletHUDCommon.mc.getWindow().getGuiScaledHeight();
        GauntletHUDCommon.render(gui,matrixStack,tickDelta,screenWidth,screenHeight);
    }

    public static void playerTick(Minecraft e) {
        Player player = e.player;
        if (player != null) {
            DavesPotioneeringClient.clientPlayerTick(player);
        }
    }
}
