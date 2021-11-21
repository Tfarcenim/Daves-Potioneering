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
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ClientEvents {

    public static void playSound(PlaySoundEvent event) {
        if (event.getName().equals(SoundEvents.BLOCK_BREWING_STAND_BREW.getName().getPath()) && !ModConfig.Client.play_block_brewing_stand_brew.get()) {
            event.setResultSound(null);
        }
    }

    public static void onKeyPress(InputEvent.KeyInputEvent e) {
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
            e.getToolTip().add(new StringTextComponent("Uses: "+stack.getTag().getInt("uses")));
        }
    }

    public static void doClientStuff(final FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.addListener(ClientEvents::playSound);
        MinecraftForge.EVENT_BUS.addListener(ClientEvents::tooltips);
        MinecraftForge.EVENT_BUS.addListener(ClientEvents::onKeyPress);
        RenderTypeLookup.setRenderLayer(ModBlocks.ADVANCED_BREWING_STAND, RenderType.getCutoutMipped());
        ScreenManager.registerFactory(ModContainerTypes.ADVANCED_BREWING_STAND, AdvancedBrewingStandScreen::new);
        ScreenManager.registerFactory(ModContainerTypes.ALCHEMICAL_GAUNTLET, GauntletWorkstationScreen::new);

        ClientRegistry.bindTileEntityRenderer(ModBlockEntityTypes.POTION_INJECTOR,PotionInjectorRenderer::new);

        Minecraft.getInstance().getBlockColors().register((state, reader, pos, index) -> {
            if (pos != null) {
                TileEntity blockEntity = reader.getTileEntity(pos);
                if (blockEntity instanceof ReinforcedCauldronBlockEntity) {
                    return ((ReinforcedCauldronBlockEntity)blockEntity).getColor();
                }
            }
            return 0xffffff;
        },ModBlocks.REINFORCED_CAULDRON);

        ItemModelsProperties.registerProperty(ModItems.ALCHEMICAL_GAUNTLET,new ResourceLocation("active"),
                (ItemStack a, ClientWorld b, LivingEntity c) -> {
           return a.hasTag() ? a.getTag().getBoolean("active") ? 1 : 0: 0;
        });

    }

    public static void onBakeModels(ModelBakeEvent event) {
        // we want to replace some of the regular baked block models with models that
        // have emissive/fullbright textures
        Map<ResourceLocation, IBakedModel> modelRegistry = event.getModelRegistry();

        // the model registry uses ModelResourceLocations that can't easily be compared
        // to regular resource locations
        // they have an additional field for the blockstate properties of a blockstate
        // so we need to replace models on a per-blockstate bases

        // we need to use existing models to create our enhanced models, so we'll need to make sure they're in the registry first and get them
        // let's make a reusable model override function
        // the resourcelocations we specify in the FullbrightBakedModel constructor are *texture* locations

        List<ResourceLocation> possibilities =
                modelRegistry.keySet().stream().filter(m -> m.getPath().contains("alchemical")).collect(Collectors.toList());

        IBakedModel litGauntlet3dModel = modelRegistry.get(
                new ModelResourceLocation(new ResourceLocation(DavesPotioneering.MODID,"alchemical_gauntlet"),"inventory"
        ));

        if (litGauntlet3dModel != null) {

        }

        // now we get all the blockstates from our block, narrow them down to the only ones we want to have fullbright textures,
        // and replace the models with fullbright-enabled models

    }

    public static void gauntletHud(RenderGameOverlayEvent.Post e) {
        if (e.getType() == RenderGameOverlayEvent.ElementType.HOTBAR) {
            PlayerEntity player = Minecraft.getInstance().player;
            ItemStack g = player.getHeldItemMainhand();
            if (g.getItem() instanceof GauntletItem) {

            }
        }
    }
}
