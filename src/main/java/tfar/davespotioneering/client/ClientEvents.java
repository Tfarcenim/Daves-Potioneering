package tfar.davespotioneering.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.ModConfig;
import tfar.davespotioneering.Util;
import tfar.davespotioneering.blockentity.ReinforcedCauldronBlockEntity;
import tfar.davespotioneering.client.particle.FastDripParticle;
import tfar.davespotioneering.client.particle.TintedSplashParticle;
import tfar.davespotioneering.init.*;
import tfar.davespotioneering.item.GauntletItem;
import tfar.davespotioneering.mixin.ParticleManagerAccess;
import tfar.davespotioneering.net.GauntletCyclePacket;
import tfar.davespotioneering.net.PacketHandler;

public class ClientEvents {

    public static void particle(RegisterParticleProvidersEvent e) {

        ParticleEngine manager = Minecraft.getInstance().particleEngine;

        manager.register(ModParticleTypes.FAST_DRIPPING_WATER, FastDripParticle.DrippingWaterFactory::new);
        manager.register(ModParticleTypes.FAST_FALLING_WATER, FastDripParticle.FallingWaterFactory::new);
        manager.register(ModParticleTypes.TINTED_SPLASH, TintedSplashParticle.Factory::new);
    }

    public static void registerLoader(final ModelEvent.RegisterGeometryLoaders event) {
        event.register("fullbright", ModelLoader.INSTANCE);
    }

    public static void onMouseInput(InputEvent.MouseButton e) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        ItemStack held = player.getMainHandItem();
        if (held.isEmpty()) return;
        if (held.getItem() instanceof GauntletItem && player.isShiftKeyDown()) {
            if (e.getButton() == 2) {
                GauntletHUDMovementGui.open();
            }
        }
    }

    public static void onMouseScroll(InputEvent.MouseScrollingEvent event) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        ItemStack held = player.getMainHandItem();
        if (held.isEmpty()) return;
        if (held.getItem() instanceof GauntletItem && player.isShiftKeyDown()) {
            if (event.getScrollDelta() == 1.f) {
                PacketHandler.sendToServer(new GauntletCyclePacket(true));
                GauntletHUD.backwardCycle();
            } else {
                PacketHandler.sendToServer(new GauntletCyclePacket(false));
                GauntletHUD.forwardCycle();
            }
            event.setCanceled(true);
        }
    }

    public static void tooltips(ItemTooltipEvent e) {
        ItemStack stack = e.getItemStack();
        if (stack.getItem() instanceof PotionItem) {
            if (Util.isMilkified(stack)) {
                e.getToolTip().add(Component.literal("Milkified"));
            }
        }

        if (stack.getItem() instanceof TieredItem && PotionUtils.getPotion(stack) != Potions.EMPTY) {
            e.getToolTip().add(Component.literal("Coated with"));
            PotionUtils.addPotionTooltip(stack, e.getToolTip(), 0.125F);
            e.getToolTip().add(Component.literal("Uses: " + stack.getTag().getInt("uses")));
        }
    }

    public static void doClientStuff(final FMLClientSetupEvent event) {



        MinecraftForge.EVENT_BUS.addListener(ClientEvents::tooltips);
        MinecraftForge.EVENT_BUS.addListener(ClientEvents::onMouseInput);
        MinecraftForge.EVENT_BUS.addListener(ClientEvents::onMouseScroll);
        MinecraftForge.EVENT_BUS.addListener(ClientEvents::playerTick);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.COMPOUND_BREWING_STAND, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.POTION_INJECTOR,RenderType.translucent());
        MenuScreens.register(ModMenuTypes.ADVANCED_BREWING_STAND, AdvancedBrewingStandScreen::new);
        MenuScreens.register(ModMenuTypes.ALCHEMICAL_GAUNTLET, GauntletWorkstationScreen::new);

        BlockEntityRenderers.register(ModBlockEntityTypes.POTION_INJECTOR, PotionInjectorRenderer::new);

        Minecraft.getInstance().getBlockColors().register((state, reader, pos, index) -> {
            if (pos != null) {
                BlockEntity blockEntity = reader.getBlockEntity(pos);
                if (blockEntity instanceof ReinforcedCauldronBlockEntity reinforced)
                    return reinforced.getColor();
            }
            return 0xffffff;
        }, ModBlocks.REINFORCED_WATER_CAULDRON);

        ItemProperties.register(ModItems.POTIONEER_GAUNTLET, new ResourceLocation("active"),
                (ItemStack a, ClientLevel b, LivingEntity c,int i) -> a.hasTag() ? a.getTag().getBoolean("active") ? 1 : 0 : 0);

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
    }

    public static void overlay(RegisterGuiOverlaysEvent e) {
        e.registerBelow(VanillaGuiOverlay.CHAT_PANEL.id(),DavesPotioneering.MODID,new GauntletHUD());
    }

    private static void registerBlockingProperty(Item item) {
        ItemProperties.register(item, new ResourceLocation("blocking"),
                (stack, world, entity,i) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);
    }

    public static void playerTick(TickEvent.PlayerTickEvent e) {
        Player player = e.player;
        if (e.phase == TickEvent.Phase.END && e.side == LogicalSide.CLIENT && player.level.getGameTime() % ModConfig.Client.particle_drip_rate.get() == 0) {

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
