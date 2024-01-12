package tfar.davespotioneering.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.DavesPotioneeringClient;
import tfar.davespotioneering.ModConfig;
import tfar.davespotioneering.Util;
import tfar.davespotioneering.client.particle.FastDripParticle;
import tfar.davespotioneering.client.particle.TintedSplashParticle;
import tfar.davespotioneering.client.renderer.PotionInjectorRenderer;
import tfar.davespotioneering.client.screens.AdvancedBrewingStandScreen;
import tfar.davespotioneering.client.screens.PotionInjectorScreen;
import tfar.davespotioneering.init.ModBlockEntityTypes;
import tfar.davespotioneering.init.ModBlocks;
import tfar.davespotioneering.init.ModMenuTypes;
import tfar.davespotioneering.init.ModParticleTypes;

public class DavesPotioneeringClientForge {

    public static void particle(RegisterParticleProvidersEvent e) {

        ParticleEngine manager = Minecraft.getInstance().particleEngine;

        manager.register(ModParticleTypes.FAST_DRIPPING_WATER, FastDripParticle.DrippingWaterFactory::new);
        manager.register(ModParticleTypes.FAST_FALLING_WATER, FastDripParticle.FallingWaterFactory::new);
        manager.register(ModParticleTypes.TINTED_SPLASH, TintedSplashParticle.Factory::new);
    }

    public static void registerLoader(final ModelEvent.RegisterGeometryLoaders event) {
      //  event.register("fullbright", ModelLoader.INSTANCE);
    }

    public static void onMouseInput(InputEvent.MouseButton e) {
        DavesPotioneeringClient.onMouseInput(e.getButton());
    }

    public static void onMouseScroll(InputEvent.MouseScrollingEvent event) {
        boolean cancel = DavesPotioneeringClient.onMouseScroll(event.getScrollDelta());
        if (cancel) event.setCanceled(true);
    }

    public static void tooltips(ItemTooltipEvent e) {
        DavesPotioneeringClient.tooltips(e.getItemStack(),e.getToolTip());
    }

    public static void doClientStuff(final FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.addListener(DavesPotioneeringClientForge::tooltips);
        MinecraftForge.EVENT_BUS.addListener(DavesPotioneeringClientForge::onMouseInput);
        MinecraftForge.EVENT_BUS.addListener(DavesPotioneeringClientForge::onMouseScroll);
        MinecraftForge.EVENT_BUS.addListener(DavesPotioneeringClientForge::playerTick);
        MinecraftForge.EVENT_BUS.addListener(DavesPotioneeringClientForge::stackAdj1);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.COMPOUND_BREWING_STAND, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.POTION_INJECTOR,RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.REINFORCED_WATER_CAULDRON,RenderType.translucent());
        MenuScreens.register(ModMenuTypes.ADVANCED_BREWING_STAND, AdvancedBrewingStandScreen::new);
        MenuScreens.register(ModMenuTypes.ALCHEMICAL_GAUNTLET, PotionInjectorScreen::new);

        BlockEntityRenderers.register(ModBlockEntityTypes.POTION_INJECTOR, PotionInjectorRenderer::new);

        Minecraft.getInstance().getBlockColors().register(DavesPotioneeringClient.CAULDRON,ModBlocks.REINFORCED_WATER_CAULDRON);
        DavesPotioneeringClient.clientSetup();
    }

    public static final IGuiOverlay OVERLAY = GauntletHUDCommon::render;

    public static void overlay(RegisterGuiOverlaysEvent e) {
        e.registerBelow(VanillaGuiOverlay.CHAT_PANEL.id(), DavesPotioneering.MODID,OVERLAY);
    }

    private static void stackAdj1(ClientPlayerNetworkEvent.LoggingIn e) {
        Util.setStackSize(Items.POTION, ModConfig.Server.potion_stack_size.get());
        Util.setStackSize(Items.SPLASH_POTION, ModConfig.Server.splash_potion_stack_size.get());
        Util.setStackSize(Items.LINGERING_POTION, ModConfig.Server.lingering_potion_stack_size.get());
    }

    public static void playerTick(TickEvent.PlayerTickEvent e) {
        Player player = e.player;
        if (player != null && e.phase == TickEvent.Phase.END && e.side == LogicalSide.CLIENT) {
            DavesPotioneeringClient.clientPlayerTick(player);
        }
    }
}
