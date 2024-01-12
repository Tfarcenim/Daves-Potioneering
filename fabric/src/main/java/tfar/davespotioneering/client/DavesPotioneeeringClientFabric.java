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
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import tfar.davespotioneering.DavesPotioneeringClient;
import tfar.davespotioneering.client.model.gecko.DoubleGeoItemStackRenderer;
import tfar.davespotioneering.client.model.gecko.GeoItemModel;
import tfar.davespotioneering.client.particle.FastDripParticle;
import tfar.davespotioneering.client.particle.TintedSplashParticle;
import tfar.davespotioneering.client.renderer.PotionInjectorRenderer;
import tfar.davespotioneering.client.screens.AdvancedBrewingStandScreen;
import tfar.davespotioneering.client.screens.PotionInjectorScreen;
import tfar.davespotioneering.init.ModBlockEntityTypes;
import tfar.davespotioneering.init.ModBlocks;
import tfar.davespotioneering.init.ModMenuTypes;
import tfar.davespotioneering.init.ModParticleTypes;
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

    public static void onMouseInput(int button) {
        DavesPotioneeringClient.onMouseInput(button);
    }

    public static boolean onMouseScroll(double scrollDelta) {
        return DavesPotioneeringClient.onMouseScroll(scrollDelta);
    }

    public static void tooltips(ItemStack stack, TooltipFlag e2, List<Component> tooltip) {
        DavesPotioneeringClient.tooltips(stack,tooltip);
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
