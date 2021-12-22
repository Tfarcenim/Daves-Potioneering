package tfar.davespotioneering.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.item.TieredItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.ModConfig;
import tfar.davespotioneering.Util;
import tfar.davespotioneering.blockentity.ReinforcedCauldronBlockEntity;
import tfar.davespotioneering.init.ModBlockEntityTypes;
import tfar.davespotioneering.init.ModBlocks;
import tfar.davespotioneering.init.ModContainerTypes;
import tfar.davespotioneering.init.ModItems;
import tfar.davespotioneering.item.GauntletItem;
import tfar.davespotioneering.net.GauntletCyclePacket;
import tfar.davespotioneering.net.PacketHandler;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static tfar.davespotioneering.DavesPotioneering.MODID;

public class ClientEvents {

    public static void playSound(PlaySoundEvent event) {
        if (event.getName().equals(SoundEvents.BLOCK_BREWING_STAND_BREW.getName().getPath()) && !ModConfig.Client.play_block_brewing_stand_brew.get()) {
            event.setResultSound(null);
        }
    }

    public static void registerLoader(final ModelRegistryEvent event) {
        ModelLoaderRegistry.registerLoader(new ResourceLocation(MODID, "fullbright"), ModelLoader.INSTANCE);
    }

    public static void onMouseInput(InputEvent.MouseInputEvent e) {
        PlayerEntity player = Minecraft.getInstance().player;
        if (player == null) return;
        ItemStack held = player.getHeldItemMainhand();
        if (held.isEmpty()) return;
        if (held.getItem() instanceof GauntletItem && player.isSneaking()) {
            if (e.getButton() == 2) {
                GauntletHUDMovementGui.open();
            }
        }
    }

    public static void onMouseScroll(InputEvent.MouseScrollEvent event) {
        PlayerEntity player = Minecraft.getInstance().player;
        if (player == null) return;
        ItemStack held = player.getHeldItemMainhand();
        if (held.isEmpty()) return;
        if (held.getItem() instanceof GauntletItem && player.isSneaking()) {
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
                e.getToolTip().add(new StringTextComponent("Milkified"));
            }
        }

        if (stack.getItem() instanceof TieredItem && PotionUtils.getPotionFromItem(stack) != Potions.EMPTY) {
            e.getToolTip().add(new StringTextComponent("Coated with"));
            PotionUtils.addPotionTooltip(stack, e.getToolTip(), 0.125F);
            e.getToolTip().add(new StringTextComponent("Uses: " + stack.getTag().getInt("uses")));
        }
    }

    public static void doClientStuff(final FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.addListener(ClientEvents::playSound);
        MinecraftForge.EVENT_BUS.addListener(ClientEvents::tooltips);
        MinecraftForge.EVENT_BUS.addListener(ClientEvents::onMouseInput);
        MinecraftForge.EVENT_BUS.addListener(ClientEvents::onMouseScroll);
        MinecraftForge.EVENT_BUS.addListener(ClientEvents::gauntletHud);
        RenderTypeLookup.setRenderLayer(ModBlocks.ADVANCED_BREWING_STAND, RenderType.getCutoutMipped());
        ScreenManager.registerFactory(ModContainerTypes.ADVANCED_BREWING_STAND, AdvancedBrewingStandScreen::new);
        ScreenManager.registerFactory(ModContainerTypes.ALCHEMICAL_GAUNTLET, GauntletWorkstationScreen::new);

        ClientRegistry.bindTileEntityRenderer(ModBlockEntityTypes.POTION_INJECTOR, PotionInjectorRenderer::new);

        Minecraft.getInstance().getBlockColors().register((state, reader, pos, index) -> {
            if (pos != null) {
                TileEntity blockEntity = reader.getTileEntity(pos);
                if (blockEntity instanceof ReinforcedCauldronBlockEntity) {
                    return ((ReinforcedCauldronBlockEntity) blockEntity).getColor();
                }
            }
            return 0xffffff;
        }, ModBlocks.REINFORCED_CAULDRON);

        ItemModelsProperties.registerProperty(ModItems.ALCHEMICAL_GAUNTLET, new ResourceLocation("active"),
                (ItemStack a, ClientWorld b, LivingEntity c) -> a.hasTag() ? a.getTag().getBoolean("active") ? 1 : 0 : 0);

    }

    public static void gauntletHud(RenderGameOverlayEvent.Post e) {
        // only renders when the hotbar renders
        if (e.getType() == RenderGameOverlayEvent.ElementType.HOTBAR) {
//            if (Minecraft.getInstance().currentScreen != null) return;
            // get player from client
            PlayerEntity player = Minecraft.getInstance().player;
            if (player == null) return;
            ItemStack g = player.getHeldItemMainhand();
            // check if holding gauntlet
            if (g.getItem() instanceof GauntletItem) {
                // get nbt
                CompoundNBT info = player.getHeldItemMainhand().getOrCreateTag().getCompound("info");
                Potion[] potions = GauntletItem.getPotionsFromNBT(info);
                if (Minecraft.getInstance().currentScreen instanceof GauntletHUDMovementGui) return;
                GauntletHUD.hudInstance.render(e.getMatrixStack());
                if (potions == null) {
                    // reset
                    GauntletHUD.hudInstance.init(null, null, null);
                    return;
                }
                GauntletHUD.hudInstance.init(potions[0], potions[1], potions[2]);
            }
        }
    }
}
